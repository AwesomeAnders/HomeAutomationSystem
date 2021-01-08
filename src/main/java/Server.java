import com.google.gson.Gson;
import org.jspace.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Server {
    //Connection
    public final static String LOCAL_HOST = "tcp://127.0.0.1:9001/?keep";

    public static void main(String[] argv) throws InterruptedException, IllegalStateException {
        // Space exposed to external client
        SpaceRepository lobby = new SpaceRepository();
        lobby.addGate(LOCAL_HOST);

        // Rooms available in lobby space
        Space lobbySpace = new SequentialSpace();
        Space rooms = new SequentialSpace();

        // Adding a space called Room to manage all lobbySpace in spaceRepo
        lobby.add("lobbySpace", lobbySpace);

        System.out.println("Remote space created");

        Object[] request;
        Gson gson = new Gson();
        String jsonString;
        Message msg;
        //Object[] arguments;
        //String callID, f;

        while (true) {
            String roomUrl;
            jsonString = lobbySpace.get(new FormalField(Object.class))[0].toString();
            System.out.println(jsonString);
            msg = gson.fromJson(jsonString, Message.class);
            System.out.println(msg.nameID);
            System.out.println("Message was: " + msg.nameID + " " + msg.func);

            Object[] s = lobbySpace.queryp(new ActualField(msg.nameID), new FormalField(String.class));
            if (s != null) {
                System.out.println("in here");
            } else {
                roomUrl = "tcp://127.0.0.1:9001/" + msg.nameID + "?keep";
                System.out.println("Creating new room in another thread");
                lobbySpace.put(msg.nameID);
                rooms.put(msg.nameID);
                new Thread(new RoomHandler(msg.nameID, lobby)).start();

                System.out.println("Created new room with the name: " + msg.nameID + " Room size is now: " + rooms.size());
            }
        }
    }
}