package aurora.commands;

import aurora.AuroraBot;
import com.michaelwflaherty.cleverbotapi.CleverBotQuery;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.HashMap;

import static aurora.utils.SendMentionMessage.sendMentionMessage;

public class CleverBot {
    static HashMap<String, String> conversationID = new HashMap<>();

    public static void cleverBot(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        try {
            String chat = message.getContent().split("@AuroraBot ")[1];
            if (AuroraBot.debugMode)
                chat = message.getContent().split("@AuroraBot ")[1];
            String author = message.getAuthor().getName();
            System.out.println(chat);

            CleverBotQuery cleverBotQuery = new CleverBotQuery("c4c6ef1eeefdfa203806506b4a2d63c0", chat);

            if (conversationID.get(author) != null)
                cleverBotQuery.setConversationID(conversationID.get(author));

            cleverBotQuery.sendRequest();
            conversationID.put(author, cleverBotQuery.getConversationID());

            chat = cleverBotQuery.getResponse();

            sendMentionMessage(channel, message, chat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
