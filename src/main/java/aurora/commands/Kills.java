package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.ArrayList;

public class Kills extends BossAbstract {
    public static void kills(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        ArrayList<Boss> bosses = changeAbbreviations(message.getContent().split("!kills ")[1]);
        for(Boss boss : bosses)
            channel.sendMessage(getKills(boss)).queue();

        if (message.getContent().contains("all"))
            channel.sendMessage(getOverallKills()).queue();
            for (Boss boss : bossList)
                channel.sendMessage(getKills(boss)).queue();
    }
}
