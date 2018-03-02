package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Random;

import static aurora.utils.SendMentionMessage.sendMentionMessage;

/**
 * Created by Tyler on 1/17/2017.
 */
public class Should {
    public static void should(MessageChannel channel, Message message) {
        if (new Random().nextBoolean()) {
            sendMentionMessage(channel, message, "YES");
        } else {
            sendMentionMessage(channel, message, "NO");
        }
    }
}
