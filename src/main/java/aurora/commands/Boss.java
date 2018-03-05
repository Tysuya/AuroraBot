package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tyler on 1/17/2017.
 */
public class Boss {
    static HashMap<String, Integer> bossRespawnTimes = new HashMap<>();
    static HashMap<String, ArrayList<User>> bossHunters = new HashMap<>();
    static HashMap<String, Date> nextBossSpawnTime = new HashMap<>();
    static MessageChannel messageChannel;
    static HashMap<String, Message> bossReport = new HashMap<>();
    static HashMap<String, ArrayList<String>> bossHistory = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> bossKills = new HashMap<>();

    public static void initialize() {
        bossRespawnTimes.put("GHOSTSNAKE", 30);
        bossRespawnTimes.put("WILDBOAR", 20);
        bossRespawnTimes.put("SPIDEY", 20);
        bossRespawnTimes.put("BERSERK GOSUMI", 20);
        bossRespawnTimes.put("BLOODY GOSUMI", 20);
        bossRespawnTimes.put("RAVEN", 20);
        bossRespawnTimes.put("BLASTER", 20);
        String[] bossNames = {"GHOSTSNAKE", "WILDBOAR", "SPIDEY", "BERSERK GOSUMI", "BLOODY GOSUMI", "RAVEN", "BLASTER"};


        for(String bossName : bossNames) {
            bossHunters.put(bossName, new ArrayList<>());
            bossHistory.put(bossName, new ArrayList<>());
            bossKills.put(bossName, new HashMap<>());
            //nextBossSpawnTime.put(bossNames[i], new Date());
        }

        HashMap<String, Integer> a = new HashMap<>();
        a.put("Jenny", 10);
        a.put("dandera", 7);
        a.put("Vampy", 2);
        a.put("Aversionist", 2);
        a.put("Syeira A.F (Bleu1mage/Angelkar)", 1);
        bossKills.put("GHOSTSNAKE", a);
        HashMap<String, Integer> b = new HashMap<>();
        b.put("Vampy", 2);
        b.put("2mutchKek", 1);
        bossKills.put("BERSERK GOSUMI", b);
        HashMap<String, Integer> c = new HashMap<>();

        c.put("dandera", 3);
        bossKills.put("SPIDEY", c);

        // Check every second if boss respawned
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String dateString = dateFormat.format(new Date());

                    for(String bossName : bossNames) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, 3);

                        String dateString2 = dateFormat.format(calendar.getTime());

                        if(nextBossSpawnTime.get(bossName) != null) {
                            if(dateString2.equals(dateFormat.format(nextBossSpawnTime.get(bossName)))) {
                                messageChannel.sendMessage(notifyingHunters(bossName) + "\n" + bold(bossName) + " will respawn in " + codeBlock("3") + " minutes! Don't forget to log in!").queue();
                            }

                            if(dateString.equals(dateFormat.format(nextBossSpawnTime.get(bossName)))) {
                                messageChannel.sendMessage(notifyingHunters(bossName) + "\n" + bold(bossName) + " has respawned! Find it and kill it!").queue();
                            }
                        }
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Update boss timer in chat
        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    for(String bossName : bossNames) {

                        if(bossReport.get(bossName) != null) {
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

    public static void punchIn(MessageChannel channel, Message message) {
        messageChannel = channel;
        String[] bossNames = changeAbbreviations(message.getContent().split("!pin ")[1]);
        System.out.println(Arrays.toString(bossNames));

        for(String bossName : bossNames) {
            if (bossName.contains("@"))
                break;
            if(message.getMentionedUsers().isEmpty())
                addHunter(channel, bossName, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers()) {
                    addHunter(channel, bossName, hunter);
                }
        }
    }

    public static void addHunter(MessageChannel channel, String bossName, User hunter) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        if(huntersList == null)
            huntersList = new ArrayList<>();

        String punchedStatus = codeBlock(hunter.getName());
        if(!huntersList.contains(hunter)) {
            huntersList.add(hunter);
            punchedStatus += " just punched in for " + bold(bossName);
        }
        else {
            punchedStatus += " has already been punched in for " + bold(bossName);
        }
        bossHunters.put(bossName, huntersList);

        channel.sendMessage(punchedStatus + respawnTime(bossName) + currentHunters(bossName)).queue();
    }

    public static void punchOut(MessageChannel channel, Message message) {
        String[] bossNames = changeAbbreviations(message.getContent().split("!pout ")[1]);

        for(String bossName : bossNames) {
            if (bossName.contains("@"))
                break;
            if(message.getMentionedUsers().isEmpty())
                removeHunter(channel, bossName, message.getAuthor());
            else
                for(User hunter : message.getMentionedUsers())
                    removeHunter(channel, bossName, hunter);
        }
    }

    public static void removeHunter(MessageChannel channel, String bossName, User hunter) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        ArrayList<User> newHuntersList = new ArrayList<>();
        if(huntersList == null)
            huntersList = new ArrayList<>();

        for(User hunterInList : huntersList) {
            if(!hunterInList.equals(hunter)) {
                newHuntersList.add(hunterInList);
            }
        }

        huntersList = newHuntersList;
        bossHunters.put(bossName, huntersList);

        String messageString = hunter.getName() + " just punched out for " + bold(bossName) + ". Thanks for your service!";

        channel.sendMessage(messageString + currentHunters(bossName)).queue();
    }

