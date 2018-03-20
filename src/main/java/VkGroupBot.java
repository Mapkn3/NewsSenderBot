import com.vk.api.sdk.actions.LongPoll;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;

import java.util.List;

public class VkGroupBot implements Bot {
    private Integer appId;
    private String appSecret;
    private String redirectUri;
    private Integer groupId;
    private String accessToken;
    private VkApiClient vk;
    private GroupActor groupActor;
    private boolean isConnected;

    public VkGroupBot(Integer appId, String appSecret, String redirectUri, Integer groupId, String accessToken) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.redirectUri = redirectUri;
        this.groupId = groupId;
        this.accessToken = accessToken;
        connect();
    }

    public Integer getGroupId() {
        return this.groupId;
    }

    public boolean connect() {
        try {
            this.vk = new VkApiClient(HttpTransportClient.getInstance());
            this.groupActor = new GroupActor(groupId, accessToken);
            this.isConnected = true;
        } catch (Exception e) {
            System.out.println(e);
            this.isConnected = false;
        }
        return isConnected;
    }

    @Override
    public void sendMessage(BotMessage msg) {
        if (this.isConnected) {
            try {
                vk.messages().send(this.groupActor).userId(msg.getToUser().getId()).message(msg.getMsg()).execute();
            } catch (ApiException | ClientException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<BotMessage> getUnreadMessages() {
        try {
            GetLongPollServerResponse response = vk.groups().getLongPollServer(groupActor).execute();
            LongPoll longPoll = new LongPoll(vk);
            System.out.println(longPoll.getEvents(response.getServer(), response.getKey(), response.getTs()).waitTime(25).execute());
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
        return null;
    }
}
