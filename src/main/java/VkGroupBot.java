import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VkGroupBot implements Bot {
    private Integer groupId;
    private String accessToken;
    private VkApiClient vk;
    private GroupActor groupActor;
    private boolean isConnected;
    private VkGroupLongPoll longPoll;
    private List<VkGroupEvent> lastEvents;

    public VkGroupBot(Integer groupId, String accessToken) {
        this.groupId = groupId;
        this.accessToken = accessToken;
        this.lastEvents = new ArrayList<>();
        connect();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public boolean connect() {
        try {
            vk = new VkApiClient(HttpTransportClient.getInstance());
            groupActor = new GroupActor(groupId, accessToken);
            longPoll = new VkGroupLongPoll(groupActor);
            isConnected = true;
        } catch (Exception e) {
            System.out.println(e);
            isConnected = false;
        }
        return isConnected;
    }

    @Override
    public void sendMessage(BotMessage msg) {
        if (isConnected) {
            try {
                vk.messages().send(groupActor).userId(msg.getToUser().getId()).message(msg.getMsg()).execute();
            } catch (ApiException | ClientException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<BotMessage> getUnreadMessages() {
        loadLastEvents(25);
        List<BotMessage> result = lastEvents.stream()
                .filter((event) -> event.getType() == VkGroupEvent.EventType.MESSAGE_NEW)
                .map((event) -> new BotMessage(event.getText(), event.getUser(), new BotUser(groupId)))
                .collect(Collectors.toList());
        lastEvents = lastEvents.stream()
                .filter((event) -> event.getType() != VkGroupEvent.EventType.MESSAGE_NEW)
                .collect(Collectors.toList());
        return result;
    }

    public List<VkGroupEvent> getNewWallPosts() {
        loadLastEvents(25);
        List<VkGroupEvent> result = lastEvents.stream()
                .filter((event) -> event.getType() == VkGroupEvent.EventType.WALL_POST_NEW)
                .collect(Collectors.toList());
        lastEvents = lastEvents.stream()
                .filter((event) -> event.getType() != VkGroupEvent.EventType.WALL_POST_NEW)
                .collect(Collectors.toList());
        return result;
    }

    public List<VkGroupEvent> getLastEvents() {
        return lastEvents;
    }

    public void loadLastEvents(int time) {
        try {
            lastEvents.addAll(longPoll.getLastEvents(time));
        } catch (ClientException | ApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "VkGroupBot{" +
                "groupId=" + groupId +
                ", lastEvents=" + lastEvents +
                '}';
    }
}
