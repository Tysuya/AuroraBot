package aurora;

import aurora.commands.Boss;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.*;
import aurora.listeners.MessageListener;

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
        String token1 = "vAqZOfj4R99bBmrd5V8";
        String token2 = "NDE4NzE0NDAxNjE3NjA4";
        String token3 = "NzA0.DX0QsQ.DQsNwtwK";

        String[] token = {token3, token2, token1};

        //token[1] + token[0] + token[2]
        JDABuilder jdaBuilder = new JDABuilder(AccountType.BOT).setToken(token[1] + token[0] + token[2]);

        //.buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        try {
            jda = jdaBuilder.buildBlocking();
            jda.addEventListener(new MessageListener());  //An instance of a class that will handle events.

            MessageChannel channel = jda.getTextChannelById("241064442429702144");
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ssa");
            dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
            //channel.sendMessage("Started on `" + dateFormat.format(new Date()) + "`").queue();
            Boss.initialize();
            cycleGames();
            keepAwake();
            bindToPort();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
