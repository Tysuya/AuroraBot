package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;
import java.util.Date;

public class Check extends BossAbstract {
    public static void check(MessageChannel channel, Message message) {
        ArrayList<Boss> bosses = changeAbbreviations(message.getContent().split("!check ")[1]);
        for(Boss boss : bosses) {
            boss.spawnTimer(channel.sendMessage(boss.respawnTime() +
                    "\nCurrent Time: " + codeBlock(dateFormat.format(new Date())) +
                    boss.currentHunters()).complete());
        }
        if (message.getContent().contains("all"))
            for (Boss boss : bossList)
                channel.sendMessage(boss.respawnTime() + boss.currentHunters()).queue();
    }
}
