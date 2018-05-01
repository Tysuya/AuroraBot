package aurora.commands;

import aurora.AuroraBot;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Maintenance {
    //static String channelID = "418683981291192331";
    static String channelID = "418818283102404611";
    static Date maintenanceStart = new Date();
    static Date maintenanceEnd = new Date();
    static String maintenanceInfo = "";
    static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public static void maintenance() {
        try {
            // Parse start and end times
            Document document = Jsoup.connect("https://www.withhive.com/game/desc/32/22").get();
            Element rawHTML = document.select("ul.list_notice > li > a > span").first();
            String[] info = rawHTML.text().substring(13).split(" ");
            String month = info[0];
            String day = "30".replace("th", "");
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

        if (maintenanceStart.getDay() == new Date().getDay()) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == 0 && Calendar.getInstance().get(Calendar.MINUTE) == 0 && Calendar.getInstance().get(Calendar.SECOND) == 0)
                        AuroraBot.jda.getTextChannelById(channelID).sendMessage("@everyone There will be a maintenance later today! Here are the details:\n" + maintenanceInfo).queue();
                    if (dateFormat.format(maintenanceStart).equals(dateFormat.format(new Date())))
                        AuroraBot.jda.getTextChannelById(channelID).sendMessage("@everyone Today's maintenance has begun!").queue();
                    if (dateFormat.format(maintenanceEnd).equals(dateFormat.format(new Date())))
                        AuroraBot.jda.getTextChannelById(channelID).sendMessage("@everyone Today's maintenance has ended!").queue();
                }
            }, 0, 1000);
        }
    }
}
