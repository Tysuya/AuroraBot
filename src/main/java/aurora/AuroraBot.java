package aurora;

import aurora.commands.Boss;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import aurora.listeners.MessageListener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static aurora.servlet.BindToPort.bindToPort;
import static aurora.servlet.BindToPort.keepAwake;
import static aurora.utils.CycleGames.cycleGames;

public class AuroraBot {
    public static JDA jda;
    public static long startTime = System.currentTimeMillis();
    public static void main(String[] args) {
        String token = System.getenv("token");

        try {
            token = new String(Files.readAllBytes(Paths.get("").toAbsolutePath().resolve("token"))).split("\n")[0];
        } catch(Exception e) {
            e.printStackTrace();
        }

        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(token);

        //.buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        try {
            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(new MessageListener());  //An instance of a class that will handle events.
            String a = "\nTotal kills for GHOSTSNAKE: 207 ```" +
                    "\n1) dandera: 45" +
                    "\n2) Jenny: 42" +
                    "\n3) my name is jeeff: 40" +
                    "\n4) Alan: 23" +
                    "\n5) TripleFury: 16" +
                    "\n6) Vampy: 9" +
                    "\n7) Yon: 8" +
                    "\n8) harold: 7" +
                    "\n9) austin: 7" +
                    "\n10) Aversionist: 3" +
                    "\n11) Viskon: 3" +
                    "\n12) xlPandas: 2" +
                    "\n13) Syeira A.F (Bleu1mage/Angelkar): 1" +
                    "\n14) Nathan: 1\n```" +
                    "\nTotal kills for SPIDEY: 71 ```" +
                    "\n1) dandera: 36" +
                    "\n2) Vampy: 12" +
                    "\n3) austin: 11" +
                    "\n4) TripleFury: 3" +
                    "\n5) Jenny: 3" +
                    "\n6) Yon: 2" +
                    "\n7) harold: 2" +
                    "\n8) my name is jeeff: 1" +
                    "\n9) 2mutchKek: 1\n```" +
                    "\nTotal kills for WILDBOAR: 28 ```" +
                    "\n1) dandera: 15" +
                    "\n2) Yon: 4" +
                    "\n3) TripleFury: 2" +
                    "\n4) Alan: 2" +
                    "\n5) Jenny: 2" +
                    "\n6) Vampy: 1" +
                    "\n7) Nathan: 1" +
                    "\n8) my name is jeeff: 1\n```";
            String b =
                    "\nTotal kills for GHOSTSNAKE: 211 ```" +
                    "\n1) dandera: 45" +
                    "\n2) Jenny: 44" +
                    "\n3) my name is jeeff: 41" +
                    "\n4) Alan: 23" +
                    "\n5) TripleFury: 16" +
                    "\n6) Vampy: 9" +
                    "\n7) Yon: 9" +
                    "\n8) harold: 7" +
                    "\n9) austin: 7" +
                    "\n10) Aversionist: 3" +
                    "\n11) Viskon: 3" +
                    "\n12) xlPandas: 2" +
                    "\n13) Syeira A.F (Bleu1mage/Angelkar): 1" +
                    "\n14) Nathan: 1\n```" +
                    "\nTotal kills for SPIDEY: 72 ```" +
                    "\n1) dandera: 36" +
                    "\n2) Vampy: 12" +
                    "\n3) austin: 11" +
                    "\n4) TripleFury: 3" +
                    "\n5) Jenny: 3" +
                    "\n6) my name is jeeff: 2" +
                    "\n7) Yon: 2" +
                    "\n8) harold: 2" +
                    "\n9) 2mutchKek: 1\n```" +
                    "\nTotal kills for WILDBOAR: 30 ```" +
                    "\n1) dandera: 15" +
                    "\n2) Yon: 5" +
                    "\n3) TripleFury: 3" +
                    "\n4) Alan: 2" +
                    "\n5) Jenny: 2" +
                    "\n6) Vampy: 1" +
                    "\n7) Nathan: 1" +
                    "\n8) my name is jeeff: 1\n```" +
                    "\nTotal kills for BERSERK GOSUMI: 55 ```" +
                    "\n1) dandera: 23" +
                    "\n2) Vampy: 18" +
                    "\n3) Yon: 11" +
                    "\n4) TripleFury: 2" +
                    "\n5) 2mutchKek: 1\n```" +
                    "\nTotal kills for BLOODY GOSUMI: 18 ```" +
                    "\n1) dandera: 7" +
                    "\n2) Cammy: 4" +
                    "\n3) carneirinhoo: 3" +
                    "\n4) 2mutchKek: 3" +
                    "\n5) Vampy: 1\n```" +
                    "\nTotal kills for RAVEN: 18 ```" +
                    "\n1) carneirinhoo: 9" +
                    "\n2) my name is jeeff: 3" +
                    "\n3) dandera: 2" +
                    "\n4) Cammy: 2" +
                    "\n5) Rosegold: 1" +
                    "\n6) Serapphire: 1\n```" +
                    "\nTotal kills for BLASTER: 15 ```" +
                    "\n1) dandera: 13" +
                    "\n2) Cammy: 2```" +
                    "\nTotal kills for BSSSZSSS: 4 ```" +
                    "\n1) TripleFury: 3" +
                    "\n2) 2mutchKek: 1\n```" +
                    "\nTotal kills for DESERT ASSASAIN: 2 ```" +
                    "\n1) TripleFury: 2\n```" +
                    "\nTotal kills for BIZIZI: 6 ```" +
                    "\n1) TripleFury: 4" +
                    "\n2) dandera: 1" +
                    "\n3) harold: 1\n```";
            jda.getTextChannelById("420067387644182538").sendMessage(b).queue();

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ssa");
            dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            //channel.sendMessage("Started on `" + dateFormat.format(new Date()) + "`").queue();=
            Boss.initialize();
            cycleGames();
            keepAwake();
            bindToPort();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