    public static void report(MessageChannel channel, Message message) {
        messageChannel = channel;

        String[] report = changeAbbreviations(message.getContent().split("!report ")[1]);

        String bossName = report[0];
        String timeOfDeath = "";

        Calendar calendar = Calendar.getInstance();

        if(report.length > 1 && !report[1].contains("LOST") && !report[1].contains("@")) {
            timeOfDeath = report[1];
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

        //channel.sendMessage(messageString + respawnTime(bossName) + messageString2 + messageString3).queue();
    }

    public static void reset(MessageChannel channel, Message message) {
        String[] bossNames = changeAbbreviations(message.getContent().split("!reset ")[1]);

        for(String bossName : bossNames) {
            nextBossSpawnTime.put(bossName, null);
            bossReport.put(bossName, null);
            channel.sendMessage(bold(bossName) + "'s respawn timer has been reset").queue();
        }
    }

    public static void history(MessageChannel channel, Message message) {
        String[] bossNames = changeAbbreviations(message.getContent().split("!history ")[1]);

        for(String bossName : bossNames) {
            String bossHistoryString = "";
            for (String historyString : bossHistory.get(bossName)) {
                bossHistoryString += "\n" + historyString;
            }
            if (bossHistoryString.isEmpty()) {
                bossHistoryString = " ";
            }
            channel.sendMessage("History for " + bold(bossName) + ":```" + bossHistoryString + "```").queue();
        }
    }

    public static void kills(MessageChannel channel, Message message) {
        String[] bossNames = changeAbbreviations(message.getContent().split("!kills ")[1]);

        for(String bossName : bossNames) {
            String bossKillsString = "";

            HashMap<String, Integer> killsHashMap = bossKills.get(bossName);
            /*System.out.println(killsHashMap.toString());
            Map<Integer, String> swapped = killsHashMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
            Object[] killCounts = swapped.keySet().toArray();
            Arrays.sort(killCounts);
            Collections.reverse(Arrays.asList(killCounts));
            System.out.println(Arrays.toString(killCounts));*/

            Object[] entrySet = killsHashMap.entrySet().toArray();
            Arrays.sort(entrySet, new Comparator() {
                public int compare(Object o1, Object o2) {
                    return ((Map.Entry<String, Integer>) o2).getValue().compareTo(((Map.Entry<String, Integer>) o1).getValue());
                }
            });

            int place = 1;
            for (Object entry : entrySet) {
                killsHashMap.put(((Map.Entry<String, Integer>) entry).getKey(), ((Map.Entry<String, Integer>) entry).getValue());
                String name = ((Map.Entry<String, Integer>) entry).getKey();
                int killCount = ((Map.Entry<String, Integer>) entry).getValue();
                bossKillsString += "\n" + place++ + ") " + name + ": " + killCount;
            }

            if (bossKillsString.isEmpty())
                bossKillsString = " ";

            channel.sendMessage("Kills for " + bold(bossName) + ":```" + bossKillsString + "```").queue();
        }
    }

    public static void check(MessageChannel channel, Message message) {
        String[] bossNames = changeAbbreviations(message.getContent().split("!check ")[1]);

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        for(String bossName : bossNames) {
            channel.sendMessage(respawnTime(bossName) +
                    "\nCurrent Time: " + codeBlock(dateFormat.format(new Date())) +
                    currentHunters(bossName)).queue();
        }
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

    public static String[] changeAbbreviations(String bossLine) {
        bossLine = bossLine.toUpperCase();

        bossLine = bossLine.replace("GS", "GHOSTSNAKE");
        bossLine = bossLine.replace("WB", "WILDBOAR");
        bossLine = bossLine.replace("BERSERK GOSUMI", "BERSERK");
        bossLine = bossLine.replace("BLOODY GOSUMI", "BLOOY");
        bossLine = bossLine.replace("RED BEE", "RED");

        String[] bossNames = bossLine.split(" ");

        for(int i = 0; i < bossNames.length; i++) {
            String bossName = bossNames[i];
            switch (bossName) {
                case "BERSERK":
                    bossName = bossName.replace("BERSERK", "BERSERK GOSUMI");
                case "BLOODY":
                    bossName = bossName.replace("BLOODY", "BLOODY GOSUMI");
                case "RED":
                    bossName = bossName.replace("RED", "RED BEE");
            }
            bossNames[i] = bossName;
        }
        return bossNames;
    }

    public static String codeBlock(String messageString) {
        if(!messageString.isEmpty()) {
            return "`" + messageString + "`";
        }
        else {
            return codeBlock("None");
        }
    }

    public static String bold(String messageString) {
        return "**" + messageString + "**";
    }
}
