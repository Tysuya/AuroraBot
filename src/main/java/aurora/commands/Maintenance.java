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
    private static Date maintenanceStart;
    private static Date maintenanceEnd;
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

                long differenceInMillis = maintenanceStart.getTime() - new Date().getTime();
                if (differenceInMillis <= 86400000 && differenceInMillis >= -43200000) {
                    System.out.println("Maintenance in <24 hours!");
                    startMaintenanceTimer();
                }
                else {
                    maintenanceTimer.cancel();
                }
            }
        }, 0, 3600000);
    }

    private static void checkForMaintenance() {
        try {
            // Parse start and end times
            Document document = Jsoup.connect("https://www.withhive.com/game/desc/32/22").get();
            if (!document.toString().contains("[Maintenance]"))
                return;
            Element rawHTML = document.select("ul.list_notice > li > a > span").first();
            String[] info = rawHTML.text().substring(26).replace("st", "").replace("nd", "").replace("rd", "").replace("th", "").split(" ");
            System.out.println(Arrays.toString(info));
            String month = info[0];
            String day = info[1];
            String startTime = info[2];
            String endTime = info[4];
            String timeZone = info[5].substring(0, info[5].length() - 1);
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

            if (maintenanceInfo.length() + 74 > 2000)
                maintenanceInfo = maintenanceInfo.substring(0, 2000 - 74);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Check if maintenance has been sent
        for (Message message : new MessageHistory(announcementsChannel).retrievePast(100).complete())
            if (message.getContent().contains(maintenanceInfo.trim()) && !maintenanceInfo.isEmpty())
                sentMaintenance = true;
    }

    private static void startMaintenanceTimer() {
        maintenanceTimer.cancel();
        maintenanceTimer = new Timer();
        maintenanceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                    if (!sentMaintenance) {
                        announcementsChannel.sendMessage("@everyone There will be a maintenance in <24 hours! Here are the details:\n" + maintenanceInfo).queue();
                        sentMaintenance = true;
                    }
                    if (dateFormat.format(maintenanceStart).equals(dateFormat.format(new Date())))
                        announcementsChannel.sendMessage("@everyone Today's maintenance has begun!").queue();
                    if (dateFormat.format(maintenanceEnd).equals(dateFormat.format(new Date()))) {
                        announcementsChannel.sendMessage("@everyone Today's maintenance has ended!").queue();
                        maintenanceTimer.cancel();
                    }
            }
        }, 0, 1000);
    }
}
