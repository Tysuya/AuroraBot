package aurora.commands;

import net.dv8tion.jda.core.entities.User;

import java.util.*;

public class Boss {
    String bossName;
    int bossRespawnTime;
    List<User> bossHunters;
    Date nextBossSpawnTime;
    ArrayList<String> bossHistory;
    HashMap<String, Integer> bossKills;
    int bossOverallKills;
    Timer bossSpawnTimer;

    public Boss(String bossName) {
        this.bossName = bossName;

    }
}
