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
            String roomUrl;
            jsonString = lobbySpace.get(new FormalField(Object.class))[0].toString();
            msg = gson.fromJson(jsonString, Message.class);

            switch (msg.func) {
                case "list":
                    System.out.println("Message was: " + msg.func);
                    retrieveListAndResponse();
                    break;
                case "add":
                    System.out.println("Message was: " + msg.spaceName + " " + msg.func);
                    Object[] s = lobbySpace.queryp(new ActualField(msg.spaceName), new FormalField(String.class));
                    if (s != null) {
                        lobbySpace.put("add", "Room with that name already exist");
                    } else {
                        roomUrl = "tcp://127.0.0.1:9001/" + msg.spaceName + "?keep";
                        System.out.println("Creating new room in another thread");
                        rooms.put(msg.spaceName);
                        new Thread(new RoomHandler(msg.spaceName, lobby)).start();

                        System.out.println("Created new room with the name: " + msg.spaceName + " Room size is now: " + rooms.size());
                    }
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

            }
        }
    }

    private static void retrieveListAndResponse() throws InterruptedException {
        Gson gson = new Gson();
        List<String> responseList = new ArrayList<>();
        List<Object[]> list = rooms.queryAll(new FormalField(String.class));

        //Changing the formatting of response from "["stue"] to "stue" so Json can parse it correctly
        for (int i = 0; i < list.size(); i++) {
            String str = formatStr(Arrays.toString(list.get(i)));
            responseList.add(str);
        }

        //Testing of query
        /*System.out.println("Result length was: " + list.size());
        String json = gson.toJson(list);
        System.out.println(json);*/

        lobbySpace.put("list", gson.toJson(responseList));
    }

    private static String formatStr(String str) {
        return str.substring(1,str.length()-1);
    }
}