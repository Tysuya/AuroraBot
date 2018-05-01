package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Date;

public class Active extends BossAbstract {
    public static void active(MessageChannel channel, Message message) {
        String messageString = "";
        int total = 0;
        for (String bossName : bossNamesFinal)
            if (nextBossSpawnTime.get(bossName) != null && !(new Date().getTime() > nextBossSpawnTime.get(bossName).getTime())) {
                messageString += respawnTime(bossName);
                total++;
            }
        channel.sendMessage("Currently tracked bosses: " + codeBlock(Integer.toString(total)) + "\n" + messageString).queue();

        /*AuroraBot.jda.getTextChannelById("420067387644182538").sendMessage(getOverallKills()).queue();
        for (String bossName : bossNamesFinal) {
            AuroraBot.jda.getTextChannelById("420067387644182538").sendMessage(getKills(bossName)).queue();
            AuroraBot.jda.getTextChannelById("422701643566678016").sendMessage(respawnTime(bossName) + currentHunters(bossName)).queue();
        }*/
    }
}
