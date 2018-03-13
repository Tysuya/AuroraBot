package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Remove extends Boss {
    public static void remove(MessageChannel channel, Message message) {
        String bossName = changeAbbreviations(message.getContent().split("!remove ")[1]).get(0);
        int amount = Integer.parseInt(message.getContent().split(" ")[2]);
        String author = message.getAuthor().getName();
        if(!message.getMentionedUsers().isEmpty())
            author = message.getMentionedUsers().get(0).getName();

        HashMap<String, Integer> authorList = bossKills.get(bossName);
        authorList.put(author, authorList.get(author) - amount);

        bossKills.put(bossName, authorList);

        try {
            List<Message> messageHistoryList = new MessageHistory(leaderboardChannel).retrievePast(50).complete();
            for (Message eachMessage : messageHistoryList) {
                if (eachMessage.getContent().contains(bossName))
                    eachMessage.editMessage(getKills(bossName)).complete();
                if (eachMessage.getContent().contains("overall"))
                    eachMessage.editMessage(getOverallKills()).complete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String messageString = "Removed " + codeBlock(Integer.toString(amount)) + " kill of " + bold(bossName) + " from " + codeBlock(author);
        if (amount > 1)
            messageString = "Removed " + codeBlock(Integer.toString(amount)) + " kills of " + bold(bossName) + " from " + codeBlock(author);
        channel.sendMessage(messageString).queue();
    }
}
