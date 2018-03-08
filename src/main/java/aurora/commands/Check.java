package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class Check extends Boss {
    public static void check(MessageChannel channel, Message message) {
        ArrayList<String> bossNames = changeAbbreviations(message.getContent().split("!check ")[1]);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        for(String bossName : bossNames)
            bossReport.put(bossName, channel.sendMessage(respawnTime(bossName) +
                    "\nCurrent Time: " + codeBlock(dateFormat.format(new Date())) +
                    currentHunters(bossName)).complete());
        spawnTimer();
    }
}
