package aurora;

import aurora.commands.BossAbstract;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import aurora.listeners.MessageListener;

import java.nio.file.Files;
import java.nio.file.Paths;

import static aurora.commands.GuildSiege.guildSiege;
import static aurora.commands.Maintenance.maintenance;
import static aurora.servlet.BindToPort.bindToPort;
import static aurora.servlet.BindToPort.keepAwake;
import static aurora.utils.CycleGames.cycleGames;

public class AuroraBot {
    public static JDA jda;
    public static long startTime = System.currentTimeMillis();
    public static boolean debugMode = false;
    public static void main(String[] args) {
        String token = System.getenv("token");

        try {
            token = new String(Files.readAllBytes(Paths.get("").toAbsolutePath().resolve("token"))).split("\n")[0];
            debugMode = true;
            System.out.println("Debug mode activated...");
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            eu.rekawek.coffeegb.gui.Main.main(new String[]{""});
        } catch (Exception e) {
            e.printStackTrace();
        }


        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(token);

        //.buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        try {
            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(new MessageListener());  //An instance of a class that will handle events.

            //DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ssa");
            //dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            //channel.sendMessage("Started on `" + dateFormat.format(new Date()) + "`").queue();

            BossAbstract.initialize();
            //battlefield();
            //guildSiege();
            maintenance();
            cycleGames();
            keepAwake();
            bindToPort();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
