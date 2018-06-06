package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageHistory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static aurora.commands.BossAbstract.announcementsChannel;

public class Maintenance {
    private static Date maintenanceStart = new Date();
    private static Date maintenanceEnd = new Date();
    private static String maintenanceInfo = "";
    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static boolean sentMaintenance = false;
    private static Timer maintenanceTimer = new Timer();

    public static void maintenance() {
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
            String pattern = "MMM dd hh:mmaa zzz yyyy";
            if (month.contains("."))
                pattern = "MMM. dd hh:mmaa zzz yyyy";
            maintenanceStart = new SimpleDateFormat(pattern).parse(month + " " + day + " " + startTime + " " + timeZone + " " + year);
            maintenanceEnd = new SimpleDateFormat(pattern).parse(month + " " + day + " " + endTime + " " + timeZone + " " + year);
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
            document = Jsoup.parse(rawHTML2.html().replace("<p>", "$$$").replace("<div>", "$$$"));
            maintenanceInfo = document.body().text().replace("$$$", "\n");
            maintenanceInfo = maintenanceInfo.substring(0, maintenanceInfo.indexOf("■ Tap for more info!"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Check if maintenance has been sent
        for (Message message : new MessageHistory(announcementsChannel).retrievePast(100).complete())
            if (message.getContent().contains(maintenanceInfo.trim()))
                sentMaintenance = true;
    }

    private static void startMaintenanceTimer() {
        long differenceInMillis = maintenanceEnd.getTime() - new Date().getTime();

        System.out.println("Maintenance coming up!");
        if (maintenanceStart.getTime() - new Date().getTime() <= 86400000)
            System.out.println("Maintenance in <24 hours!");

        maintenanceTimer.cancel();
        maintenanceTimer = new Timer();
        maintenanceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (differenceInMillis <= 86400000 && differenceInMillis >= -86400000) {
                    if (!sentMaintenance) {
                        announcementsChannel.sendMessage("@everyone There will be a maintenance in <24 hours! Here are the details:\n" + maintenanceInfo).queue();
                        sentMaintenance = true;
                    }
                    if (dateFormat.format(maintenanceStart).equals(dateFormat.format(new Date())))
                        announcementsChannel.sendMessage("@everyone Today's maintenance has begun!").queue();
                    if (dateFormat.format(maintenanceEnd).equals(dateFormat.format(new Date())))
                        announcementsChannel.sendMessage("@everyone Today's maintenance has ended!").queue();
                }
            }
        }, 0, 1000);
    }
}
