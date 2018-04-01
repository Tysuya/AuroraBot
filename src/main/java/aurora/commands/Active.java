package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Date;

public class Active extends Boss {
    public static void active(MessageChannel channel, Message message) {
        String messageString = "";
        for (String bossName : bossNamesFinal)
            if (nextBossSpawnTime.get(bossName) != null && !(new Date().getTime() > nextBossSpawnTime.get(bossName).getTime()))
                messageString += respawnTime(bossName);
        if (messageString.isEmpty())
            messageString = codeBlock("No active bosses");
        channel.sendMessage(messageString).queue();

        if (message.getContent().contains("dump")) {
            channel.sendMessage(getOverallKills(true)).queue();
            for (String bossName : bossNamesFinal)
                channel.sendMessage(getKills(bossName, true)).queue();
        }
    }
}
