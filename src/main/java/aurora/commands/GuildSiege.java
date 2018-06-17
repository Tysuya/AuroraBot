package aurora.commands;

import java.util.*;

import static aurora.commands.BossAbstract.announcementsChannel;

public class GuildSiege {
    private static Timer guildSiegeTimer = new Timer();

    public static void guildSiege() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Checking for Guild Siege...");

                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("PST"));
                int date = calendar.get(Calendar.DAY_OF_WEEK);
                if (date == 3 || date == 5 || date == 7)
                    startGuildSiegeTimer();
            }
        }, 0, 21600000);
    }

    private static void startGuildSiegeTimer() {
        guildSiegeTimer.cancel();
        guildSiegeTimer = new Timer();
        guildSiegeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("PST"));
                if (calendar.get(Calendar.SECOND) == 0) {
                    if (calendar.get(Calendar.HOUR_OF_DAY) == 19)
                        if (calendar.get(Calendar.MINUTE) == 30)
                            announcementsChannel.sendMessage("@everyone The Guild Siege will begin in `1` hour!").queue();

                    if (calendar.get(Calendar.HOUR_OF_DAY) == 20) {
                        if (calendar.get(Calendar.MINUTE) == 20)
                            announcementsChannel.sendMessage("@everyone The Guild Siege will begin in `10` minutes!").queue();
                        if (calendar.get(Calendar.MINUTE) == 30) {
                            announcementsChannel.sendMessage("@everyone The Guild Siege has begun!").queue();
                            guildSiegeTimer.cancel();
                        }
                    }
                }
            }
        }, 0, 1000);
    }
}
