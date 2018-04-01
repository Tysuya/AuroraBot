package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;
import java.util.List;

public class PunchOut extends Boss {
    public static void punchOut(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!pout ")[1]);

        channel.sendMessage(codeBlock(message.getAuthor().getName()) + " was unsuccessful in punching out").queue();

    /*    if (message.getContent().contains("all")) {
            if(message.getMentionedUsers().isEmpty())
                removeAll(channel, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeAll(channel, hunter);
        }

        for(String bossName : bossNames) {
            if(message.getMentionedUsers().isEmpty())
                removeHunter(channel, bossName, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeHunter(channel, bossName, hunter);
            updateBossInfo(bossName);
        }*/
    }

    public static void removeHunter(MessageChannel channel, String bossName, User hunter) {
        bossHunters.get(bossName).remove(hunter);

        String messageString = codeBlock(hunter.getName()) + " just punched out for " + bold(bossName) + ". Thanks for your service!";

        channel.sendMessage(messageString + currentHunters(bossName)).queue();
    }

    public static void removeAll(MessageChannel channel, User hunter) {
        channel.sendMessage(codeBlock(hunter.getName()) + " just punched out for " + bold("ALL BOSSES") + ". Thanks for your service!").queue();

        for (String bossName : bossNamesFinal) {
            bossHunters.get(bossName).remove(hunter);
            updateBossInfo(bossName);
        }
    }
}
