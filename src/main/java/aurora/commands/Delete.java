package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;

public class Delete {
    public static void delete(MessageChannel channel, Message message) {
        int amount = Integer.parseInt(message.getContent().split("!delete ")[1]);
        List<Message> messageHistoryList = new MessageHistory(channel).retrievePast(amount).complete();
        for (Message eachMessage : messageHistoryList) {
            eachMessage.delete().queue();
        }
    }
}
