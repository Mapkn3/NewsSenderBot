public class VkGroupEvent {
    private EventType type;
    private BotUser user;
    private String text;

    public VkGroupEvent(EventType type, BotUser user, String text) {
        this.type = type;
        this.user = user;
        this.text = text;
    }

    public EventType getType() {
        return type;
    }

    public BotUser getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "VkGroupEvent{" +
                "type=" + type +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }

    enum EventType {
        MESSAGE_NEW,
        WALL_POST_NEW,
        UNKNOWN
    }
}
