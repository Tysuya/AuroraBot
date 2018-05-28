package aurora;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;

public class SpamFilter {
    public static void spamFilter(MessageChannel channel, Message message) {
        List<Message> messageHistoryList = new MessageHistory(channel).retrievePast(5).complete();
        int counter = 0;
        for (Message eachMessage : messageHistoryList) {
            System.out.println(message.getAttachments().isEmpty());
            if (message.getAuthor().equals(eachMessage.getAuthor()) && message.getAttachments().isEmpty()) {
                if (message.getContent().equals(eachMessage.getContent()))
                    counter++;
                if (counter >= 3)
                    message.delete().queue();
            }
        }
    }
}
