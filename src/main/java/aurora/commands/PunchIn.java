package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class PunchIn extends BossAbstract {
    public static void punchIn(MessageChannel channel, Message message) {
        ArrayList<Boss> bosses = changeAbbreviations(message.getContent().split("!pin ")[1]);
        System.out.println(bosses.toString());

        if (message.getContent().contains("all")) {
            if (message.getMentionedUsers().isEmpty())
                addAll(channel, message.getAuthor());
            else
                for (User hunter : message.getMentionedUsers())
                    addAll(channel, hunter);
        }

        for(Boss boss : bosses) {
            if(message.getMentionedUsers().isEmpty())
                addHunter(channel, boss, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    addHunter(channel, boss, hunter);
        }

        for (Boss boss : bossList)
            boss.updateBossInfo();
    }

    public static void addHunter(MessageChannel channel, Boss boss, User hunter) {
        List<User> huntersList = boss.getHunters();

        String punchedStatus = codeBlock(hunter.getName());
        if(!huntersList.contains(hunter)) {
            huntersList.add(hunter);
            punchedStatus += " just punched in for " + bold(boss.getBossName());
        }
        else {
            punchedStatus += " has already been punched in for " + bold(boss.getBossName());
        }
        boss.setHunters(huntersList);

        channel.sendMessage(punchedStatus + boss.respawnTime() + boss.currentHunters()).queue();
    }

    public static void addAll(MessageChannel channel, User hunter) {
        channel.sendMessage(codeBlock(hunter.getName()) + " just punched in for " + bold("ALL BOSSES")).queue();

        for (Boss boss : bossList) {
            List<User> huntersList = boss.getHunters();
            if(!huntersList.contains(hunter))
                huntersList.add(hunter);
        }
    }
}
