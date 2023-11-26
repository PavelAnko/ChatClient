package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private final Gson gson;
    private int allNumber; // /get?from=n
    private String login;

    public GetThread(String login) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.login=login;
    }

    public void run() { // WebSockets
        try {
            while (!Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + allNumber + "&sender=" + login);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = Utils.responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);
                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) System.out.println(m);
                    }
                } finally {
                    is.close();
                }
                allNumber = getAllNumberOfRecords();
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private int getAllNumberOfRecords() {
        Integer numberOfRecords = 0;
        try {
            URL url = new URL(Utils.getURL() + "/getNumberOfRecordsList");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream is = http.getInputStream();
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            numberOfRecords = gson.fromJson(strBuf, Integer.class);

        } catch (MalformedURLException e) {

        } catch (IOException e) {

        }
        return numberOfRecords;
    }
}
