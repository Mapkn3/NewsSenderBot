public class BotUser {
    private Integer id;

    public BotUser(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "BotUser{" +
                "id=" + id +
                '}';
    }
}
