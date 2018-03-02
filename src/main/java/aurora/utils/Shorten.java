package aurora.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Shorten {
    public static String shorten(String rootString) {
        String returnString = "";
        try {
            String sURL = "http://api.waa.ai/imgur?key=ae3a8ffbd1e21935f2900d07f37ba201&image=" + rootString;
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.addRequestProperty("User-Agent", "Mozilla/4.76");
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonObject rootObj = root.getAsJsonObject();
            JsonElement rootObj2 = rootObj.get("data");
            JsonObject rootObj3 = rootObj2.getAsJsonObject();
            returnString = rootObj3.get("url").toString();
            returnString = returnString.substring(1, returnString.length() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(returnString);
        return returnString;
    }
}
