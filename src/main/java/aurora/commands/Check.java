package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Date;

public class Check extends Boss {
    public static void check(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!check ")[1]);
        for(String bossName : bossNames) {
            spawnTimer(bossName, channel.sendMessage(respawnTime(bossName) +
                    "\nCurrent Time: " + codeBlock(dateFormat.format(new Date())) +
                    currentHunters(bossName)).complete());
        }
    }
}
