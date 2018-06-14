package aurora;

import aurora.commands.BossAbstract;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static aurora.commands.BossAbstract.bossHuntersChannel;

public class SpamFilter {
    private static long time = System.currentTimeMillis() - 500;

    public static void spamFilter(MessageChannel channel, Message message) {
        if (!channel.equals(bossHuntersChannel)) {
            List<Message> messageHistoryList = new MessageHistory(channel).retrievePast(10).complete();
            int counter = 0;
            for (Message eachMessage : messageHistoryList) {
                if (message.getAuthor().equals(eachMessage.getAuthor()) && message.getAttachments().isEmpty()) {
                    if (message.getContent().equals(eachMessage.getContent()))
                        counter++;
                    if (counter >= 3 || System.currentTimeMillis() - time < 500) {
                        message.delete().queue();
                        channel.sendMessage(message.getAuthor().getAsMention() + " Your message has been marked as spam and has been deleted.").complete().delete().queueAfter(5, TimeUnit.SECONDS);
                        break;
                    }
                }
            }
            time = System.currentTimeMillis();
        }
    }
}
