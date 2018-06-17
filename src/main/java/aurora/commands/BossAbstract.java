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
public abstract class BossAbstract {
    static HashMap<String, Integer> hunterOverallKills = new HashMap<>();
    static int auroraOverallKills = 0;
    static Dropbox dropbox = new Dropbox();
    static ArrayList<Boss> bossList = new ArrayList<>();

    public static MessageChannel bossHuntersChannel = AuroraBot.jda.getTextChannelById("418683981291192331");
    public static MessageChannel leaderboardChannel = AuroraBot.jda.getTextChannelById("420058966257827841");
    public static MessageChannel bossInfoChannel = AuroraBot.jda.getTextChannelById("422636412702031873");
    public static MessageChannel announcementsChannel = bossHuntersChannel;

    static final String[] bossNamesFinal = {"GHOSTSNAKE", "WILDBOAR", "SPIDEY", "BERSERK GOSUMI", "WHITE CROW", "BLOODY GOSUMI", "RAVEN", "BLASTER", "BSSSZSSS", "DESERT ASSASAIN", "STEALTH", "BUZSS", "BIZIZI", "BIGMOUSE", "LESSER MADMAN", "SHAAACK", "SUUUK", "SUSUSUK", "ELDER BEHOLDER", "SANDGRAVE", "CHIEF MAGIEF", "MAGMA SENIOR THIEF", "BBINIKJOE", "BURNING STONE", "ELEMENTAL QUEEN", "TWISTER", "MAELSTROM", "SWIRL FLAME", "TANK", "STEAMPUNK", "LANDMINE", "TITANIUM GOLEM", "LACOSTEZA", "BLACKSKULL", "TURTLE Z"};

    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd z");

    public static void initialize() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        setRespawnTimes();

        if (!AuroraBot.debugMode) {
            bossHuntersChannel = AuroraBot.jda.getTextChannelById("417803228764176385");
            leaderboardChannel = AuroraBot.jda.getTextChannelById("420067387644182538");
            bossInfoChannel = AuroraBot.jda.getTextChannelById("422701643566678016");
            announcementsChannel = AuroraBot.jda.getTextChannelById("418818283102404611");

            System.out.println("Initializing history...");
            for (Boss boss : bossList)
                boss.setHistory(dropbox.readHistory(boss.getBossName(), "History"));

            System.out.println("Initializing kills...");
            initializeKills();
        }

        System.out.println("Initializing hunters...");
        initializeHunters();

