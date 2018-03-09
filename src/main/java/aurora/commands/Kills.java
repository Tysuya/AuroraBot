package aurora.commands;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.util.ArrayList;

public class Kills extends Boss {
    public static void kills(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!kills ")[1]);
        for(String bossName : bossNames)
            channel.sendMessage(bossKillsLog.get(bossName).getContent()).queue();
    }
}