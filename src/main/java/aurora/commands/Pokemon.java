package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Pokemon extends ListenerAdapter {
    public static MessageChannel channel;
    public static Message originalImage;

    public static void pokemon(MessageChannel channel, Message message) {
        Pokemon.channel = channel;
        sendImage(true);
    }

    public static void input(String input) {
        if (input != null) {
            eu.rekawek.coffeegb.gui.Main.input(input);
            sendImage(false);

            try {
                Thread.sleep(2500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            sendImage(true);
        }
    }

    public static void sendImage(boolean addReactions) {
        try {
            if (originalImage != null)
                originalImage.delete().queue();

            // Resize image
            BufferedImage bufferedImmage = new BufferedImage(320, 288, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bufferedImmage.createGraphics();
            graphics2D.drawImage(eu.rekawek.coffeegb.gui.Main.getScreen().getScaledInstance(320, 288, Image.SCALE_SMOOTH), 0, 0, null);
            graphics2D.dispose();

            // Convert to inputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImmage, "png", outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            originalImage = channel.sendFile(inputStream, "pokemon.png", null).complete();

            if (addReactions) {
                originalImage.addReaction("\u2B05").queue();
                originalImage.addReaction("\u27A1").queue();
                originalImage.addReaction("\u2B06").queue();
                originalImage.addReaction("\u2B07").queue();
                originalImage.addReaction("\uD83C\uDD70").queue();
                originalImage.addReaction("\uD83C\uDD71").queue();
                originalImage.addReaction("\uD83C\uDDFD").queue();
                originalImage.addReaction("\uD83C\uDDFE").queue();
                originalImage.addReaction("\u23ED").queue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String parseReaction(String input) {
        switch (input) {
            case "\u2B05":
                System.out.println("l");
                return "l";
            case "\u27A1":
                System.out.println("r");
                return "r";
            case "\u2B06":
                System.out.println("u");
                return "u";
            case "\u2B07":
                System.out.println("d");
                return "d";
            case "\uD83C\uDD70":
                System.out.println("a");
                return "a";
            case "\uD83C\uDD71":
                System.out.println("b");
                return "b";
            case "\uD83C\uDDFD":
                System.out.println("start");
                return "start";
            case "\uD83C\uDDFE":
                System.out.println("select");
                return "select";
            case "\u23ED":
                sendImage(true);
        }

        return null;
    }
}
