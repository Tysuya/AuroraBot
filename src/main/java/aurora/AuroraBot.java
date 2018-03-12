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
            String[] a = {
                    "\nTotal overall kills for **AURORA**: `479` ```" +
                            "1) dandera: 148\n" +
                            "2) Jenny: 57\n" +
                            "3) my name is jeeff: 50\n" +
                            "4) Vampy: 46\n" +
                            "5) TripleFury: 38\n" +
                            "6) Yon: 29\n" +
                            "7) Alan: 25\n" +
                            "8) austin: 20\n" +
                            "9) carneirinhoo: 12\n" +
                            "10) harold: 11\n" +
                            "11) Kaitie \uD83D\uDC95: 9\n" +
                            "12) 2mutchKek: 7\n" +
                            "13) Cammy: 6\n" +
                            "14) Serapphire: 6\n" +
                            "15) Aversionist: 3\n" +
                            "16) Viskon: 3\n" +
                            "17) Syeira A.F (Bleu1mage/Angelkar): 2\n" +
                            "18) xlPandas: 2\n" +
                            "19) Nathan: 2\n" +
                            "20) imjayski: 1\n" +
                            "21) Domm1e/Henry: 1\n" +
                            "22) Rosegold: 1\n```",
                    "\nTotal kills for **GHOSTSNAKE**: `239` ```" +
                            "\n1) dandera: 52" +
                            "\n2) Jenny: 52" +
                            "\n3) my name is jeeff: 45" +
                            "\n4) Alan: 23" +
                            "\n5) TripleFury: 19" +
                            "\n6) Vampy: 9" +
                            "\n7) Yon: 9" +
                            "\n8) austin: 8" +
                            "\n9) harold: 7" +
                            "\n10) Domm1e/Henry: 3" +
                            "\n11) Aversionist: 3" +
                            "\n12) Viskon: 3" +
                            "\n13) xlPandas: 2" +
                            "\n14) imjayski: 1" +
                            "\n15) Syeira A.F (Bleu1mage/Angelkar): 1" +
                            "\n16) Nathan: 1" +
                            "\n17) Serapphire: 1\n```",
                    "\nTotal kills for **WILDBOAR**: `37` ```" +
                            "\n1) dandera: 19" +
                            "\n2) Yon: 7" +
                            "\n3) TripleFury: 3" +
                            "\n4) Alan: 2" +
                            "\n5) Jenny: 2" +
                            "\n6) Vampy: 1" +
                            "\n7) Nathan: 1" +
                            "\n8) my name is jeeff: 1\n```",
                    "\nTotal kills for **SPIDEY**: `84` ```" +
                            "\n1) dandera: 45" +
                            "\n2) Vampy: 13" +
                            "\n3) austin: 11" +
                            "\n4) TripleFury: 3" +
                            "\n5) Jenny: 3" +
                            "\n6) my name is jeeff: 2" +
                            "\n7) Nathan: 2" +
                            "\n8) Yon: 2" +
                            "\n9) harold: 2" +
                            "\n10) 2mutchKek: 1\n```",
                    "\nTotal kills for **BERSERK GOSUMI**: `72` ```" +
                            "\n1) dandera: 32" +
                            "\n2) Vampy: 20" +
                            "\n3) Yon: 12" +
                            "\n4) TripleFury: 3" +
                            "\n5) 2mutchKek: 2" +
                            "\n6) Nathan: 1" +
                            "\n7) my name is jeeff: 1" +
                            "\n8) Jenny: 1\n```",
                    "\nTotal kills for **WHITE CROW**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **BLOODY GOSUMI**: `19` ```" +
                            "\n1) dandera: 8" +
                            "\n2) Cammy: 4" +
                            "\n3) carneirinhoo: 3" +
                            "\n4) 2mutchKek: 3" +
                            "\n5) Vampy: 1\n```",
                    "\nTotal kills for **RAVEN**: `42` ```" +
                            "\n1) carneirinhoo: 9" +
                            "\n2) austin: 8" +
                            "\n3) Domm1e/Henry: 7" +
                            "\n4) Serapphire: 7" +
                            "\n5) my name is jeeff: 3" +
                            "\n6) 2mutchKek: 3" +
                            "\n7) dandera: 2" +
                            "\n8) Cammy: 2" +
                            "\n9) Rosegold: 1\n```",
                    "\nTotal kills for **BLASTER**: `17` ```" +
                            "\n1) dandera: 13" +
                            "\n2) Cammy: 2" +
                            "\n3) Syeira A.F (Bleu1mage/Angelkar): 1" +
                            "\n4) 2mutchKek: 1\n```",
                    "\nTotal kills for **BSSSZSSS**: `17` ```" +
                            "\n1) Jenny: 5" +
                            "\n2) TripleFury: 3" +
                            "\n3) my name is jeeff: 3" +
                            "\n4) Kaitie \uD83D\uDC95: 3" +
                            "\n5) dandera: 2" +
                            "\n6) 2mutchKek: 1\n```",
                    "\nTotal kills for **DESERT ASSASAIN**: `16` ```" +
                            "\n1) Jenny: 7" +
                            "\n2) TripleFury: 2" +
                            "\n3) my name is jeeff: 2" +
                            "\n4) Kaitie \uD83D\uDC95: 2" +
                            "\n5) Domm1e/Henry: 1" +
                            "\n6) dandera: 1" +
                            "\n7) 2mutchKek: 1\n```",
                    "\nTotal kills for **STEALTH**: `13` ```" +
                            "\n1) Kaitie \uD83D\uDC95: 6" +
                            "\n2) dandera: 3" +
                            "\n3) TripleFury: 2" +
                            "\n4) Jenny: 2\n```",
                    "\nTotal kills for **BUZSS**: `8` ```" +
                            "\n1) 2mutchKek: 3" +
                            "\n2) dandera: 2" +
                            "\n3) my name is jeeff: 2" +
                            "\n4) TripleFury: 1\n```",
                    "\nTotal kills for **BIZIZI**: `10` ```" +
                            "\n1) TripleFury: 4" +
                            "\n2) my name is jeeff: 2" +
                            "\n3) harold: 2" +
                            "\n4) dandera: 1" +
                            "\n5) 2mutchKek: 1\n```",
                    "\nTotal kills for **BIGMOUSE**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **LESSER MADMAN**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **SHAAACK**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **SUUUK**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **SUSUSUK**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **ELDER BEHOLDER**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **SANDGRAVE**: `0` ```" +
                            " \n```",
                    "\nTotal kills for **LACOSTEZA**: `1` ```" +
                            "\n1) austin: 1\n```"};
            for (String b : a) {
                jda.getTextChannelById("420067387644182538").sendMessage(b).queue();
            }

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
