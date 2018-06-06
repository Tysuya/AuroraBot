package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import net.dv8tion.jda.core.entities.User;

import java.util.*;

import static aurora.commands.BossAbstract.*;

public class Boss {
    private String bossName;
    private int respawnTime;
    private List<User> hunters;
    private Date nextSpawnTime;
    private String history;
    private HashMap<String, Integer> kills;
    private int overallKills;
    private Timer spawnTimer;

    public Boss(String bossName, int respawnTime) {
        this.bossName = bossName;
        this.respawnTime = respawnTime;
        hunters = new ArrayList<>();
        kills = new HashMap<>();
        spawnTimer = new Timer();
    }

    public void spawnTimer( Message bossReport) {
        getSpawnTimer().cancel();
        setSpawnTimer(new Timer());
        getSpawnTimer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (bossReport != null && getNextSpawnTime() != null) {
                    long time = (getNextSpawnTime().getTime() - Calendar.getInstance().getTimeInMillis()) / 1000;
                    long seconds = time % 60;
                    long minutes = time / 60 % 60;
                    long hours = time / 3600 % 24;

                    List<User> usersList = bossReport.getMentionedUsers();
                    List<String> usersString = new ArrayList<>();
                    for (User user : usersList)
                        usersString.add(user.getAsMention());

                    String messageString = bossReport.getContent().substring(bossReport.getContent().indexOf('\n') + 1);
                    if (usersList.isEmpty())
                        messageString = bossReport.getContent();

                    bossReport.editMessage(String.join(", ", usersString) + "\n" +
                            messageString +
                            "\nSpawn Timer: " + codeBlock(Long.toString(hours)) + " hours " + codeBlock(Long.toString(minutes)) + " minutes " + codeBlock(Long.toString(seconds)) + " seconds").complete();
                    /*System.out.println(bossName +
                            "\nSpawn Timer: " + codeBlock(Long.toString(hours)) + " hours " + codeBlock(Long.toString(minutes)) + " minutes " + codeBlock(Long.toString(seconds)) + " seconds");*/
                }
            }
        }, 0, 10000);
    }

    public String respawnTime() {
        String nextSpawn = "Unknown";
        String note = "";

        if (getNextSpawnTime() != null) {
            nextSpawn = dateFormat.format(getNextSpawnTime());
            if (new Date().getTime() > getNextSpawnTime().getTime())
                note = "\n(Note: " + bold(getBossName()) + " has been dropped)";
        }

        if (nextSpawn.equals("Unknown"))
            note = "\n(Note: " + bold(getBossName()) + " has been dropped)";

        return "\n" + bold(getBossName()) + " will respawn next at " + codeBlock(nextSpawn) + note;
    }

    public String currentHunters() {
        List<User> huntersList = getHunters();
        String huntersString = "";
        for (int i = 0; i < huntersList.size(); i++) {
            huntersString += codeBlock(huntersList.get(i).getName());
            if (i != huntersList.size() - 1)
                huntersString += ", ";
        }

        if (huntersString.isEmpty())
            huntersString = codeBlock("None");

        return "\nCurrent Hunters: " + huntersString;
    }

    public String notifyingHunters() {
        List<User> huntersList = getHunters();
        String huntersString = "";
        for (int i = 0; i < huntersList.size(); i++) {
            huntersString += huntersList.get(i).getAsMention();
            if (i != huntersList.size() - 1)
                huntersString += ", ";
        }
        /*if(huntersString.isEmpty())
            huntersString = codeBlock("None");*/
        return huntersString;
    }

    public void updateBossInfo() {
        List<Message> messageHistoryList = new MessageHistory(bossInfoChannel).retrievePast(100).complete();
        for (Message message : messageHistoryList) {
            String messageString = respawnTime() + currentHunters();

            if (message.getContent().contains(getBossName()) && !("\n" + message.getContent()).equals(messageString))
                message.editMessage(messageString).complete();
        }
    }

    public int getRespawnTime() {
        return respawnTime;
    }

    public String getBossName() {
        return bossName;
    }

    public List<User> getHunters() {
        return hunters;
    }

    public void setHunters(List<User> hunters) {
        this.hunters = hunters;
    }

    public Date getNextSpawnTime() {
        return nextSpawnTime;
    }

    public void setNextSpawnTime(Date nextSpawnTime) {
        this.nextSpawnTime = nextSpawnTime;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public HashMap<String, Integer> getKills() {
        return kills;
    }

    public void setKills(HashMap<String, Integer> kills) {
        this.kills = kills;
    }

    public int getOverallKills() {
        return overallKills;
    }

    public void setOverallKills(int overallKills) {
        this.overallKills = overallKills;
    }

    public Timer getSpawnTimer() {
        return spawnTimer;
    }

    public void setSpawnTimer(Timer spawnTimer) {
        this.spawnTimer = spawnTimer;
    }
}
