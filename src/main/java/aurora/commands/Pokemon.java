package aurora.commands;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Pokemon {
    public static void pokemon(MessageChannel channel, Message message) {
        try {
            // Resize image
            BufferedImage bufferedImmage = new BufferedImage(320, 288, BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics2D = bufferedImmage.createGraphics();
            graphics2D.drawImage(eu.rekawek.coffeegb.gui.Main.getScreen().getScaledInstance(320, 288, Image.SCALE_SMOOTH), 0, 0, null);
            graphics2D.dispose();

            // Convert to inputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImmage, "png", outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            channel.sendFile(inputStream, "pokemon.png", null).queue();

            eu.rekawek.coffeegb.gui.Main.input("start");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
