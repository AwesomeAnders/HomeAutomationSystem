import com.google.gson.Gson;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomHandler implements Runnable{
    private Space room;
    private String name;
    private SpaceRepository lobby;
    private String command;


    public RoomHandler(String name, SpaceRepository lobby, Space room, String command){
        this.name = name;
        this.lobby = lobby;
        this.room = room;
        this.command = command;
    }

    public RoomHandler(SpaceRepository lobby, Space room, String command){
        this.lobby = lobby;
        this.room = room;
        this.command = command;
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

            default:
                break;
        }

    }

    public void addRoom(){
        try {
            lobby.add(name,room);
            room.put(name);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showAllRooms(){
        Gson gson = new Gson();
        List<String> responseList = new ArrayList<>();
        List<Object[]> list = null;
        try {
            list = room.queryAll(new FormalField(String.class));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Changing the formatting of response from "["stue"] to "stue" so Json can parse it correctly
        for (int i = 0; i < list.size(); i++) {
            String str = formatStr(Arrays.toString(list.get(i)));
            responseList.add(str);
        }
        try {
            lobby.put("list", gson.toJson(responseList));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String formatStr(String str) {
        return str.substring(1,str.length()-1);
    }

}
