package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class Reset extends Boss {
    public static void reset(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!reset ")[1]);

        for(String bossName : bossNames) {
            nextBossSpawnTime.put(bossName, null);
            bossReport.put(bossName, null);
            channel.sendMessage(bold(bossName) + "'s respawn timer has been reset").queue();
            updateBossInfo(bossName);
        }
    }
}
