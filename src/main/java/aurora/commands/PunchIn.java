package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PunchIn extends Boss {
    public static void punchIn(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!pin ")[1]);
        System.out.println(bossNames.toString());

        if (message.getContent().contains("all")) {
            if (message.getMentionedUsers().isEmpty())
                addAll(channel, message.getAuthor());
            else
                for (User hunter : message.getMentionedUsers())
                    addAll(channel, hunter);
        }

        for(String bossName : bossNames) {
            if(message.getMentionedUsers().isEmpty())
                addHunter(channel, bossName, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    addHunter(channel, bossName, hunter);
            updateBossInfo(bossName);
        }
    }

    public static void addHunter(MessageChannel channel, String bossName, User hunter) {
        List<User> huntersList = bossHunters.get(bossName);

        String punchedStatus = codeBlock(hunter.getName());
        if(!huntersList.contains(hunter)) {
            huntersList.add(hunter);
            punchedStatus += " just punched in for " + bold(bossNamesFinal[new Random().nextInt(bossNamesFinal.length)]);
        }
        else {
            punchedStatus += " has already been punched in for " + bold(bossNamesFinal[new Random().nextInt(bossNamesFinal.length)]);
        }
        bossHunters.put(bossName, huntersList);

        channel.sendMessage(punchedStatus + respawnTime(bossName) + currentHunters(bossName)).queue();
    }

    public static void addAll(MessageChannel channel, User hunter) {
        channel.sendMessage(codeBlock(hunter.getName()) + " just punched in for " + bold("ALL BOSSES")).queue();

        for (String bossName : bossNamesFinal) {
            List<User> huntersList = bossHunters.get(bossName);
            if(!huntersList.contains(hunter))
                huntersList.add(hunter);
            updateBossInfo(bossName);
        }
    }
}
