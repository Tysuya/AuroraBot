package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.*;

public class Report extends Boss {
    public static void report(MessageChannel channel, Message message) {
        String timeOfDeath = "";
        String[] initialReport = message.getContent().split("!report ")[1].split(" ");

        Calendar calendar = Calendar.getInstance();

        for(int i = 0; i < initialReport.length; i++) {
            if (initialReport[i].matches(".*\\d+.*")) {
                timeOfDeath = initialReport[i];
                String[] timeOfDeathArray = new String[2];
                if (timeOfDeath.contains(":")) {
                    timeOfDeathArray = timeOfDeath.split(":");
                } else {
                    timeOfDeathArray[0] = timeOfDeath.charAt(0) + "" + timeOfDeath.charAt(1);
                    timeOfDeathArray[1] = timeOfDeath.charAt(2) + "" + timeOfDeath.charAt(3);
                }

                calendar.set(Calendar.MINUTE, Integer.parseInt(timeOfDeathArray[0]));
                calendar.set(Calendar.SECOND, Integer.parseInt(timeOfDeathArray[1]));
            }
        }

        ArrayList<String> report = changeAbbreviations(message.getContent().split("!report ")[1]);

        String bossName = report.get(0);

        if(calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

        String author = message.getAuthor().getName();
        if(message.getContent().contains("@"))
            author = message.getMentionedUsers().get(0).getName();

        String messageString = "";

        if(message.getContent().contains("lost")) {
            messageString = "That's okay, " + codeBlock(author) + "! We all fail sometimes!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was lost   by " + author);

            calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
            nextBossSpawnTime.put(bossName, calendar.getTime());

        }
        else {
            messageString = "Great job, " + codeBlock(author) + "!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was killed by " + author);
            HashMap<String, Integer> authorList = bossKills.get(bossName);

            authorList.putIfAbsent(author, 0);
            authorList.put(author, authorList.get(author) + 1);

            bossKills.put(bossName, authorList);

            calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
            nextBossSpawnTime.put(bossName, calendar.getTime());
            try {
                MessageHistory messageHistory = new MessageHistory(leaderboardChannel);
                List<Message> messageHistoryList = messageHistory.retrievePast(50).complete();
                for (Message eachMessage : messageHistoryList) {
                    if (eachMessage.getContent().contains(bossName))
                        eachMessage.editMessage(getKills(bossName)).complete();
                    if (eachMessage.getContent().contains("overall"))
                        eachMessage.editMessage(getOverallKills()).complete();
                }
                messageHistory = new MessageHistory(bossInfoChannel);
                messageHistoryList = messageHistory.retrievePast(50).complete();
                for (Message eachMessage : messageHistoryList)
                    if (eachMessage.getContent().contains(bossName))
                        updateBossInfo(bossName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(name) + "! You have just reported your " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill!\n" + emojiString).queue();
        }

        if (totalKillCount % 100 == 0 && totalKillCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, everyone! " + codeBlock(bossHuntersChannel.getMessageById(bossHuntersChannel.getLatestMessageId()).complete().getAuthor().getName()) + " just reported the " + codeBlock(Integer.toString(totalKillCount)) + "th overall kill for " + bold("Aurora") + "!\n" + emojiString).queue();

        if (overallKillsString.isEmpty())
            overallKillsString = " ";
        return "Total overall kills for " + bold("AURORA") +  ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + overallKillsString + "\n```";
    }
}
