package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.List;

public class Delete {
    public static void delete(MessageChannel channel, Message message) {
        int amount = Integer.parseInt(message.getContent().split("!delete ")[1]);
        int remainder = amount % 100;
        int counter = (amount - remainder) / 100;
        amount = 100;

        for (int i = 0; i <= counter; i++) {
            if (i == counter)
                amount = remainder;
            List<Message> messageHistoryList = new MessageHistory(channel).retrievePast(amount).complete();
            for (Message eachMessage : messageHistoryList)
                eachMessage.delete().queue();
        }
    }
}
