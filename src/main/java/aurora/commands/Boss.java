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
    static HashMap<String, Date> bossTimes = new HashMap<>();
    static MessageChannel messageChannel;

    public static void initialize() {
        bossRespawnTimes.put("GHOSTSNAKE", 30);
        bossRespawnTimes.put("SPIDEY", 30);
        bossRespawnTimes.put("WILDBOAR", 20);
        bossRespawnTimes.put("BERSERK GOSUMI", 20);
        bossRespawnTimes.put("BLOODY GOSUMI", 20);
        bossRespawnTimes.put("RAVEN", 20);
        bossRespawnTimes.put("BLASTER", 20);
        String[] bossNames = {"GHOSTSNAKE", "SPIDEY", "WILDBOAR", "BERSERK GOSUMI", "BLOODY GOSUMI", "RAVEN", "BLASTER"};
        for(int i = 0; i < bossNames.length; i++) {
            bossHunters.put(bossNames[i], new ArrayList<>());
            //bossTimes.put(bossNames[i], new Date());
        }

        try {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    String dateString = dateFormat.format(new Date());

                    for(int i = 0; i < bossNames.length; i++) {
                        String huntersString = "";
                        for(int j = 0; j < bossHunters.get(bossNames[i]).size(); j++) {
                            huntersString += bossHunters.get(bossNames[i]).get(j).getAsMention();
                            if(j != bossHunters.get(bossNames[i]).size() - 1) {
                                huntersString += ", ";
                            }
                        }

                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.MINUTE, 3);

                        String dateString2 = dateFormat.format(calendar.getTime());

                        if(bossTimes.get(bossNames[i]) != null) {
                            if(dateString2.equals(dateFormat.format(bossTimes.get(bossNames[i])))) {
                                messageChannel.sendMessage(huntersString + " " + bold(bossNames[i]) + " will respawn in 3 minutes! Don't forget to log in!").queue();
                            }

                            if(dateString.equals(dateFormat.format(bossTimes.get(bossNames[i])))) {

                                messageChannel.sendMessage(huntersString + " " + bold(bossNames[i]) + " has respawned! Find it and kill it!").queue();
                            }
                        }
                    }
                }
            }, 0, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void punchIn(MessageChannel channel, Message message) {
        messageChannel = channel;
        String bossName = changeAbbreviations(message.getContent().split("!pin ")[1].toUpperCase());
        if(bossName.contains(", ")) {
            String[] bossNames = bossName.split(", ");
            for(int i = 0; i < bossNames.length; i++) {
                addHunter(channel, bossNames[i], message.getAuthor());
            }
        }
        else {
            addHunter(channel, bossName, message.getAuthor());
        }
    }

    public static void addHunter(MessageChannel channel, String bossName, User hunter) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        String punchedStatus = hunter.getAsMention();
        if(!huntersList.contains(hunter)) {
            huntersList.add(hunter);
            punchedStatus += " just punched in for " + bossName;
        }
        else {
            punchedStatus += " has already been punched in for " + bossName;
        }
        bossHunters.put(bossName, huntersList);

        String messageString = punchedStatus + currentHunters(huntersList);
        channel.sendMessage(messageString + respawnTime(bossName)).queue();
    }

    public static void punchOut(MessageChannel channel, Message message) {
        String bossName = changeAbbreviations(message.getContent().split("!pout ")[1].toUpperCase());
        if(bossName.contains(", ")) {
            String[] bossNames = bossName.split(", ");
            for(int i = 0; i < bossNames.length; i++) {
                removeHunter(channel, bossNames[i], message.getAuthor());
            }
        }
        else {
            removeHunter(channel, bossName, message.getAuthor());
        }
    }

    public static void removeHunter(MessageChannel channel, String bossName, User hunter) {
        ArrayList<User> huntersList = bossHunters.get(bossName);
        ArrayList<User> newHuntersList = new ArrayList<>();

        for(int i = 0; i < huntersList.size(); i++) {
            if(!huntersList.get(i).equals(hunter)) {
                newHuntersList.add(huntersList.get(i));
            }
        }

        huntersList = newHuntersList;
        bossHunters.put(bossName, huntersList);

        String messageString = hunter.getAsMention() + " just punched out for " + bold(bossName) + ". Thanks for your service!";
        String messageString2 = currentHunters(huntersList);

        channel.sendMessage(messageString + messageString2).queue();
    }

    public static void report(MessageChannel channel, Message message) {
        messageChannel = channel;

        String[] report = message.getContent().split("!report ")[1].split(" ");
        System.out.println(Arrays.toString(report));
        String bossName = changeAbbreviations(report[0].toUpperCase());
        String timeOfDeath = "";

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        Calendar calendar = Calendar.getInstance();

        if(report.length > 1) {
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

        calendar.add(Calendar.MINUTE, bossRespawnTimes.get(bossName));

        String dateString = dateFormat.format(calendar.getTime());
        System.out.println(dateString);

        bossTimes.put(bossName, calendar.getTime());
        String huntersString = "";
        String[] bossNames = {"GHOSTSNAKE", "SPIDEY", "WILDBOAR", "BERSERK GOSUMI", "BLOODY GOSUMI", "RAVEN", "BLASTER"};

        for(int i = 0; i < bossNames.length; i++) {
            for(int j = 0; j < bossHunters.get(bossNames[i]).size(); j++) {
                huntersString += bossHunters.get(bossNames[i]).get(j).getAsMention();
                if(j != bossHunters.get(bossNames[i]).size() - 1) {
                    huntersString += ", ";
                }
            }
        }
        if(huntersString.isEmpty()) {
            huntersString = codeBlock("None");
        }

        String messageString = "Great job, " + message.getAuthor().getName() + "!";
        String messageString2 = "\nWhen it respawns, I will be notifying: " + huntersString;
        String messageString3 = currentHunters(bossHunters.get(bossName));
        channel.sendMessage(messageString + respawnTime(bossName) + messageString2 + messageString3).queue();
    }

    public static void check(MessageChannel channel, Message message) {
        String bossName = changeAbbreviations(message.getContent().split("!check ")[1].toUpperCase());
        channel.sendMessage(currentHunters(bossHunters.get(bossName)) + respawnTime(bossName)).queue();
    }

    public static String respawnTime(String bossName) {
        String nextSpawn = "Unknown";

        if(bossTimes.get(bossName) != null) {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss MM/dd");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            nextSpawn = dateFormat.format(bossTimes.get(bossName));
        }

        return "\n" + bold(bossName) + " will respawn next at " + codeBlock(nextSpawn);
    }

    public static String currentHunters(ArrayList<User> huntersList) {
        String huntersString = "";
        for(int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getName();
            if(i != huntersList.size() - 1) {
                huntersString += ", ";
            }
        }

        return  "\nCurrent Hunters: " + codeBlock(huntersString);
    }

    public static String changeAbbreviations(String bossName) {
        switch(bossName) {
            case "GS":
                bossName = bossName.replace("GS", "GHOSTSNAKE");
            case "WB":
                bossName = bossName.replace("WB", "WILDBOAR");
        }
        return bossName;
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
