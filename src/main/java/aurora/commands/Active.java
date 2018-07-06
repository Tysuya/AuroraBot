package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Date;

public class Active extends BossAbstract {
    public static void active(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        String messageString = "";
        int total = 0;
        for (Boss boss : bossList)
            if (boss.getNextSpawnTime() != null && !(new Date().getTime() > boss.getNextSpawnTime().getTime())) {
                messageString += boss.respawnTime();
                total++;
            }
        channel.sendMessage("Currently tracked bosses: " + codeBlock(Integer.toString(total)) + messageString).queue();
    }
}
