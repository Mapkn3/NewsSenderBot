import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties properties = loadConfiguration();
        Integer appId = Integer.valueOf(properties.getProperty("app.id"));
        String appSecret = properties.getProperty("app.secret");
        String redirectUri = properties.getProperty("redirect.uri");
        Integer groupId = Integer.valueOf(properties.getProperty("group.id"));
        String accessToken = properties.getProperty("group.access.token");
        VkGroupBot bot = new VkGroupBot(appId, appSecret, redirectUri, groupId, accessToken);
        //bot.sendMessage(new BotMessage("Hello!", new BotUser(bot.getGroupId()), new BotUser(64099040)));
        bot.getUnreadMessages();
    }
    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = Main.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return properties;
    }
}
