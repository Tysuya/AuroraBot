package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tyler on 1/17/2017.
 */
public abstract class Boss {
    static HashMap<String, Integer> bossRespawnTimes = new HashMap<>();
    static HashMap<String, ArrayList<User>> bossHunters = new HashMap<>();
    static HashMap<String, Date> nextBossSpawnTime = new HashMap<>();
    static MessageChannel messageChannel;
    static HashMap<String, Message> bossReport = new HashMap<>();
    static HashMap<String, ArrayList<String>> bossHistory = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> bossKills = new HashMap<>();
    static HashMap<String, Message> bossKillsLog = new HashMap<>();
    static HashMap<String, Integer> bossOverallKills = new HashMap<>();

    static final String[] bossNamesFinal = {"GHOSTSNAKE", "WILDBOAR", "SPIDEY", "BERSERK GOSUMI", "BLOODY GOSUMI", "RAVEN", "BLASTER", "BSSSZSSS", "DESERT ASSASAIN", "STEALTH", "BUZSS", "BIZIZI"};

    public static void initialize() {
        try {
            //AuroraBot.jda.getTextChannelById("417803228764176385").sendMessage("Good morning, I just woke up! Please punch in and file your most recent reports again. I apologize for any inconveniences my restart caused ^^").queue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        bossRespawnTimes.put(bossNamesFinal[0], 30);
        bossRespawnTimes.put(bossNamesFinal[1], 20);
        bossRespawnTimes.put(bossNamesFinal[2], 20);
        bossRespawnTimes.put(bossNamesFinal[3], 20);
        bossRespawnTimes.put(bossNamesFinal[4], 20);
        bossRespawnTimes.put(bossNamesFinal[5], 20);
        bossRespawnTimes.put(bossNamesFinal[6], 20);
        bossRespawnTimes.put(bossNamesFinal[7], 30);
        bossRespawnTimes.put(bossNamesFinal[8], 20);
        bossRespawnTimes.put(bossNamesFinal[9], 20);
        bossRespawnTimes.put(bossNamesFinal[10], 20);
        bossRespawnTimes.put(bossNamesFinal[11], 20);

        for(String bossName : bossNamesFinal) {
            bossHunters.put(bossName, new ArrayList<>());
            bossHistory.put(bossName, new ArrayList<>());
            bossKills.put(bossName, new HashMap<>());
        }

        try {
            initializeKills();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check every second if boss respawned
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String currentDate = dateFormat.format(new Date());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, 3);
                    String fastDate = dateFormat.format(calendar.getTime());

                    for(String bossName : bossNamesFinal) {
                        if(nextBossSpawnTime.get(bossName) != null) {
                            if(fastDate.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
                                messageChannel.sendMessage(notifyingHunters(bossName) +
                                        "\n" + bold(bossName) + " will respawn in " + codeBlock("3") + " minutes! Don't forget to log in!").queue();

                            if(currentDate.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
                                messageChannel.sendMessage(notifyingHunters(bossName) +
                                        "\n" + bold(bossName) + " has respawned! Find it and kill it!").queue();
                        }
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Timer spawnTimer = new Timer();
    public static void spawnTimer() {
        try {
            spawnTimer.cancel();
            spawnTimer = new Timer();
            spawnTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for(String bossName : bossNamesFinal) {
                        if(bossReport.get(bossName) != null && nextBossSpawnTime.get(bossName) != null) {
                            long time = (nextBossSpawnTime.get(bossName).getTime() - Calendar.getInstance().getTimeInMillis()) / 1000;
                            long seconds = time % 60;
                            long minutes = time / 60 % 60;

                            bossReport.get(bossName).editMessage(bossReport.get(bossName).getContent() +
                                    "\nSpawn Timer: " + codeBlock(Long.toString(minutes)) + " minutes " + codeBlock(Long.toString(seconds)) + " seconds").queue();
                        }
                    }
                }
            }, 0, 5000);
        } catch(Exception e) {
            e.printStackTrace();
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
        bossKills.put("GHOSTSNAKE", a);
        HashMap<String, Integer> b = new HashMap<>();
        b.put("Vampy", 2);
        b.put("2mutchKek", 1);
        bossKills.put("BERSERK GOSUMI", b);
        HashMap<String, Integer> c = new HashMap<>();
        c.put("dandera", 9);
        bossKills.put("SPIDEY", c);
        HashMap<String, Integer> d = new HashMap<>();
        d.put("dandera", 1);
        bossKills.put("WILDBOAR", d);*/

        HashMap<String, String> bossKillsIDS = new HashMap<>();
        bossKillsIDS.put(bossNamesFinal[0], "420069733711216661");
        bossKillsIDS.put(bossNamesFinal[1], "420069734080577536");
        bossKillsIDS.put(bossNamesFinal[2], "420069734021857283");
        bossKillsIDS.put(bossNamesFinal[3], "420069734651002880");
        bossKillsIDS.put(bossNamesFinal[4], "420069734982090752");
        bossKillsIDS.put(bossNamesFinal[5], "420069760739311627");
        bossKillsIDS.put(bossNamesFinal[6], "420069761125318676");
        bossKillsIDS.put(bossNamesFinal[7], "421561477829492738");
        bossKillsIDS.put(bossNamesFinal[8], "421561478328483850");
        bossKillsIDS.put(bossNamesFinal[9], "421561478567690240");
        bossKillsIDS.put(bossNamesFinal[10], "421561478815154176");
        bossKillsIDS.put(bossNamesFinal[11], "421561478882000897");

        for(String bossName : bossNamesFinal) {
            Message bossKillsMessage = AuroraBot.jda.getTextChannelById("420067387644182538").getMessageById(bossKillsIDS.get(bossName)).complete();
            String[] lines = bossKillsMessage.getContent().split("\\n");
            HashMap<String, Integer> bossKillsHashMap = new HashMap<>();
            for (int i = 1; i < lines.length - 1; i++) {
                int parenthesis = lines[i].indexOf(")");
                int colon = lines[i].indexOf(":");

                String name = lines[i].substring(parenthesis + 2, colon);
                Integer kills = Integer.parseInt(lines[i].substring(colon + 2));
                bossKillsHashMap.put(name, kills);
            }
            System.out.println(bossKillsHashMap.toString());
            System.out.println(bossKillsMessage.getContent());
            bossKills.put(bossName, bossKillsHashMap);
            bossKillsLog.put(bossName, bossKillsMessage);
        }
    }

    public static void updateOverallKills() {
        for(String bossName : bossNamesFinal) {
            Message bossKillsMessage = bossKillsLog.get(bossName);
            String[] lines = bossKillsMessage.getContent().split("\\n");
            for (int i = 1; i < lines.length - 1; i++) {
                int parenthesis = lines[i].indexOf(")");
                int colon = lines[i].indexOf(":");

                String name = lines[i].substring(parenthesis + 2, colon);
                Integer kills = Integer.parseInt(lines[i].substring(colon + 2));
                bossOverallKills.putIfAbsent(name, 0);
                bossOverallKills.put(name, bossOverallKills.get(name) + kills);
            }
        }
    }

    public static String respawnTime(String bossName) {
        String nextSpawn = "Unknown";
        String note = "";

        if(nextBossSpawnTime.get(bossName) != null) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            nextSpawn = dateFormat.format(nextBossSpawnTime.get(bossName));

            if(new Date().getTime() > nextBossSpawnTime.get(bossName).getTime())
                note = " (Note: This time has already passed)";
        }

        return "\n" + bold(bossName) + " will respawn next at " + codeBlock(nextSpawn) + note;
    }

    public static String currentHunters(String bossName) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getName();
            if(i != huntersList.size() - 1)
                huntersString += ", ";
        }

        if(huntersString.isEmpty())
            huntersString = codeBlock("None");

        return "\nCurrent Hunters: " + codeBlock(huntersString);
    }

