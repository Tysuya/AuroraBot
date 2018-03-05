package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.ArrayList;

public class PunchOut extends Boss {
    public static void punchOut(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!pout ")[1]);

        for(String bossName : bossNames) {
            if (bossName.contains("@"))
                break;
            if(message.getMentionedUsers().isEmpty())
                removeHunter(channel, bossName, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeHunter(channel, bossName, hunter);
        }
    }

    public static void removeHunter(MessageChannel channel, String bossName, User hunter) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        ArrayList<User> newHuntersList = new ArrayList<>();
        if(huntersList == null)
            huntersList = new ArrayList<>();

        for(User hunterInList : huntersList) {
            if(!hunterInList.equals(hunter)) {
                newHuntersList.add(hunterInList);
            }
        }

        huntersList = newHuntersList;
        bossHunters.put(bossName, huntersList);

        String messageString = codeBlock(hunter.getName()) + " just punched out for " + bold(bossName) + ". Thanks for your service!";

        channel.sendMessage(messageString + currentHunters(bossName)).queue();
    }
}
