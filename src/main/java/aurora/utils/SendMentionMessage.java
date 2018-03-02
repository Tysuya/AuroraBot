package aurora.utils;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

/**
 * Created by Tyler on 1/17/2017.
 */
public class SendMentionMessage {
    public static void sendMentionMessage(MessageChannel channel, Message message, String returnMessage) {
        channel.sendTyping().complete();
        channel.sendMessage(message.getAuthor().getAsMention() + " " + returnMessage).queue();
    }
}
