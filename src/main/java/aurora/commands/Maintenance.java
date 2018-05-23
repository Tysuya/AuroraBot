package aurora.commands;

import aurora.AuroraBot;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageHistory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Maintenance {
    private static MessageChannel channel = AuroraBot.jda.getTextChannelById("418818283102404611");
    private static Date maintenanceStart = new Date();
    private static Date maintenanceEnd = new Date();
    private static String maintenanceInfo = "";
    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static boolean sentMaintenance = false;
    private static Timer maintenanceTimer = new Timer();

    public static void maintenance() {
        if (AuroraBot.debugMode)
            channel = BossAbstract.bossHuntersChannel;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Checking for maintenance...");
                checkForMaintenance();
                startMaintenanceTimer();
            }
        }, 0, 21600000);
    }

    private static void checkForMaintenance() {
        try {
            // Parse start and end times
            Document document = Jsoup.connect("https://www.withhive.com/game/desc/32/22").get();
            if (!document.toString().contains("[Maintenance]"))
                return;
            Element rawHTML = document.select("ul.list_notice > li > a > span").first();
            String[] info = rawHTML.text().substring(13).split(" ");
            String month = info[0];
            String day = info[1].replace("st", "").replace("nd", "").replace("rd", "").replace("th", "");
            String startTime = info[3].substring(1);
            String endTime = info[5];
            String timeZone = info[6].substring(0, info[6].length() - 1);
            String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR));
            maintenanceStart = new SimpleDateFormat("MMMM dd hh:mmaa zzz yyyy").parse(month + " " + day + " " + startTime + " " + timeZone + " " + year);
            maintenanceEnd = new SimpleDateFormat("MMMM dd hh:mmaa zzz yyyy").parse(month + " " + day + " " + endTime + " " + timeZone + " " + year);
            // In case maintenance goes past midnight
            if (maintenanceEnd.getTime() < maintenanceStart.getTime()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(maintenanceEnd);
                cal.add(Calendar.DATE, 1);
                maintenanceEnd = cal.getTime();
            }
            // Parse maintenance details
            document = Jsoup.connect("https://www.withhive.com" + document.select("ul.list_notice > li > a").first().attr("href")).get();
            Element rawHTML2 = document.select("div.article").first();
            document = Jsoup.parse(rawHTML2.html().replace("<p>", "$$$"));
            maintenanceInfo = document.body().text().replace("$$$", "\n");
            maintenanceInfo = maintenanceInfo.substring(0, maintenanceInfo.indexOf("■ Tap for more info!"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if maintenance has been sent
        for (Message message : new MessageHistory(channel).retrievePast(50).complete()) {
            if (message.getContent().contains(maintenanceInfo.trim()))
                sentMaintenance = true;
        }
    }

    private static void startMaintenanceTimer() {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        Calendar maintenanceCal = Calendar.getInstance();
        maintenanceCal.setTime(maintenanceStart);
        System.out.println(maintenanceStart);

        if (maintenanceEnd.getTime() > new Date().getTime()) {
            System.out.println("Maintenance coming up!");
            System.out.println(maintenanceCal.get(Calendar.DATE) + " " + now.get(Calendar.DATE));
            if (maintenanceCal.get(Calendar.DATE) == now.get(Calendar.DATE))
                System.out.println("Maintenance today!");
            maintenanceTimer.cancel();
            maintenanceTimer = new Timer();
            maintenanceTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (maintenanceCal.get(Calendar.DATE) == now.get(Calendar.DATE)) {
                        if (Calendar.getInstance().get(Calendar.MINUTE) == 0 && !sentMaintenance) {
                            channel.sendMessage("@everyone There will be a maintenance later today! Here are the details:\n" + maintenanceInfo).queue();
                            sentMaintenance = true;
                        }
                        if (dateFormat.format(maintenanceStart).equals(dateFormat.format(new Date())))
                            channel.sendMessage("@everyone Today's maintenance has begun!").queue();
                        if (dateFormat.format(maintenanceEnd).equals(dateFormat.format(new Date())))
                            channel.sendMessage("@everyone Today's maintenance has ended!").queue();
                    }
                }
            }, 0, 1000);
        }
    }
}
