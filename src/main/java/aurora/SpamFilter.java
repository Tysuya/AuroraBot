package aurora;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;

public class SpamFilter {
    private static long time = System.currentTimeMillis() - 500;

    public static void spamFilter(MessageChannel channel, Message message) {
        if (System.currentTimeMillis() - time < 500) {
            System.out.println(System.currentTimeMillis() - time);
            message.delete().queue();
            time = System.currentTimeMillis();
            return;
        }

        List<Message> messageHistoryList = new MessageHistory(channel).retrievePast(10).complete();
        int counter = 0;
        for (Message eachMessage : messageHistoryList) {
            if (message.getAuthor().equals(eachMessage.getAuthor()) && message.getAttachments().isEmpty()) {
                if (message.getContent().equals(eachMessage.getContent()))
                    counter++;
                if (counter >= 3) {
                    message.delete().queue();
                    break;
                }
            }
        }
        time = System.currentTimeMillis();
    }
}
