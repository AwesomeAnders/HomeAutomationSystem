import com.google.gson.Gson;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.jspace.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RoomHandler implements Runnable{
    private Space room;
    private String name;
    private SpaceRepository spaceRepository;
    private String command;
    private Space rooms;
    private Space lobbySpace;
    private String userName;
    private String pwd;
    private Space clientSpace;

    public RoomHandler(String name, SpaceRepository lobby, Space rooms, String command, Space lobbySpace, String userName, String pwd, Space clientSpace) throws InterruptedException {
        this.name = name;
        this.spaceRepository = lobby;
        this.room = new SequentialSpace();
        this.command = command;
        this.rooms = rooms;
        this.lobbySpace = lobbySpace;
        this.userName = userName;
        this.pwd = pwd;
        this.clientSpace = clientSpace;

    }

    public RoomHandler(Space lobbySpace, Space rooms, String command, String userName, String pwd, Space clientSpace) {
        this.lobbySpace = lobbySpace;
        this.rooms = rooms;
        this.command = command;
        this.userName = userName;
        this.pwd = pwd;
        this.clientSpace = clientSpace;
    }


    @Override
    public void run() {

        if (!validateUser()){
            try {
                lobbySpace.put("Error", "You dont have permission");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }



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
                spaceRepository.add(name,room);
                lobbySpace.put("add", "Added new room with name: "+name);
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

    public boolean validateUser() {
        try {
            Object[] exist = clientSpace.queryp(new ActualField(userName), new FormalField(Integer.class), new FormalField(Enum.class), new ActualField(pwd));
            if (exist != null ) {
                if (exist[2].equals(User.Role.admin) || exist[2].equals(User.Role.user))
                    return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String formatStr(String str) {
        return str.substring(1,str.length()-1);
    }
}
