package aurora.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static aurora.utils.SendMentionMessage.sendMentionMessage;

/**
 * Created by Tyler on 1/17/2017.
 */
public class Fact {
    public static void fact(MessageChannel channel, Message message) {
        channel.sendTyping().complete();
        Message lastMessage = null;
        try {
            lastMessage = channel.sendMessage(message.getAuthor().getAsMention() + " Loading...").complete();
            System.out.println(lastMessage.getContent());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // lastMessage.addReaction(new EmoteImpl("<:poop" + message.getGuild().getId() + ">", jda)).queue();

        int randomNumber = (int) (Math.random() * 1000.0);
        String rootString = "";
        try {
                // http://catfacts-api.appspot.com/api/facts
                String sURL = "https://catfact.ninja/fact";
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
                JsonObject rootObj = root.getAsJsonObject();
                rootString = rootObj.get("fact").getAsString();

                String sURL2 = "https://api.thecatapi.com/api/images/get?format=xml&amp;results_per_page=1";
                URL url2 = new URL(sURL2);
                HttpURLConnection request2 = (HttpURLConnection) url2.openConnection();
                request2.connect();
                try {
                    XPathFactory xpf = XPathFactory.newInstance();
                    XPath xPath = xpf.newXPath();

                    InputSource inputSource = new InputSource(new InputStreamReader((InputStream) request2.getContent()));
                    String result = (String) xPath.evaluate("//url", inputSource, XPathConstants.STRING);
                    // waa.ai imgur API is down
                    //result = shorten(result);
                    System.out.println(message.getAuthor().getAsMention() + " " + rootString);
                    sendMentionMessage(channel, message, "Here's a random cat: " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*randomNumber = (int) (Math.random() * 1000.0);
                String sURL = "http://mentalfloss.com/api/1.0/views/amazing_facts.json?id=" + randomNumber;
                URL url = new URL(sURL);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();

                JsonElement root = new JsonParser().parse(new InputStreamReader((InputStream) request.getContent()));
                JsonArray rootArr = root.getAsJsonArray();
                String r = "";
                for (int i = 0; i < rootArr.size(); i++) {
                    r += rootArr.get(i);
                }
                int index = 0;

                for (int i = 0; i < r.length() - 3; i++) {
                    if (r.substring(i, i + 4).equals("</p>")) {
                        index = i;
                    }
                }
                rootString = r.substring(11, index);*/

            rootString = rootString.replaceAll("<em>", "");
            rootString = rootString.replaceAll("</em>", "");
            rootString = rootString.replaceAll("&amp", "&");
            lastMessage.editMessage(message.getAuthor().getAsMention() + " " + rootString).queue();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
