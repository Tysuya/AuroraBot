package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class PunchIn extends Boss {
    public static void punchIn(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!pin ")[1]);
        System.out.println(bossNames.toString());

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
            punchedStatus += " just punched in for " + bold(bossName);
        }
        else {
            punchedStatus += " has already been punched in for " + bold(bossName);
        }
        bossHunters.put(bossName, huntersList);

        channel.sendMessage(punchedStatus + respawnTime(bossName) + currentHunters(bossName)).queue();
    }
}
