package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class Reset extends BossAbstract {
    public static void reset(MessageChannel channel, Message message) {
        ArrayList<Boss> bosses = changeAbbreviations(message.getContent().split("!reset ")[1]);

        if(message.getContent().contains("all")) {
            channel.sendMessage(bold("ALL BOSS") + " respawn timers have been reset").queue();
            for (Boss boss : bossList)
                boss.setNextSpawnTime(null);
        }

        for(Boss boss : bosses) {
            boss.setNextSpawnTime(null);
            channel.sendMessage(bold(boss.getBossName()) + "'s respawn timer has been reset").queue();
        }

        for (Boss boss : bossList)
            boss.updateBossInfo();
    }
}