        // Check every second if boss has respawned
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String currentDate = dateFormat.format(new Date());

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.MINUTE, 3);
                    String fastDate = dateFormat.format(calendar.getTime());

                    for (Boss boss : bossList) {
                        if (boss.getNextSpawnTime() != null) {
                            if (fastDate.equals(dateFormat.format(boss.getNextSpawnTime())))
                                boss.spawnTimer(bossHuntersChannel.sendMessage(boss.notifyingHunters() +
                                        "\n" + bold(boss.getBossName()) + " will respawn in " + codeBlock("3") + " minutes! Don't forget to log in!").complete());

                            if (currentDate.equals(dateFormat.format(boss.getNextSpawnTime()))) {
                                boss.spawnTimer(bossHuntersChannel.sendMessage(boss.notifyingHunters() +
                                        "\n" + bold(boss.getBossName()) + " has respawned! Find it and kill it!").complete());
                                boss.updateBossInfo();
                            }
                        }
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setRespawnTimes() {
        for (String bossName : bossNamesFinal) {
            int bossRespawnTime = 20;
            if (Arrays.asList("LACOSTEZA").contains(bossName))
                bossRespawnTime = 1;
            if (Arrays.asList("GHOSTSNAKE", "WHITE CROW", "BSSSZSSS", "SHAAACK", "CHIEF MAGIEF").contains(bossName))
                bossRespawnTime = 30;
            if (Arrays.asList("TWISTER", "MAELSTROM", "SWIRL FLAME", "TANK", "LANDMINE").contains(bossName))
                bossRespawnTime = 58;
            if (Arrays.asList("TITANIUM GOLEM").contains(bossName))
                bossRespawnTime = 59;
            if (Arrays.asList("BLACKSKULL").contains(bossName))
                bossRespawnTime = 65;
            if (Arrays.asList("ELEMENTAL QUEEN", "STEAMPUNK").contains(bossName))
                bossRespawnTime = 150;
            if (Arrays.asList("TURTLE Z").contains(bossName))
                bossRespawnTime = 333;
            bossList.add(new Boss(bossName, bossRespawnTime));
        }
    }

    public static void initializeHunters() {
        List<Message> messageHistoryList = new MessageHistory(bossInfoChannel).retrievePast(100).complete();
        for (Message message : messageHistoryList) {
            for (Boss boss : bossList) {
                if (message.getContent().contains(boss.getBossName())) {
                    String[] lines = message.getContent().replace("`", "").split("\n");
                    try {
                        if (!lines[0].split("at ")[1].equals("Unknown")) {
                            Date time = dateFormat.parse(lines[0].split("at ")[1]);
                            /*if (!TimeZone.getTimeZone("PST").inDaylightTime(time))
                                time.setHours(time.getHours() + 1);*/
                            if (AuroraBot.debugMode)
                                time.setHours(time.getHours() + 1);
                            time.setYear(new Date().getYear());
                            //System.out.println(boss.getBossName() + " | " + time);
                            boss.setNextSpawnTime(time);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!lines[lines.length - 1].split(": ")[1].contains("None")) {
                        String[] hunters = lines[lines.length - 1].split(": ")[1].split(", ");
                        List<User> huntersList = new ArrayList<>();
                        for (String hunter : hunters)
                            huntersList.addAll(AuroraBot.jda.getUsersByName(hunter, true));
                        boss.setHunters(huntersList);
                    }
                }
            }
        }
    }

    public static void initializeKills() {
        for (Boss boss : bossList) {
            String[] bossKillsLines = dropbox.readHistory(boss.getBossName(), "Leaderboard").split("\nTotal");
            //System.out.println(Arrays.toString(bossKillsLines));

            for (String bossKillsLine : bossKillsLines) {
                String[] huntersLine = bossKillsLine.split("\n");
                //System.out.println(Arrays.toString(huntersLine));

                HashMap<String, Integer> bossKillsHashMap = parseKills(huntersLine);

                String bossName = "";
                for (String eachBossName : bossNamesFinal)
                    if (huntersLine[0].contains(eachBossName))
                        bossName = eachBossName;
                //System.out.println(bossName + " = " + bossKillsHashMap);

                if (!bossName.isEmpty())
                    bossList.get(Arrays.asList(bossNamesFinal).indexOf(bossName)).setKills(bossKillsHashMap);
            }
        }
    }

    public static String getKills(Boss boss) {
        HashMap<String, Integer> killsHashMap = boss.getKills();

        Object[] info = generateKills(killsHashMap);
        String bossKillsString = (String) info[0];
        int totalKillCount = (int) info[1];

        boss.setOverallKills(totalKillCount);

        return "\nTotal kills for " + bold(boss.getBossName()) + ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + bossKillsString + "\n```";
    }

    public static String getOverallKills() {
        hunterOverallKills.clear();
        for (Boss boss : bossList) {
            String[] huntersLine = getKills(boss).split("\n");
            // Remove first element
            huntersLine = Arrays.copyOfRange(huntersLine, 1, huntersLine.length);

            HashMap<String, Integer> bossKillsHashMap = parseKills(huntersLine);
            for (String key : bossKillsHashMap.keySet()) {
                hunterOverallKills.putIfAbsent(key, 0);
                hunterOverallKills.put(key, hunterOverallKills.get(key) + bossKillsHashMap.get(key));
            }
        }

        Object[] info = generateKills(hunterOverallKills);
        String overallKillsString = (String) info[0];
        int totalKillCount = (int) info[1];

        auroraOverallKills = totalKillCount;

        return "Total overall kills for " + bold("AURORA") + ": " + codeBlock(Integer.toString(totalKillCount)) + " ```" + overallKillsString + "\n```";
    }

    public static HashMap<String, Integer> parseKills(String[] huntersLine) {
        //System.out.println(name);
                        /*if (name.equals("Domm1e/Henry"))
                            name = "narrak/Henry";*/
        HashMap<String, Integer> bossKillsHashMap = new HashMap<>();
        for (int i = 1; i < huntersLine.length - 1; i++) {
            int parenthesis = huntersLine[i].indexOf(")");
            int colon = huntersLine[i].indexOf(":");
            int dash = huntersLine[i].length() + 1;
            if (huntersLine[i].contains("-"))
                dash = huntersLine[i].indexOf("-");

            String name = huntersLine[i].substring(parenthesis + 2, colon).trim();
            Integer kills = Integer.parseInt(huntersLine[i].substring(colon + 2, dash - 1));

            bossKillsHashMap.put(name, kills);
        }
        return bossKillsHashMap;
    }

    public static Object[] generateKills(HashMap<String, Integer> killsHashMap) {
        Object[] entrySet = killsHashMap.entrySet().toArray();
        Arrays.sort(entrySet, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
            }
        });

        int totalKillCount = 0;
        for (Object entry : entrySet)
            totalKillCount += ((Map.Entry<String, Integer>) entry).getValue();

        String killsString = "";
        int rank = 1;
        for (Object entry : entrySet) {
            killsHashMap.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
            String name = ((Map.Entry<String, Integer>) entry).getKey();
            int killCount = ((Map.Entry<String, Integer>) entry).getValue();
            double percent = Math.round(killCount * 1000d / totalKillCount) / 10d;
            /*String a = rank++ + ") " + name + ": ";
            String b = String.format("%-25s" + killCount + " - " + percent + "%%", a);
            System.out.println(b);*/
            killsString += "\n" + rank++ + ") " + name + ": " + killCount;
        }
        if (killsString.isEmpty())
            killsString = " ";
        return new Object[]{killsString, totalKillCount};
    }

    public static ArrayList<Boss> changeAbbreviations(String bossLine) {
        bossLine = bossLine.toUpperCase();

        for (String bossName : bossNamesFinal)
            if (bossName.contains(" "))
                bossLine = bossLine.replace(bossName, bossName.split(" ")[0]);

        ArrayList<String> bossNames = new ArrayList<>(Arrays.asList(bossLine.split(" ")));
        HashMap<String, List<String>> replacements = new HashMap<>();
        replacements.put(bossNamesFinal[0], new ArrayList<>(Arrays.asList("GS")));
        replacements.put(bossNamesFinal[1], new ArrayList<>(Arrays.asList("WB")));
        replacements.put(bossNamesFinal[8], new ArrayList<>(Arrays.asList("RED BEE", "RB")));
        replacements.put(bossNamesFinal[9], new ArrayList<>(Arrays.asList("ASSASSIN", "ASSASAIN", "ASS")));
        replacements.put(bossNamesFinal[13], new ArrayList<>(Arrays.asList("BM")));
        replacements.put(bossNamesFinal[19], new ArrayList<>(Arrays.asList("SG")));
        replacements.put(bossNamesFinal[22], new ArrayList<>(Arrays.asList("BB", "JOE")));
        replacements.put(bossNamesFinal[24], new ArrayList<>(Arrays.asList("QUEEN")));
        replacements.put(bossNamesFinal[27], new ArrayList<>(Arrays.asList("FLAME")));
        replacements.put(bossNamesFinal[29], new ArrayList<>(Arrays.asList("STEAM", "PUNK", "SP")));
        replacements.put(bossNamesFinal[30], new ArrayList<>(Arrays.asList("LAND", "MINE")));
        replacements.put(bossNamesFinal[31], new ArrayList<>(Arrays.asList("TIT", "GOLEM")));
        replacements.put(bossNamesFinal[32], new ArrayList<>(Arrays.asList("LACOS")));
        replacements.put(bossNamesFinal[33], new ArrayList<>(Arrays.asList("BS")));
        replacements.put(bossNamesFinal[34], new ArrayList<>(Arrays.asList("TZ")));

        for (String bossName : bossNamesFinal) {
            if (bossName.contains(" ")) {
                String[] names = bossName.split(" ");
                String initials = "";
                for (String name : names)
                    initials += name.charAt(0);
                replacements.putIfAbsent(bossName, new ArrayList<>());
                if (!Arrays.asList("BERSERK GOSUMI", "BLOODY GOSUMI", "BURNING STONE").contains(bossName))
                    replacements.get(bossName).add(initials);
                replacements.get(bossName).add(bossName.split(" ")[0]);
            }
        }

        ArrayList<String> removedBossNames = new ArrayList<>();
        for (int i = 0; i < bossNames.size(); i++) {
            String bossName = bossNames.get(i);
            for (String eachBossName : bossNamesFinal) {
                if (replacements.get(eachBossName) != null && replacements.get(eachBossName).contains(bossName))
                    bossName = eachBossName;
                bossNames.set(i, bossName);
            }
            if (!new ArrayList<>(Arrays.asList(bossNamesFinal)).contains(bossName))
                removedBossNames.add(bossName);
        }
        bossNames.removeAll(removedBossNames);

        ArrayList<Boss> bosses = new ArrayList<>();
        for (String bossName : bossNames)
            bosses.add(bossList.get(Arrays.asList(bossNamesFinal).indexOf(bossName)));

        return bosses;
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
        if (!messageString.isEmpty())
            return "`" + messageString + "`";
        else
            return codeBlock("None");
    }

    public static String bold(String messageString) {
        return "**" + messageString + "**";
    }
}
