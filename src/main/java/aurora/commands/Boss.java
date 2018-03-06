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

    static final String[] bossNamesFinal = {"GHOSTSNAKE", "WILDBOAR", "SPIDEY", "BERSERK GOSUMI", "BLOODY GOSUMI", "RAVEN", "BLASTER"};

    public static void initialize() {
        bossRespawnTimes.put(bossNamesFinal[0], 30);
        bossRespawnTimes.put(bossNamesFinal[1], 20);
        bossRespawnTimes.put(bossNamesFinal[2], 20);
        bossRespawnTimes.put(bossNamesFinal[3], 20);
        bossRespawnTimes.put(bossNamesFinal[4], 20);
        bossRespawnTimes.put(bossNamesFinal[5], 20);
        bossRespawnTimes.put(bossNamesFinal[6], 20);

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
                    String dateString = dateFormat.format(new Date());

                    for(String bossName : bossNamesFinal) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, 3);

                        String dateString2 = dateFormat.format(calendar.getTime());

                        if(nextBossSpawnTime.get(bossName) != null) {
                            if(dateString2.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
                                messageChannel.sendMessage(notifyingHunters(bossName) +
                                        "\n" + bold(bossName) + " will respawn in " + codeBlock("3") + " minutes! Don't forget to log in!").queue();

                            if(dateString.equals(dateFormat.format(nextBossSpawnTime.get(bossName))))
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
        spawnTimer.cancel();
        try {
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

        for(String bossName : bossNamesFinal)
            bossKillsLog.put(bossName, AuroraBot.jda.getTextChannelById("420067387644182538").getMessageById(bossKillsIDS.get(bossName)).complete());

        for(int i = 0; i < bossNamesFinal.length; i++) {
            String[] lines = bossKillsLog.get(bossNamesFinal[i]).getContent().split("\\n");
            HashMap<String, Integer> bossKillsHashMap = new HashMap<>();
            for (int j = 1; j < lines.length - 1; j++) {
                int parenthesis = lines[j].indexOf(")");
                int colon = lines[j].indexOf(":");

                String name = lines[j].substring(parenthesis + 2, colon);
                Integer kills = Integer.parseInt(lines[j].substring(colon + 2));
                bossKillsHashMap.put(name, kills);
            }
            System.out.println(bossKillsLog.get(bossNamesFinal[i]).getContent());
            System.out.println(bossKillsHashMap);
            bossKills.put(bossNamesFinal[i], bossKillsHashMap);
        }
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

        int totalKillCount = 0;
        int place = 1;
        for (Object entry : entrySet) {
            killsHashMap.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
            String name = ((Map.Entry<String, Integer>) entry).getKey();
            int killCount = ((Map.Entry<String, Integer>) entry).getValue();
            totalKillCount += killCount;
            bossKillsString += "\n" + place++ + ") " + name + ": " + killCount;
        }

        if (bossKillsString.isEmpty())
            bossKillsString = " ";

        return "Total kills for " + bold(bossName) + ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + bossKillsString + "\n```";
    }

    public static String respawnTime(String bossName) {
        String nextSpawn = "Unknown";
        String note = "";

        if(nextBossSpawnTime.get(bossName) != null) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            nextSpawn = dateFormat.format(nextBossSpawnTime.get(bossName));

            if(new Date().getTime() > nextBossSpawnTime.get(bossName).getTime()) {
                note = " (Note: This time has already passed)";
            }
        }

        return "\n" + bold(bossName) + " will respawn next at " + codeBlock(nextSpawn) + note;
    }

    public static String currentHunters(String bossName) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getName();
            if(i != huntersList.size() - 1) {
                huntersString += ", ";
            }
        }

        if(huntersString.isEmpty()) {
            huntersString = codeBlock("None");
        }

        return "\nCurrent Hunters: " + codeBlock(huntersString);
    }

    public static String notifyingHunters(String bossName) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getAsMention();
            if(i != huntersList.size() - 1) {
                huntersString += ", ";
            }
        }
        /*if(huntersString.isEmpty())
            huntersString = codeBlock("None");*/
        return huntersString;
    }

    public static ArrayList<String> changeAbbreviations(String bossLine) {
        bossLine = bossLine.toUpperCase();

        bossLine = bossLine.replace("GS", "GHOSTSNAKE");
        bossLine = bossLine.replace("WB", "WILDBOAR");
        bossLine = bossLine.replace("BERSERK GOSUMI", "BERSERK");
        bossLine = bossLine.replace("BLOODY GOSUMI", "BLOOY");
        bossLine = bossLine.replace("RED BEE", "RED");

        ArrayList<String> bossNames = new ArrayList<>(Arrays.asList(bossLine.split(" ")));

        for(int i = 0; i < bossNames.size(); i++) {
            switch (bossNames.get(i)) {
                case "BERSERK":
                    bossNames.set(i, bossNames.get(i).replace("BERSERK", "BERSERK GOSUMI"));
                case "BLOODY":
                    bossNames.set(i, bossNames.get(i).replace("BLOODY", "BLOODY GOSUMI"));
                case "RED":
                    bossNames.set(i, bossNames.get(i).replace("RED", "RED BEE"));
            }
        }
        ArrayList<String> toRemove = new ArrayList<>();
        for(String bossName : bossNames)
            if(!new ArrayList<>(Arrays.asList(bossNamesFinal)).contains(bossName))
                toRemove.add(bossName);
        bossNames.removeAll(toRemove);

        return bossNames;
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
