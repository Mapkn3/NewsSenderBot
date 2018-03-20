import java.util.List;

public interface Bot {
    void sendMessage(BotMessage msg);
    List<BotMessage> getUnreadMessages();
}
