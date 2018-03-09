package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

        String messageString = "";

        if(message.getContent().contains("lost")) {
            messageString = "That's okay, " + codeBlock(author) + "! We all fail sometimes!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was lost   by " + author);
        }
        else {
            messageString = "Great job, " + codeBlock(author) + "!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was killed by " + author);
            HashMap<String, Integer> authorList = bossKills.get(bossName);

            authorList.putIfAbsent(author, 0);
            authorList.put(author, authorList.get(author) + 1);

            bossKills.put(bossName, authorList);
            try {
                bossKillsLog.get(bossName).editMessage(getKills(bossName)).queue();
                AuroraBot.jda.getTextChannelById("420067387644182538").editMessageById("421561504383500290", getOverallKills()).queue();
                System.out.println(getOverallKills());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
        nextBossSpawnTime.put(bossName, calendar.getTime());

        bossReport.put(bossName, channel.sendMessage(messageString +
                respawnTime(bossName) +
                currentHunters(bossName)).complete());

        spawnTimer();
    }

    public static String getKills(String bossName) {
        String bossKillsString = "";
        HashMap<String, Integer> killsHashMap = bossKills.get(bossName);

        Object[] entrySet = killsHashMap.entrySet().toArray();
        Arrays.sort(entrySet, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
        String emojiString = "";
        for(int i = 1; i < 101; i++) {
            emojiString += emojis[new Random().nextInt(5)];
            if(i % 20 == 0)
                emojiString += "\n";
            else
                emojiString += " ";
        }

        int totalKillCount = 0;
        int rank = 1;
        for (Object entry : entrySet) {
            killsHashMap.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
            String name = ((Map.Entry<String, Integer>) entry).getKey();
            int killCount = ((Map.Entry<String, Integer>) entry).getValue();
            totalKillCount += killCount;
            bossKillsString += "\n" + rank++ + ") " + name + ": " + killCount;

            if(killCount % 100 == 0)
                messageChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(name) + "! You have just reported your " + codeBlock(Integer.toString(totalKillCount)) + "th kill for " + bold(bossName) + "!\n" + emojiString).queue();
        }

        if (bossKillsString.isEmpty())
            bossKillsString = " ";
        if (totalKillCount % 100 == 0)
            messageChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(messageChannel.getMessageById(messageChannel.getLatestMessageId()).complete().getAuthor().getName()) + "! You have just reported the " + codeBlock(Integer.toString(totalKillCount)) + "th total kill for " + bold(bossName) + "!\n" + emojiString).queue();

        return "Total kills for " + bold(bossName) + ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + bossKillsString + "\n```";
    }

    public static String getOverallKills() {
        updateOverallKills();

        String overallKillsString = "";

        Object[] entrySet = bossOverallKills.entrySet().toArray();
        Arrays.sort(entrySet, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
        String emojiString = "";
        for(int i = 1; i < 101; i++) {
            emojiString += emojis[new Random().nextInt(5)];
            if(i % 20 == 0)
                emojiString += "\n";
            else
                emojiString += " ";
        }

        int totalKillCount = 0;
        int rank = 1;
        for (Object entry : entrySet) {
            bossOverallKills.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
            String name = ((Map.Entry<String, Integer>) entry).getKey();
            int killCount = ((Map.Entry<String, Integer>) entry).getValue();
            totalKillCount += killCount;
            overallKillsString += "\n" + rank++ + ") " + name + ": " + killCount;

            if(killCount % 100 == 0)
                messageChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(name) + "! You have just reported your " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill!\n" + emojiString).queue();
        }

        if (totalKillCount % 100 == 0)
            messageChannel.sendMessage("@everyone\nCongratulations, everyone! " + codeBlock(messageChannel.getMessageById(messageChannel.getLatestMessageId()).complete().getAuthor().getName()) + " just reported the " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill for Aurora!\n" + emojiString).queue();

        if (overallKillsString.isEmpty())
            overallKillsString = " ";
        return "Total overall kills: " + codeBlock(Integer.toString(totalKillCount)) + " ```" + overallKillsString + "\n```";
    }
}
