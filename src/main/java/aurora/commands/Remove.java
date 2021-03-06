package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.HashMap;
import java.util.List;

public class Remove extends BossAbstract {
    public static void remove(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        Boss boss = changeAbbreviations(message.getContent().split("!remove ")[1]).get(0);
        int amount = 1;
        for (String amountString : message.getContent().split(" "))
            if (amountString.chars().allMatch(Character::isDigit))
                amount = Integer.parseInt(amountString);

        String author = message.getAuthor().getName();
        if(!message.getMentionedUsers().isEmpty())
            author = message.getMentionedUsers().get(0).getName();

        HashMap<String, Integer> authorList = boss.getKills();
        authorList.put(author, authorList.get(author) - amount);

        if (authorList.get(author) <= 0)
            authorList.remove(author);

        boss.setKills(authorList);

        try {
            if (!AuroraBot.debugMode)
                dropbox.writeHistory(boss.getBossName(), "Leaderboard", getKills(boss).trim());

            updateChannels(boss);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String messageString = "Removed " + codeBlock(Integer.toString(amount)) + " kill of " + bold(boss.getBossName()) + " from " + codeBlock(author);
        if (amount > 1)
            messageString = "Removed " + codeBlock(Integer.toString(amount)) + " kills of " + bold(boss.getBossName()) + " from " + codeBlock(author);
        channel.sendMessage(messageString).queue();
    }
}
