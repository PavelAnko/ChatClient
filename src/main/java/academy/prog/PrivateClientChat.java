package academy.prog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PrivateClientChat {
   private String login;
   private final Gson gson = new GsonBuilder().create();
   private Map<String, String> keySearch = new HashMap<>();
   private List<String> usersList;

    public PrivateClientChat(String login) {
        this.login = login;
        setAllServiceCommands();
        try {
            getUsersList();
        } catch (IOException e) {}
    }
    public void getUsersList() throws IOException {
        java.net.URL url = new URL(Utils.getURL() + "/getPrivateUserList");
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        InputStream is = http.getInputStream();
        try {
            byte[] buf = Utils.responseBodyToArray(is);
            String strBuf = new String(buf, StandardCharsets.UTF_8);
            usersList = gson.fromJson(strBuf, List.class);
            if (usersList != null) {
                System.out.print("All users are : ");
                for (String user : usersList) {
                    System.out.print(user + ",");
                }
                System.out.println("");
            }
        } finally {
            is.close();
        }
    }
    public void startRealtimeHistoryUpdate(){
        Thread realtimeHistoryUpdate = new Thread(new GetThread(login));
        realtimeHistoryUpdate.setDaemon(true);
        realtimeHistoryUpdate.start();
    }
    public void startChatting(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your massage: ");
        while (true){
            String text = scanner.nextLine();
            if (text.isEmpty()) break;
            if (isServiceCommand(text)){
                startServiceCommand(text);
            }
            else{
                try {
                    sendNewMessage(text);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        scanner.close();
    }
    private void setAllServiceCommands(){
        keySearch.put("chat -getUsersList", "getUsersList");
    }
    public boolean isServiceCommand(String command){
        return keySearch.containsKey(command);
    }
    public void startServiceCommand(String command){
        try {
            Method method = this.getClass().getMethod(keySearch.get(command));
            method.invoke(this, null);
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        } catch (IllegalAccessException e) {
        }
    }
    private Message generatrNewMessage(String text){
        String recipient = "All";
        String message = text;
        if (text.substring(0, 1).equals("@")){
            int firstSpaceIndexName = text.indexOf(" ");
            String textRecipient = text.substring(1, firstSpaceIndexName);
            if (usersList.contains(textRecipient)){
                recipient = textRecipient;
                message = text.substring(firstSpaceIndexName + 1);
            }
        }
        return new Message(login, message, recipient);
    }
    private void sendNewMessage(String text) throws IOException {
        Message message = generatrNewMessage(text);
        int res = message.send(Utils.getURL() + "/add");
        if (res != 200){
            System.out.printf("HTTP ERROR OCCURRED: "+ res);
        }
    }
}