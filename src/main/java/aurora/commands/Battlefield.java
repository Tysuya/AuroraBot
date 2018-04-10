package aurora.commands;

import aurora.AuroraBot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Battlefield {
    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void battlefield() {
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String[] date = dateFormat.format(new Date()).split(":");
        String hour = date[0];
        String minute = date[1];
        String second = date[2];

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (Integer.parseInt(hour) % 2 != 0 && minute.equals("50") && second.equals("00")) {
                    try {
                        AuroraBot.jda.getTextChannelById("433153631127339018").sendMessage("Battlefield will begin in `10` minutes.").queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (Integer.parseInt(hour) % 2 != 0 && minute.equals("55") && second.equals("00")) {
                    try {
                        AuroraBot.jda.getTextChannelById("433153631127339018").sendMessage("Battlefield will begin in `5` minutes.").queue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 1000);
    }
}