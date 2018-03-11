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
            HashMap<String, Integer> authorList = bossHuntersKills.get(bossName);

            authorList.putIfAbsent(author, 0);
            authorList.put(author, authorList.get(author) + 1);

            bossHuntersKills.put(bossName, authorList);
            try {
                String killsString = "";
                for (String eachBossName : bossNamesFinal)
                    killsString += getKills(eachBossName);
                bossKillsMessage.editMessage(killsString).complete();
                AuroraBot.jda.getTextChannelById("420067387644182538").editMessageById("422263655187349525", getOverallKills()).complete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
        nextBossSpawnTime.put(bossName, calendar.getTime());

        bossReport.put(bossName, channel.sendMessage(messageString +
                respawnTime(bossName) +
                currentHunters(bossName)).complete());

        spawnTimer(bossName);
    }

    public static String getOverallKills() {
        bossOverallKills.clear();
        for(String bossName : bossNamesFinal) {
            String[] huntersLine = getKills(bossName).split("\n");
            for (int i = 2; i < huntersLine.length - 1; i++) {
                int parenthesis = huntersLine[i].indexOf(")");
                int colon = huntersLine[i].indexOf(":");

                String name = huntersLine[i].substring(parenthesis + 2, colon);
                Integer kills = Integer.parseInt(huntersLine[i].substring(colon + 2));
                bossOverallKills.putIfAbsent(name, 0);
                bossOverallKills.put(name, bossOverallKills.get(name) + kills);
            }
        }

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

            if(killCount % 100 == 0 && killCount != 0)
                messageChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(name) + "! You have just reported your " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill!\n" + emojiString).queue();
        }

        if (totalKillCount % 100 == 0 && totalKillCount != 0)
            messageChannel.sendMessage("@everyone\nCongratulations, everyone! " + codeBlock(messageChannel.getMessageById(messageChannel.getLatestMessageId()).complete().getAuthor().getName()) + " just reported the " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill for " + bold("Aurora") + "!\n" + emojiString).queue();

        if (overallKillsString.isEmpty())
            overallKillsString = " ";
        return "Total overall kills for " + bold("Aurora") +  ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + overallKillsString + "\n```";
    }
}