    public static String notifyingHunters(String bossName) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
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

        bossLine = bossLine.replace("GS", bossNamesFinal[0]);
        bossLine = bossLine.replace("WB", bossNamesFinal[1]);
        bossLine = bossLine.replace("BERSERK GOSUMI", bossNamesFinal[3]);
        bossLine = bossLine.replace("BLOODY GOSUMI", bossNamesFinal[4]);
        bossLine = bossLine.replace("RED BEE", bossNamesFinal[7]);
        bossLine = bossLine.replace("RB", bossNamesFinal[7]);
        bossLine = bossLine.replace("ASSASSIN", bossNamesFinal[8]);
        bossLine = bossLine.replace("ASS", bossNamesFinal[8]);

        ArrayList<String> bossNames = new ArrayList<>(Arrays.asList(bossLine.split(" ")));

        for(int i = 0; i < bossNames.size(); i++) {
            switch (bossNames.get(i)) {
                case "BERSERK":
                    bossNames.set(i, bossNames.get(i).replace("BERSERK", bossNamesFinal[3]));
                case "BLOODY":
                    bossNames.set(i, bossNames.get(i).replace("BLOODY", bossNamesFinal[4]));
                case "DESERT":
                    bossNames.set(i, bossNames.get(i).replace("DESERT", bossNamesFinal[8]));
            }
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
