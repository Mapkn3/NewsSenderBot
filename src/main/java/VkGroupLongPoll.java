import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.api.sdk.actions.LongPoll;
import com.vk.api.sdk.callback.longpoll.responses.GetLongPollEventsResponse;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.groups.responses.GetLongPollServerResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VkGroupLongPoll {
    private LongPoll longPoll;
    private String server;
    private String key;
    private Integer ts;

    public VkGroupLongPoll(GroupActor groupActor) throws ClientException, ApiException {
        VkApiClient vk = new VkApiClient(HttpTransportClient.getInstance());
        GetLongPollServerResponse response = vk.groups().getLongPollServer(groupActor).execute();
        this.longPoll = new LongPoll(vk);
        this.server = response.getServer();
        this.key = response.getKey();
        this.ts = response.getTs();
    }

    public List<VkGroupEvent> getLastEvents(int waitTime) throws ClientException, ApiException {
        GetLongPollEventsResponse response = longPoll.getEvents(server, key, ts).waitTime(waitTime).execute();
        ts = response.getTs();
        List<JsonObject> updates = response.getUpdates();
        List<VkGroupEvent> events = new ArrayList<>();
        for (JsonObject update : updates) {
            VkGroupEvent.EventType type = parseEventType(update.get("type").getAsString());
            BotUser user = parseUserId(update.get("object"));
            String text = parseText(update.get("object"));
            events.add(new VkGroupEvent(type, user, text));
        }
        return events;
    }

    public VkGroupEvent.EventType parseEventType(String type) {
        switch (type) {
            case "message_new":
                return VkGroupEvent.EventType.MESSAGE_NEW;
            case "wall_post_new":
                return VkGroupEvent.EventType.WALL_POST_NEW;
            default:
                return VkGroupEvent.EventType.UNKNOWN;
        }
    }

    public String getValueForPossibleKey(JsonElement element, List<String> possibleKeys) {
        JsonObject object = element.getAsJsonObject();
        String result = null;
        for (String key : possibleKeys) {
            if (object.has(key)) {
                result = object.get(key).getAsString();
            }
        }
        return result;
    }

    public BotUser parseUserId(JsonElement element) {
        String string_id = getValueForPossibleKey(element, Arrays.asList("user_id", "created_by"));
        return new BotUser(Integer.parseInt(string_id));
    }

    public String parseText(JsonElement element) {
        return getValueForPossibleKey(element, Arrays.asList("body", "text"));
    }
}
