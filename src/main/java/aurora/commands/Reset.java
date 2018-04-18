package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class Reset extends Boss {
    public static void reset(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!reset ")[1]);

        if(message.getContent().contains("all")) {
            channel.sendMessage(bold("ALL BOSS") + " respawn timers have been reset").queue();
            for (String bossName : bossNamesFinal) {
                nextBossSpawnTime.put(bossName, null);
                updateBossInfo(bossName);
            }
        }

        for(String bossName : bossNames) {
            nextBossSpawnTime.put(bossName, null);
            channel.sendMessage(bold(bossName) + "'s respawn timer has been reset").queue();
            updateBossInfo(bossName);
        }
    }
}
