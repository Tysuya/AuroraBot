package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tyler on 1/17/2017.
 */
public abstract class Boss {
    static HashMap<String, Integer> bossRespawnTimes = new HashMap<>();
    static HashMap<String, List<User>> bossHunters = new HashMap<>();
    static HashMap<String, Date> nextBossSpawnTime = new HashMap<>();
    static HashMap<String, Message> bossReport = new HashMap<>();
    static HashMap<String, ArrayList<String>> bossHistory = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> bossKills = new HashMap<>();
    static HashMap<String, Integer> bossOverallKills = new HashMap<>();
    static HashMap<String, Timer> bossSpawnTimers = new HashMap<>();

    // Test
    /*static MessageChannel bossHuntersChannel = AuroraBot.jda.getTextChannelById("418683981291192331");
    static MessageChannel leaderboardChannel = AuroraBot.jda.getTextChannelById("420058966257827841");
    static MessageChannel bossInfoChannel = AuroraBot.jda.getTextChannelById("422636412702031873");*/

    static MessageChannel bossHuntersChannel = AuroraBot.jda.getTextChannelById("417803228764176385");
    static MessageChannel leaderboardChannel = AuroraBot.jda.getTextChannelById("420067387644182538");
    static MessageChannel bossInfoChannel = AuroraBot.jda.getTextChannelById("422701643566678016");

    static final String[] bossNamesFinal = {"GHOSTSNAKE", "WILDBOAR", "SPIDEY", "BERSERK GOSUMI", "WHITE CROW", "BLOODY GOSUMI", "RAVEN", "BLASTER", "BSSSZSSS", "DESERT ASSASAIN", "STEALTH", "BUZSS", "BIZIZI", "BIGMOUSE", "LESSER MADMAN", "SHAAACK", "SUUUK", "SUSUSUK", "ELDER BEHOLDER", "SANDGRAVE", "LACOSTEZA"};

    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd z");

