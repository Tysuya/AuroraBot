package aurora.listeners;

import aurora.AuroraBot;
import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import static aurora.commands.Active.active;
import static aurora.commands.Check.check;
import static aurora.commands.Delete.delete;
import static aurora.commands.Fact.fact;
import static aurora.commands.History.history;
import static aurora.commands.Kills.kills;
import static aurora.commands.Pokemon.*;
import static aurora.commands.PunchOut.punchOut;
import static aurora.commands.Remove.remove;
import static aurora.commands.Report.report;
import static aurora.commands.Reset.reset;
import static aurora.commands.Should.should;
import static aurora.commands.Uptime.uptime;
import static aurora.commands.CleverBot.cleverBot;
import static aurora.commands.PunchIn.punchIn;

public class MessageListener extends ListenerAdapter {
    String helpMessage = "Hello, I am AuroraBot. My current commands are as follows:" +
            "\n__General Commands:__" +
            "\n```ab!ping - to ping AuroraBot and get a response time" +
            "\nab!uptime - to check the uptime of AuroraBot```" +
            "\n__Boss Hunting Commands:__" +
            "\n```!pin [bossName] - to punch in to be notified when a boss spawns" +
            "\n!pout [bossName] - to punch out of notifications" +
            "\n!report [bossName] [time] [lost] - !r - to report when you killed a boss - add \"lost\" if you didn't kill the boss" +
            "\n!reset [bossName] - to reset a boss time to \"Unknown\"" +
            "\n!check [bossName] - to check the next time a boss is going to spawn" +
            "\n!history [bossName] [amount] - to check the history of a boss" +
            "\n!kills [bossName] - to check the number of kills on a boss" +
            "\n!remove [bossName] [kills] - to remove a number of kills of a boss from a hunter" +
            "\n!active - to check active bosses```" +
            "\n__Boss names and abbreviations recognized by AuroraBot:__" +
            "\n```GHOSTSNAKE - GS" +
            "\nSPIDEY" +
            "\nWILDBOAR - WB" +
            "\nBERSERK GOSUMI - BERSERK" +
            "\nWHITE CROW - WHITE - WC" +
            "\nBLOODY GOSUMI - BLOODY" +
            "\nRAVEN" +
            "\nBLASTER" +
            "\nBSSSZSSS - RED BEE - RB" +
            "\nDESERT ASSASAIN - DESERT - ASSASAIN - ASSASSIN - ASS - DA" +
            "\nSTEALTH" +
            "\nBUZSS" +
            "\nBIZIZI" +
            "\nBIGMOUSE - BM" +
            "\nLESSER MADMAN - LESSER - MADMAN - LM" +
            "\nSHAAACK" +
            "\nSUUUK" +
            "\nSUSUSUK" +
            "\nELDER BEHOLDER - ELDER - EM" +
            "\nSANDGRAVE - SG" +
            "\nCHIEF MAGIEF - CHIEF - CM" +
            "\nMAGMA SENIOR THIEF - MAGMA - MST" +
            "\nBBINIKJOE - BB - JOE" +
            "\nBURNING STONE - BURNING" +
            "\nELEMENTAL QUEEN - ELEMENTAL - QUEEN - EQ" +
            "\nTWISTER" +
            "\nMAELSTROM" +
            "\nSWIRL FLAME - SWIRL - FLAME - SF" +
            "\nTANK" +
            "\nSTEAMPUNK - STEAM - PUNK - SP" +
            "\nLANDMINE - LAND - MINE" +
            "\nTITANIUM GOLEM - TITANIUM - TIT - GOLEM - TG" +
            "\nLACOSTEZA - LACOS" +
            "\nBLACKSKULL - BS" +
            "\nTURTLE Z - TURTLE - TZ```";
    String helpMessage2 = "__Notes:__" +
            "\n```Boss names are not case-sensitive" +
            "\nUsing colons in times is optional" +
            "\nMost of the commands can take multiple boss names" +
            "\nYou can use @mention to perform commands for others" +
            "\nYou can use \"!pin all\" and \"pout all\" to punch in/out for all bosses```" +
            "I am also a chatbot, so start a message with <@418714401617608704> and I will respond." +
            "\nI am still in development, so some things may break. Send any bugs to my creator, <@159201526114549760>.";

