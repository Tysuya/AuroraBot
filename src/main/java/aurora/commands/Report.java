package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;

import java.util.*;

public class Report extends BossAbstract {
    public static void report(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        String timeOfDeath;
        String[] initialReport;
        if (message.getContent().contains("!r "))
            initialReport = message.getContent().split("!r ")[1].split(" ");
        else
            initialReport = message.getContent().split("!report ")[1].split(" ");

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

        ArrayList<Boss> bosses;

        if (message.getContent().contains("!r "))
            bosses = changeAbbreviations(message.getContent().split("!r ")[1]);
        else
            bosses = changeAbbreviations(message.getContent().split("!report ")[1]);

        Boss boss = bosses.get(0);

        if (calendar.getTimeInMillis() > Calendar.getInstance().getTimeInMillis())
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);

        String author = message.getAuthor().getName();
        if (!message.getMentionedUsers().isEmpty())
            author = message.getMentionedUsers().get(0).getName();

        String messageString = "Great job, " + codeBlock(author) + "!";
        String historyString = "At " + dateFormat.format(calendar.getTime()) + " " + boss.getBossName() + " was killed by " + author;
        if (message.getContent().contains("lost")) {
            messageString = "That's okay, " + codeBlock(author) + "! We all fail sometimes!";
            historyString = "At " + dateFormat.format(calendar.getTime()) + " " + boss.getBossName() + " was lost   by " + author;
        }

        calendar.add(Calendar.MINUTE, boss.getRespawnTime());
        boss.setNextSpawnTime(calendar.getTime());

        boss.spawnTimer(channel.sendMessage(boss.notifyingHunters() + "\n" +
                messageString +
                boss.respawnTime() +
                boss.currentHunters()).complete());

        if (!boss.getHistory().trim().contains(historyString)) {
            boss.setHistory(boss.getHistory() + "\n" + historyString);
            /*if (!AuroraBot.debugMode)
                dropbox.writeHistory(boss.getBossName(), "History", boss.getHistory().trim());*/
        }

        HashMap<String, Integer> authorList = boss.getKills();
        authorList.putIfAbsent(author, 0);
        authorList.put(author, authorList.get(author) + 1);
        boss.setKills(authorList);

        // Update #leaderboard and #bossinfo
        if (!message.getContent().contains("lost"))
            updateChannels(boss, author);
    }

    private static void updateChannels(Boss boss, String author) {
        if (!AuroraBot.debugMode)
            dropbox.writeHistory(boss.getBossName(), "Leaderboard", getKills(boss).trim());

        try {
            List<Message> messageHistoryList = new MessageHistory(leaderboardChannel).retrievePast(100).complete();
            for (Message eachMessage : messageHistoryList) {
                if (eachMessage.getContent().contains(boss.getBossName()))
                    eachMessage.editMessage(getKills(boss)).complete();
                if (eachMessage.getContent().contains("overall"))
                    eachMessage.editMessage(getOverallKills()).complete();
            }
            messageHistoryList = new MessageHistory(bossInfoChannel).retrievePast(100).complete();
            for (Message eachMessage : messageHistoryList)
                if (eachMessage.getContent().contains(boss.getBossName()))
                    boss.updateBossInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkKills(boss, author);
    }

    private static void checkKills(Boss boss, String author) {
        String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
        String emojiString = "";
        for (int i = 1; i < 51; i++) {
            emojiString += emojis[new Random().nextInt(5)];
            if (i % 10 == 0)
                emojiString += "\n";
            else
                emojiString += " ";
        }

        int killCount = boss.getKills().get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("Congratulations, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th kill for " + bold(boss.getBossName()) + "!\n" + emojiString).queue();

        killCount = hunterOverallKills.get(author);
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("Congratulations, " + codeBlock(author) + "! You have just reported your " + codeBlock(Integer.toString(killCount)) + "th overall kill!\n" + emojiString).queue();

        killCount = boss.getOverallKills();
        if (killCount % 100 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("Congratulations, " + codeBlock(author) + "! You have just reported the " + codeBlock(Integer.toString(killCount)) + "th total kill for " + bold(boss.getBossName()) + "!\n" + emojiString).queue();

        killCount = auroraOverallKills;
        if (killCount % 500 == 0 && killCount != 0)
            bossHuntersChannel.sendMessage("Congratulations, everyone! " + codeBlock(author) + " just reported the " + codeBlock(Integer.toString(killCount)) + "th overall kill for " + bold("Aurora") + "!\n" + emojiString).queue();
    }
}
