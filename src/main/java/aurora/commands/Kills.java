package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class Kills extends BossAbstract {
    public static void kills(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!kills ")[1]);
        for(String bossName : bossNames)
            channel.sendMessage(getKills(bossName)).queue();
    }
}
