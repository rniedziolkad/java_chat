package random.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements AutoCloseable{
    private final Socket socket;
    private final PrintWriter writer;
    private final BufferedReader reader;
    private ClientReceiver receiver;
    private String user;

    private final ArrayList<UserEventListener> userEventListeners;
    private final ArrayList<MessageListener> messageListeners;

    public Client(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.userEventListeners = new ArrayList<>();
        this.messageListeners = new ArrayList<>();
        this.user = null;
    }

    public void registerMessageListener(MessageListener listener){
        messageListeners.add(listener);
    }
    public void removeMessageListener(MessageListener listener){
        messageListeners.remove(listener);
    }

    public void registerUserEvenListener(UserEventListener listener){
        userEventListeners.add(listener);
    }

    public void removeUserEvenListener(UserEventListener listener){
        userEventListeners.remove(listener);
    }

    private void notifyMessageListeners(String user, String message){
        for(MessageListener listener: messageListeners){
            listener.notifyAboutNewMessage(user, message);
        }
    }

    private void userEventListenersJoin(String user){
        for(UserEventListener listener: userEventListeners){
            listener.userJoin(user);
        }
    }

    private void userEventListenersExit(String user){
        for(UserEventListener listener: userEventListeners){
            listener.userExit(user);
        }
    }
    public void login(String username, String password) throws IOException {
        writer.println("LOGIN "+username+" "+password);
        String response = reader.readLine();
        String errMsg;
        if(response.equals("LOGIN_SUCCESS "+username)){
            this.user = username;
            System.out.println("Logged in as "+username);
            this.receiver = new ClientReceiver(this);
            this.receiver.start();
            return;
        }
        else if(response.startsWith("ERROR"))
            errMsg = response.split("\\s+", 2)[1];
        else
            errMsg = "Incorrect message received: "+response;

        System.err.println(errMsg);
    }

    public void logout() {
        writer.println("LOGOUT");
    }

    private void userExit(){
        this.user = null;
        System.out.println("Logged out of app");
    }

    public void readCommands(){
        String msg;
        try {
            while (!socket.isClosed() && (msg = reader.readLine()) != null) {
                String [] parts = msg.split("\\s+", 2);
                switch (parts[0]) {
                    case "LOGOUT_SUCCESS" -> {
                        userExit();
                        return;
                    }
                    case "MSG" -> {
                        parts = parts[1].split("\\s+", 2);
                        notifyMessageListeners(parts[0], parts[1]);
                    }
                    case "ERROR" -> System.err.println(parts[1]);
                    case "EVENT" -> {
                        parts = parts[1].split("\\s+", 2);
                        if(parts[0].equals("USER_EXIT"))
                            userEventListenersExit(parts[1]);
                        else if (parts[0].equals("USER_JOIN"))
                            userEventListenersJoin(parts[1]);
                        else
                            System.out.println("Received event: "+parts[0]);
                    }
                }
            }

            close();
        }catch (IOException e){
            System.out.println("error reading: "+e.getMessage());
        }
        user = null;
    }

    public void send(String message){
        if(message != null && !message.isEmpty() && !message.isBlank()){
            writer.println("BROADCAST "+message);
        }
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