    public static void initialize() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            //bossHuntersChannel.sendMessage("Good morning, I just woke up! Please punch in and report your most recent kills again. I apologize for any inconveniences my restart caused ^^").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bossNamesFinal.length; i++) {
            bossRespawnTimes.put(bossNamesFinal[i], 20);
            if (i == 0 || i == 4 || i == 8 || i == 15)
                bossRespawnTimes.put(bossNamesFinal[i], 30);
            if (i == 20)
                bossRespawnTimes.put(bossNamesFinal[i], 1);
        }

        for(String bossName : bossNamesFinal) {
            bossHunters.put(bossName, new ArrayList<>());
            bossHistory.put(bossName, new ArrayList<>());
            bossKills.put(bossName, new HashMap<>());
            bossSpawnTimers.put(bossName, new Timer());
        }
        try {
            initializeKills();
            initializeHunters();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check every second if boss respawned
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String currentDate = dateFormat.format(new Date());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, 3);
                    String fastDate = dateFormat.format(calendar.getTime());

                    for(String bossName : bossNamesFinal) {
                        if(nextBossSpawnTime.get(bossName) != null) {
                            if(fastDate.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
                                bossHuntersChannel.sendMessage(notifyingHunters(bossName) +
                                        "\n" + bold(bossName) + " will respawn in " + codeBlock("3") + " minutes! Don't forget to log in!").queue();

                            if(currentDate.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
                                bossHuntersChannel.sendMessage(notifyingHunters(bossName) +
                                        "\n" + bold(bossName) + " has respawned! Find it and kill it!").queue();
                        }
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void spawnTimer(String bossName) {
        try {
            bossSpawnTimers.get(bossName).cancel();
            bossSpawnTimers.put(bossName, new Timer());
            bossSpawnTimers.get(bossName).schedule(new TimerTask() {
                @Override
                public void run() {
                    if(bossReport.get(bossName) != null && nextBossSpawnTime.get(bossName) != null) {
                        long time = (nextBossSpawnTime.get(bossName).getTime() - Calendar.getInstance().getTimeInMillis()) / 1000;
                        long seconds = time % 60;
                        long minutes = time / 60 % 60;
                        
                        bossReport.get(bossName).editMessage(bossReport.get(bossName).getContent() +
                                "\nSpawn Timer: " + codeBlock(Long.toString(minutes)) + " minutes " + codeBlock(Long.toString(seconds)) + " seconds").queue();
                    }
                }
            }, 0, 10000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateBossInfo(String bossName) {
        MessageHistory messageHistory = new MessageHistory(bossInfoChannel);
        List<Message> messageHistoryList = messageHistory.retrievePast(50).complete();
        for (Message message : messageHistoryList) {
            String messageString = respawnTime(bossName) + currentHunters(bossName);
            if (message.getContent().contains(bossName) && !("\n" + message.getContent()).equals(messageString))
                bossReport.put(bossName, message.editMessage(respawnTime(bossName) +
                        currentHunters(bossName)).complete());
                //spawnTimer(bossName);
        }

    }

    public static void initializeHunters() {
        MessageHistory messageHistory = new MessageHistory(bossInfoChannel);
        List<Message> messageHistoryList = messageHistory.retrievePast(50).complete();
        for (Message message : messageHistoryList) {
            for (String bossName : bossNamesFinal) {
                if (message.getContent().contains(bossName)) {
                    String[] lines = message.getContent().split("\n");
                    for (int i = 0; i < lines.length; i++)
                        lines[i] = lines[i].replace("`", "");

                    try {
                        if (!lines[0].split("at ")[1].equals("Unknown")) {
                            Date time = dateFormat.parse(lines[0].split("at ")[1]);
                            /*if (!TimeZone.getTimeZone("PST").inDaylightTime(time))
                                time.setHours(time.getHours() + 1);*/
                            if(bossHuntersChannel.getId().equals("418683981291192331"))
                                time.setHours(time.getHours() + 1);
                            time.setYear(new Date().getYear());
                            System.out.println(bossName + " | " + time);
                            nextBossSpawnTime.put(bossName, time);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!lines[1].split(": ")[1].contains("None")) {
                        String[] hunters = lines[1].split(": ")[1].split(", ");
                        List<User> huntersList = new ArrayList<>();
                        for (String hunter : hunters)
                            huntersList.addAll(AuroraBot.jda.getUsersByName(hunter, true));
                        bossHunters.put(bossName, huntersList);
                    }
                }
            }
        }
    }

    public static void initializeKills() {
        /*HashMap<String, Integer> a = new HashMap<>();
        a.put("dandera", 10);
        a.put("Jenny", 10);
        a.put("Vampy", 3);
        a.put("Aversionist", 2);
        a.put("my name is jeeff", 2);
        a.put("Syeira A.F (Bleu1mage/Angelkar)", 1);
        bossKills.put("GHOSTSNAKE", a);*/

        MessageHistory messageHistory = new MessageHistory(leaderboardChannel);
        List<Message> messageHistoryList = messageHistory.retrievePast(50).complete();
        for (Message message : messageHistoryList) {
            String[] bossKillsLines = message.getContent().split("\nTotal");
            //System.out.println(Arrays.toString(bossKillsLines));

            for (String bossKillsLine : bossKillsLines) {
                String[] huntersLine = bossKillsLine.split("\n");
                //System.out.println(Arrays.toString(huntersLine));

                HashMap<String, Integer> bossKillsHashMap = new HashMap<>();
                for (int i = 1; i < huntersLine.length - 1; i++) {
                    int parenthesis = huntersLine[i].indexOf(")");
                    int colon = huntersLine[i].indexOf(":");

                    String name = huntersLine[i].substring(parenthesis + 2, colon);
                    Integer kills = Integer.parseInt(huntersLine[i].substring(colon + 2));
                    bossKillsHashMap.put(name, kills);
                }

                String bossName = "";
                for (String eachBossName : bossNamesFinal)
                    if (huntersLine[0].contains(eachBossName))
                        bossName = eachBossName;
                //System.out.println(bossName + " = " + bossKillsHashMap);
                bossKills.put(bossName, bossKillsHashMap);
            }
        }
        System.out.println(bossKills.toString());
    }

    public static String getKills(String bossName) {
        String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
        String emojiString = "";
        for(int i = 1; i < 101; i++) {
            emojiString += emojis[new Random().nextInt(5)];
            if(i % 20 == 0)
                emojiString += "\n";
            else
                emojiString += " ";
        }

        String bossKillsString = "";
        HashMap<String, Integer> killsHashMap = bossKills.get(bossName);

        Object[] entrySet = killsHashMap.entrySet().toArray();
        Arrays.sort(entrySet, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });


        int totalKillCount = 0;
        int rank = 1;
        for (Object entry : entrySet) {
            killsHashMap.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
            String name = ((Map.Entry<String, Integer>) entry).getKey();
            int killCount = ((Map.Entry<String, Integer>) entry).getValue();
            totalKillCount += killCount;
            bossKillsString += "\n" + rank++ + ") " + name + ": " + killCount;

            /*if(killCount % 100 == 0 && killCount != 0)
                bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(name) + "! You have just reported your " + codeBlock(Integer.toString(totalKillCount)) + "th kill for " + bold(bossName) + "!\n" + emojiString).queue();*/
        }

        if (bossKillsString.isEmpty())
            bossKillsString = " ";
        /*if (totalKillCount % 100 == 0 && totalKillCount != 0)
            bossHuntersChannel.sendMessage("@everyone\nCongratulations, " + codeBlock(bossHuntersChannel.getMessageById(bossHuntersChannel.getLatestMessageId()).complete().getAuthor().getName()) + "! You have just reported the " + codeBlock(Integer.toString(totalKillCount)) + "th total kill for " + bold(bossName) + "!\n" + emojiString).queue();*/

        return "\nTotal kills for " + bold(bossName) + ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + bossKillsString + "\n```";
    }

    public static String respawnTime(String bossName) {
        String nextSpawn = "Unknown";
        String note = "";

        if(nextBossSpawnTime.get(bossName) != null) {
            nextSpawn = dateFormat.format(nextBossSpawnTime.get(bossName));

            /*if(new Date().getTime() > nextBossSpawnTime.get(bossName).getTime())
                note = " (Note: This time has passed)";*/
        }

        return "\n" + bold(bossName) + " will respawn next at " + codeBlock(nextSpawn) + note;
    }

    public static String currentHunters(String bossName) {
        List<User> huntersList = bossHunters.get(bossName);
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += codeBlock(huntersList.get(i).getName());
            if(i != huntersList.size() - 1)
                huntersString += ", ";
        }

        if(huntersString.isEmpty())
            huntersString = codeBlock("None");

        return "\nCurrent Hunters: " + huntersString;
    }

    public static String notifyingHunters(String bossName) {
        List<User> huntersList = bossHunters.get(bossName);
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getAsMention();
            if(i != huntersList.size() - 1)
                huntersString += ", ";
        }
        /*if(huntersString.isEmpty())
            huntersString = codeBlock("None");*/
        return huntersString;
    }

    public static ArrayList<String> changeAbbreviations(String bossLine) {
        bossLine = bossLine.toUpperCase();

        for(String bossName : bossNamesFinal)
            if (bossName.contains(" "))
                bossLine = bossLine.replace(bossName, bossName.split(" ")[0]);

        ArrayList<String> bossNames = new ArrayList<>(Arrays.asList(bossLine.split(" ")));

        for(int i = 0; i < bossNames.size(); i++) {
            String bossName = bossNames.get(i);
            bossName = bossName.replace("GS", bossNamesFinal[0]);
            bossName = bossName.replace("WB", bossNamesFinal[1]);
            bossName = bossName.replace("WC", bossNamesFinal[4]);
            bossName = bossName.replace("RED BEE", bossNamesFinal[8]);
            bossName = bossName.replace("RB", bossNamesFinal[8]);
            bossName = bossName.replace("ASSASSIN", bossNamesFinal[9]);
            bossName = bossName.replace("ASSASAIN", bossNamesFinal[9]);
            if (bossName.equals("ASS"))
                bossName = bossName.replace("ASS", bossNamesFinal[9]);
            bossName = bossName.replace("DA", bossNamesFinal[9]);
            bossName = bossName.replace("BM", bossNamesFinal[13]);
            bossName = bossName.replace("LM", bossNamesFinal[14]);
            bossName = bossName.replace("EB", bossNamesFinal[18]);
            bossName = bossName.replace("SG", bossNamesFinal[19]);
            if (bossName.equals("LACOS"))
                bossName = bossName.replace("LACOS", bossNamesFinal[20]);

            for(String eachBossName : bossNamesFinal)
                if (eachBossName.contains(" ") && bossName.equals(eachBossName.split(" ")[0]) && !bossName.equals(eachBossName))
                    bossName = bossName.replace(eachBossName.split(" ")[0], eachBossName);

            bossNames.set(i, bossName);
        }

        ArrayList<String> removedBossNames = new ArrayList<>();
        for(String bossName : bossNames)
            if(!new ArrayList<>(Arrays.asList(bossNamesFinal)).contains(bossName))
                removedBossNames.add(bossName);
        bossNames.removeAll(removedBossNames);

        return bossNames;
    }

    public static String getTitle(int killCount) {
        String title = "";
        if (killCount < 30)
            title = "Recruit";
        else if (killCount < 100)
            title = "Scout";
        else if (killCount < 300)
            title = "Combat Soldier";
        else if (killCount < 600)
            title = "Veteran Solider";
        else if (killCount < 1000)
            title = "Apprentice Knight";
        else if (killCount < 2000)
            title = "Fighter";
        else if (killCount < 5000)
            title = "Elite Fighter";
        else if (killCount < 10000)
            title = "Field Commander";
        else
            title = "General";
        return title;
    }

    public static String codeBlock(String messageString) {
        if(!messageString.isEmpty())
            return "`" + messageString + "`";
        else
            return codeBlock("None");
    }

    public static String bold(String messageString) {
        return "**" + messageString + "**";
    }
}
