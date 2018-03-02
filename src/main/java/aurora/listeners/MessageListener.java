package aurora.listeners;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDAInfo;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.user.UserTypingEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import aurora.utils.CleverBotQuery;

import static aurora.commands.Boss.*;
import static aurora.commands.Fact.fact;
import static aurora.commands.Should.should;
import static aurora.commands.Uptime.uptime;

import static aurora.utils.SendMentionMessage.sendMentionMessage;

public class MessageListener extends ListenerAdapter {
    public MessageListener() {

    }

    @Override
    public void onUserTyping(UserTypingEvent event) {
        JDA jda = event.getJDA();
        User user = event.getUser();
        MessageChannel channel = event.getChannel();
        JDAInfo jdaInfo = new JDAInfo();
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

        String messageContent = message.getContent();              //This returns a human readable version of the Message. Similar to
        // what you would see in the client.

        boolean bot = author.isBot();                     //This boolean is useful to determine if the User that
        // sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

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

        if(!message.getAuthor().isBot()) {
            if (messageContent.equals("ab!help")) {
                channel.sendMessage("Hello, I am AuroraBot. My current commands are as follows:" +
                        "```ab!ping\nab!uptime```" +
                        "I am also a chatbot, so start a message with <@418714401617608704> and I will respond.\n" +
                        "I am in active development, so some things may break. Send any bugs to my creator, <@159201526114549760>.").queue();

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
            } else if (messageContent.contains("ab!punchin") || messageContent.contains("ab!pin")) {
                punchIn(channel, message);
            } else if (messageContent.contains("ab!punchout") || messageContent.contains("ab!pout")) {
                punchOut(channel, message);
            } else if (messageContent.contains("ab!report")) {
                report(channel, message);
            } else if (messageContent.contains("ab!check")) {
                check(channel, message);
            } else if (messageContent.startsWith("@AuroraBot")) {
                try {
                    String chat = messageContent.split("@AuroraBot ")[1];
                    System.out.println(chat);

                    CleverBotQuery cleverBotQuery = new CleverBotQuery("c4c6ef1eeefdfa203806506b4a2d63c0", chat);
                    cleverBotQuery.sendRequest();
                    chat = cleverBotQuery.getResponse();
                    sendMentionMessage(channel, message, chat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}