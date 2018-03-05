package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class Report extends Boss {
    public static void report(MessageChannel channel, Message message) {
        messageChannel = channel;

        String timeOfDeath = "";
        String[] initialReport = message.getContent().split("!report ")[1].split(" ");


        Calendar calendar = Calendar.getInstance();

        if(initialReport.length > 1 && !initialReport[1].contains("LOST") && !initialReport[1].contains("@")) {
            timeOfDeath = initialReport[1];
            String[] timeOfDeathArray = new String[2];
            if(timeOfDeath.contains(":")) {
                timeOfDeathArray = timeOfDeath.split(":");
            }
            else {
                timeOfDeathArray[0] = timeOfDeath.charAt(0) + "" + timeOfDeath.charAt(1);
                timeOfDeathArray[1] = timeOfDeath.charAt(2) + "" + timeOfDeath.charAt(3);
            }

            //now.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, Integer.parseInt(timeOfDeathArray[0]));
            calendar.set(Calendar.SECOND, Integer.parseInt(timeOfDeathArray[1]));
        }

        ArrayList<String> report = changeAbbreviations(message.getContent().split("!report ")[1]);

        String bossName = report.get(0);

        if(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis()) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        }

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String author = message.getAuthor().getName();

        if(message.getContent().contains("@"))
            author = message.getMentionedUsers().get(0).getName();

        String messageString = "Great job, " + codeBlock(author) + "!";

        if(message.getContent().contains("lost")) {
            messageString = "That's okay, " + codeBlock(author) + "! We all fail sometimes!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was lost   by " + author);
        }
        else {
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was killed by " + author);
            HashMap<String, Integer> authorList = bossKills.get(bossName);
            authorList.putIfAbsent(author, 0);

            authorList.put(author, authorList.get(author) + 1);

            bossKills.put(bossName, authorList);
        }

        calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
        nextBossSpawnTime.put(bossName, calendar.getTime());

        bossReport.put(bossName, channel.sendMessage(messageString +
                respawnTime(bossName) +
                currentHunters(bossName)).complete());

        bossKillsMessages.get(bossName).editMessage("Kills for " + bold(bossName) + ":```" + killsString(bossName) + "\n```").queue();
        //channel.sendMessage(messageString + respawnTime(bossName) + messageString2 + messageString3).queue();
    }
}
