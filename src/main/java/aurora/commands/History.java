package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class History extends BossAbstract {
    public static void history(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!history ")[1]);

        for(String bossName : bossNames) {
            String bossHistoryString = "";
            for (String historyString : bossHistory.get(bossName))
                bossHistoryString += "\n" + historyString;
            if (bossHistoryString.isEmpty())
                bossHistoryString = " ";
            channel.sendMessage("History for " + bold(bossName) + ":```" + bossHistoryString + "```").queue();
        }
    }
}
