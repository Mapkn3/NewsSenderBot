public class BotMessage {
    private String msg;
    private BotUser fromUser;
    private BotUser toUser;

    public BotMessage(String msg, BotUser fromUser, BotUser toUser) {
        this.msg = msg;
        this.fromUser = fromUser;
        this.toUser = toUser;
    }

    public String getMsg() {
        return this.msg;
    }

    public BotUser getFromUser() {
        return this.fromUser;
    }

    public BotUser getToUser() {
        return this.toUser;
    }
}
