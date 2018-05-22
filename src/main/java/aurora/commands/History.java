package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class History extends BossAbstract {
    public static void history(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        Boss boss = changeAbbreviations(message.getContent().split("!history ")[1]).get(0);

        String bossHistoryString = "";
        String[] bossHistoryArray = boss.getHistory().split("\n");

        int amount = 10;
        for (String amountString : message.getContent().split(" "))
            if (amountString.chars().allMatch(Character::isDigit))
                amount = Integer.parseInt(amountString);
        if (amount > bossHistoryArray.length)
            amount = bossHistoryArray.length - 1;

        for (int i = bossHistoryArray.length - amount; i < bossHistoryArray.length; i++)
            bossHistoryString += "\n" + bossHistoryArray[i];
        if (bossHistoryString.isEmpty())
            bossHistoryString = " ";
        channel.sendMessage("History for " + codeBlock(Integer.toString(amount)) + " kills of " + bold(boss.getBossName()) + ":```" + bossHistoryString + "```").queue();
    }
}
