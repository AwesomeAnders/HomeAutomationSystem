import com.google.gson.Gson;
import org.jspace.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    //Connection
    public final static String LOCAL_HOST = "tcp://127.0.0.1:9001/?keep";
    private static Space lobbySpace;
    private static Space rooms;

    public static void main(String[] argv) throws InterruptedException, IllegalStateException {
        // Space exposed to external client
        SpaceRepository lobby = new SpaceRepository();
        lobby.addGate(LOCAL_HOST);

        // Rooms available in lobby space
        lobbySpace = new SequentialSpace();
        rooms = new SequentialSpace();

        // Adding a space called Room to manage all lobbySpace in spaceRepo
        lobby.add("lobbySpace", lobbySpace);

        System.out.println("Remote space created");

        Gson gson = new Gson();
        String jsonString;
        Message msg;
        Object[] theRoom;

        while (true) {

            jsonString = lobbySpace.get(new FormalField(Object.class))[0].toString();
            msg = gson.fromJson(jsonString, Message.class);

            switch (msg.func) {
                case "list":
                    new Thread(new RoomHandler(lobby,rooms, msg.func)).start();
                    break;

                case "add":
                    Object[] s = lobbySpace.queryp(new ActualField(msg.spaceName), new FormalField(String.class));
                    if (s == null) {
                        new Thread(new RoomHandler(msg.spaceName, lobby, rooms, msg.func)).start();
                        System.out.println("Created new room with the name: " + msg.spaceName + " Room size is now: " + rooms.size());
                    } else
                        System.out.println("Room by that name already exists");
                    break;

                case "addComp":
                case "deleteComp":
                case "updateComp":
                case "showAll":
                    theRoom = rooms.queryp(new ActualField(msg.spaceName));
                    if (theRoom != null){
                        String roomURI = "tcp://127.0.0.1:9001/" + msg.spaceName + "?keep";
                        try {
                            RemoteSpace space = new RemoteSpace(roomURI);
                            new Thread(new ComponentHandler(msg.componentName,"off",space, msg.func)).start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        System.out.println("No room by that name");
                    }
                    break;

                case "default":
                    break;
            }
        }
    }
}