    public MessageListener() {
        if (!AuroraBot.debugMode)
            try {
                AuroraBot.jda.getTextChannelById("418818283102404611").editMessageById("419724800471203854", helpMessage).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onUserTyping(UserTypingEvent event) {
        JDA jda = event.getJDA();
        User user = event.getUser();
        MessageChannel channel = event.getChannel();
        JDAInfo jdaInfo = new JDAInfo();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!event.getUser().isBot())
            input(parseReaction(event.getReactionEmote().getName()));
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        System.out.println(event.getShutdownTime());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.

        //Event specific information
        User author = event.getAuthor();                  //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
        //  This could be a TextChannel, PrivateChannel, or Group!

        String messageContent = message.getContent(); //This returns a human readable version of the Message. Similar to
        // what you would see in the client.

        boolean bot = author.isBot();                     //This boolean is useful to determine if the User that
        // sent the Message is a BOT or not!

        Member member = null;

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name = member.getEffectiveName();    //This will either use the Member's nickname if they have one,
            // otherwise it will default to their username. (User#getName())

            System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, messageContent);
        } else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), messageContent);
        } else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!
        {
            //The message was sent in a Group. It should be noted that Groups are CLIENT only.
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), messageContent);
        }

        if (!author.isBot()) {
            messageContent = message.getContent().toLowerCase();
            if (messageContent.equals("ab!help")) {
                channel.sendTyping().complete();
                channel.sendMessage(helpMessage).queue();
                channel.sendMessage(helpMessage2).queue();
            } else if (messageContent.equals("ab!ping")) {
                long start = System.currentTimeMillis();
                try {
                    Message ping = channel.sendMessage(":ping_pong:`...`").complete();
                    ping.editMessage(":ping_pong: `" + (System.currentTimeMillis() - start) + "ms`").queue();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (messageContent.equals("ab!uptime")) {
                uptime(channel);
            } else if (messageContent.contains("fact") && !message.getAuthor().isBot()) {
                fact(channel, message);
            } else if (messageContent.contains("ab!should")) {
                should(channel, message);
            } else if (messageContent.contains("!delete")) {
                if (member != null & member.hasPermission(Permission.ADMINISTRATOR))
                    delete(channel, message);
            } else if (messageContent.contains("!pin")) {
                punchIn(channel, message);
            } else if (messageContent.contains("!pout")) {
                punchOut(channel, message);
            } else if (messageContent.contains("!report") || messageContent.contains("!r ")) {
                report(channel, message);
            } else if (messageContent.contains("!reset")) {
                reset(channel, message);
            } else if (messageContent.contains("!check")) {
                check(channel, message);
            } else if (messageContent.contains("!history")) {
                history(channel, message);
            } else if (messageContent.contains("!kills")) {
                kills(channel, message);
            } else if (messageContent.contains("!remove")) {
                remove(channel, message);
            } else if (messageContent.contains("!active")) {
                active(channel, message);
            } else if (messageContent.contains("!poke")) {
                pokemon(channel, message);
            } else if (message.isMentioned(jda.getSelfUser())) {
                if (messageContent.contains("aurorabot"))
                    cleverBot(channel, message);
            }

            String[] emoteNames = {"omg", "dizzy", "mad", "greedy", "surprise", "cry", "happy", "love", "heh"};
            for (String emoteName : emoteNames)
                if (messageContent.equals(emoteName) || messageContent.equals(":" + emoteName + ":"))
                    message.addReaction(message.getGuild().getEmotesByName(emoteName, true).get(0)).queue();
        }
    }
}