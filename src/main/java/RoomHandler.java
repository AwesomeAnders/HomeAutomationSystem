import com.google.gson.Gson;
import org.jspace.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomHandler implements Runnable{
    private Space room;
    private String name;
    private SpaceRepository lobby;
    private String command;
    private Space rooms;
    private Space lobbySpace;

    public RoomHandler(String name, SpaceRepository lobby, Space rooms, String command) throws InterruptedException {
        this.name = name;
        this.lobby = lobby;
        this.room = new SequentialSpace();
        this.command = command;
        this.rooms = rooms;

    }

    public RoomHandler(Space lobbySpace, Space rooms, String command){
        this.lobbySpace = lobbySpace;
        this.command = command;
        this.rooms = rooms;
    }



    @Override
    public void run() {

        switch (command){

            case "add":
                addRoom();
                break;

            case "list":
                showAllRooms();
                break;

        }
    }

    public void addRoom(){
        Object[] s;
        try {
            s = rooms.queryp(new ActualField(name));
            if (s != null) {
                System.out.println("Room with that name already exist");
                lobbySpace.put("add", "Room with that name already exist");
            }else {
                System.out.println("Creating new room in another thread");
                rooms.put(name);
                lobby.add(name,room);
                System.out.println("Created new room with the name: " + name + " Room size is now: " + rooms.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showAllRooms(){
        Gson gson = new Gson();
        List<String> responseList = new ArrayList<>();
        List<Object[]> list = null;
        try {
            list = rooms.queryAll(new FormalField(String.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Changing the formatting of response from "["stue"] to "stue" so Json can parse it correctly
        for (int i = 0; i < list.size(); i++) {
            String str = formatStr(Arrays.toString(list.get(i)));
            responseList.add(str);
        }

        try {
            lobbySpace.put("list", gson.toJson(responseList));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String formatStr(String str) {
        return str.substring(1,str.length()-1);
    }

}
