package aurora.commands;

import aurora.AuroraBot;
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

        for (String bossName : bossNamesFinal)
            AuroraBot.jda.getTextChannelById("422701643566678016").sendMessage(respawnTime(bossName) + currentHunters(bossName)).queue();
    }
}
