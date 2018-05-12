package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;

public class PunchOut extends BossAbstract {
    public static void punchOut(MessageChannel channel, Message message) {
        ArrayList<Boss> bosses = changeAbbreviations(message.getContent().split("!pout ")[1]);

        if (message.getContent().contains("all")) {
            if(message.getMentionedUsers().isEmpty())
                removeAll(channel, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeAll(channel, hunter);
        }

        for(Boss boss : bosses) {
            if(message.getMentionedUsers().isEmpty())
                removeHunter(channel, boss, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeHunter(channel, boss, hunter);
        }

        for (Boss boss : bossList)
            boss.updateBossInfo();
    }

    public static void removeHunter(MessageChannel channel, Boss boss, User hunter) {
        boss.getHunters().remove(hunter);

        String messageString = codeBlock(hunter.getName()) + " just punched out for " + bold(boss.getBossName()) + ". Thanks for your service!";

        channel.sendMessage(messageString + boss.currentHunters()).queue();
    }

    public static void removeAll(MessageChannel channel, User hunter) {
        channel.sendMessage(codeBlock(hunter.getName()) + " just punched out for " + bold("ALL BOSSES") + ". Thanks for your service!").queue();

        for (Boss boss : bossList)
            boss.getHunters().remove(hunter);
    }
}
