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

        for (int i = 0; i < initialReport.length; i++) {
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

        if (calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

        String author = message.getAuthor().getName();
        if (!message.getMentionedUsers().isEmpty())
            author = message.getMentionedUsers().get(0).getName();

        String messageString = "";

        if (message.getContent().contains("lost")) {
            messageString = "That's okay, " + codeBlock(author) + "! We all fail sometimes!";
            messageString = "Wow, you're such a failure " + codeBlock(author) + ". You better not miss the next one!";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was lost   by " + author);

            calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
            nextBossSpawnTime.put(bossName, calendar.getTime());
        }
        else {
            messageString = "Great job, " + codeBlock(author) + "!";
            messageString = "What took so long, " + codeBlock(author) + "?";
            bossHistory.get(bossName).add("At " + dateFormat.format(calendar.getTime()) + " " + bossName + " was killed by " + author);
            HashMap<String, Integer> authorList = bossKills.get(bossName);

            authorList.putIfAbsent(author, 0);
            authorList.put(author, authorList.get(author) + 1);

            bossKills.put(bossName, authorList);

            calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));
            nextBossSpawnTime.put(bossName, calendar.getTime());

            try {
                List<Message> messageHistoryList = new MessageHistory(leaderboardChannel).retrievePast(50).complete();
                for (Message eachMessage : messageHistoryList) {
                    if (eachMessage.getContent().contains(bossName))
                        eachMessage.editMessage(getKills(bossName)).complete();
                    if (eachMessage.getContent().contains("overall"))
                        eachMessage.editMessage(getOverallKills()).complete();
                }
                messageHistoryList = new MessageHistory(bossInfoChannel).retrievePast(50).complete();
                for (Message eachMessage : messageHistoryList)
                    if (eachMessage.getContent().contains(bossName))
                        updateBossInfo(bossName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            checkKills(bossName, author);
        }
        spawnTimer(bossName, channel.sendMessage(notifyingHunters(bossName) + "\n" +
                messageString +
                respawnTime(bossName) +
                currentHunters(bossName)).complete());
    }

    public static void checkKills(String bossName, String author) {
        String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
        String emojiString = "";
        for (int i = 1; i < 101; i++) {
            emojiString += emojis[new Random().nextInt(5)];
            if (i % 20 == 0)
                emojiString += "\n";
            else
                emojiString += " ";
        }

        int killCount = bossKills.get(bossName).get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nHey, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th kill for " + bold(bossName) + "!\n" + "Get a life, scrub.").queue();

        killCount = hunterOverallKills.get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nHey, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th overall kill!\n" + "Get a life, scrub.").queue();

        killCount = bossOverallKills.get(bossName);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nHey, " + codeBlock(author) + "! You have just reported the " + codeBlock(Integer.toString(killCount)) + "th total kill for " + bold(bossName) + "!\n" + "Get a life, scrub.").queue();

        killCount = auroraOverallKills;
        if (killCount % 500 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, everyone! " + codeBlock(author) + " just reported the " + codeBlock(Integer.toString(killCount)) + "th overall kill for " + bold("Aurora") + "!\n" + emojiString).queue();

        /*int killCount = bossKills.get(bossName).get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th kill for " + bold(bossName) + "!\n" + emojiString).queue();

        killCount = hunterOverallKills.get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th overall kill!\n" + emojiString).queue();

        killCount = bossOverallKills.get(bossName);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(author) + "! You have just reported the " + codeBlock(Integer.toString(killCount)) + "th total kill for " + bold(bossName) + "!\n" + emojiString).queue();

        killCount = auroraOverallKills;
        if (killCount % 500 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, everyone! " + codeBlock(author) + " just reported the " + codeBlock(Integer.toString(killCount)) + "th overall kill for " + bold("Aurora") + "!\n" + emojiString).queue();*/
    }
}
