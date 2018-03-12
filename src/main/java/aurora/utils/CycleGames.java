package aurora.utils;

import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.managers.Presence;
import aurora.AuroraBot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Tyler on 1/17/2017.
 */
public class CycleGames {
    public static void cycleGames() {
        try {
            Presence presence = AuroraBot.jda.getPresence();
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    String[] games = {"League of Legends", "Starcraft II", "Overwatch", "Twitch.tv", "Pokémon", "Stardew Valley", "LoZ:BOTW", "CS:GO", "Halo 3", "Heroku", "Animal Crossing", "Mega Man", "Pac-Man", "Ping-Pong", "Magic", "Hearthstone", "CodeFights", "CodeWars", "The Land of Chai 2", "Chainary", "RetroCube", "Temple Run", "Flappy Bird", "Reddit", "Music", "Piano", "Guitar", "Ukulele", "Bass", "Drums", "Triangle", "Xylophone", "Kazoo", "Nothing"};
                    int random = new Random().nextInt(games.length);
                    presence.setGame(Game.of(games[random] + " | Use ab!help"));
                    System.out.println(games[random] + " | Use ab!help");
                }
            }, 0, 300000);

            /*timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MessageChannel channel = AuroraBot.jda.getTextChannelById("241064442429702144");
                    Uptime.uptime(channel);
                }
            }, 0, 900000);*/

            // Birthdays; general channel
            /*timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    MessageChannel channel = AuroraBot.jda.getTextChannelById("232011880485486592");

                    DateFormat dateFormat = new SimpleDateFormat("MM/dd hh:mma");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
                    String dateString = dateFormat.format(new Date());

                    String[] emojis = {":birthday:", ":fireworks:", ":sparkler:", ":tada:", ":confetti_ball:"};
                    String emojiString = "";
                    for(int i = 1; i < 101; i++) {
                        emojiString += emojis[new Random().nextInt(5)];
                        if(i % 20 == 0)
                            emojiString += "\n";
                        else
                            emojiString += " ";
                    }

                    if(dateString.contains("09/20")) {
                        presence.setGame(Game.of("It's Chaikitty's Birthday!"));
                    }
                    if(dateString.equals("09/20 12:00AM")) {
                        channel.sendMessage("@everyone It is now 12:00AM on 09/20! <@232011523256483840> I'd like to wish a happy birthday to the one and only `Chaikitty`! Ahem, please join me in song :notes::" + "```Happy birthday to you,\n" + "Happy birthday to you,\n" + "Happy birthday dear Chaikitty,\n" + "Happy birthday to you.\n" + "\n" + "From good friends and true,\n" + "From old friends and new,\n" + "May good luck go with you,\n" + "And happiness too.\n```" + emojiString).queue();
                    }
                    if(dateString.equals("09/20 11:59PM")) {
                        channel.sendMessage("@everyone It is now 11:59PM on 09/20! <@232011523256483840> I hope your birthday was everything you wished for and more! I'll be sure to message you next year too (if I'm still awake :zzz:).").queue();
                    }

                    if(dateString.contains("11/06")) {
                        presence.setGame(Game.of("It's aurora's Birthday!"));
                    }
                    if(dateString.equals("11/06 12:00AM")) {
                        channel.sendMessage("@everyone It is now 12:00AM on 11/06! <@159201526114549760> I'd like to wish a happy birthday to the one and only `aurora`! Ahem, please join me in song :notes::" + "```Happy birthday to you,\n" + "Happy birthday to you,\n" + "Happy birthday dear aurora,\n" + "Happy birthday to you.\n" + "\n" + "From good friends and true,\n" + "From old friends and new,\n" + "May good luck go with you,\n" + "And happiness too.\n```" + emojiString).queue();
                    }
                    if(dateString.equals("11/06 11:59PM")) {
                        channel.sendMessage("@everyone It is now 11:59PM on 11/06! <@159201526114549760> I hope your birthday was everything you wished for and more! I'll be sure to message you next year too (if I'm still awake :zzz:).").queue();
                    }
                }
            }, 0, 59000);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
