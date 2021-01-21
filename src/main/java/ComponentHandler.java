import org.jspace.*;

import java.io.IOException;
import java.util.List;

public class ComponentHandler implements Runnable{
    private String name;
    private String status;
    private Space space;
    private String command;
    private Space rooms;
    private String spaceName;
    private Space lobbySpace;
    private String userName;
    private String pwd;
    private Space clientSpace;

    public ComponentHandler(String name, String status, String command, Space rooms, String spaceName, Space lobbySpace, String userName, String pwd, Space clientSpace) {
        this.name = name;
        this.status = status;
        this.command = command;
        this.rooms = rooms;
        this.spaceName = spaceName;
        this.lobbySpace = lobbySpace;
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

        try {
            Object [] theRoom = rooms.queryp(new ActualField(spaceName));
            if (theRoom != null){
                String roomURI = "tcp://127.0.0.1:9001/" + spaceName + "?keep";
                space = new RemoteSpace(roomURI);
            }else {
                System.out.println("No room by that name");
                lobbySpace.put("Error", "No room by that name");
                return;
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        switch (command){
            case "addComp":
                addComponent();
                break;

            case "deleteComp":
                deleteComponent();
                break;

            case "showAll":
                showAll();
                break;
            case "updateComp":
                updateComponent();
                break;

            case "default":
                break;
        }
    }

    public void showAll(){
        try {
            List<Object[]> list = space.queryAll(new FormalField(String.class), new FormalField(Object.class));
            for (Object[] object : list ) {
                System.out.println(object[0].toString());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateComponent(){
        Object[] item;
        try {
            space.get(new ActualField("lock"),new ActualField(name));

            item = space.get(new ActualField(name), new FormalField(Object.class));
            if (item != null){
               Tuple tuple = (Tuple) item[1];
               boolean componentStatus =  Boolean.parseBoolean(tuple.getElementAt(1).toString());
               componentStatus = !componentStatus;
               space.put(name, new Tuple(name, componentStatus));
               space.put("lock",name);
               System.out.println("Updated component to "+name+" "+componentStatus);
               lobbySpace.put(command,"Updated component to "+name+" "+componentStatus);
            } else{
               lobbySpace.put(command,"No component by that name");
               System.out.println("No component by that name");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addComponent() {
        Tuple component = new Tuple(name, status);
        try {
            Object[] item = space.getp(new ActualField(name), new FormalField(Object.class));
            if (item != null){
                System.out.println("Component with that name already exists");
                lobbySpace.put(command, "Component with that name already exists");
                return;
            }
            if ( space.put(name, component)){
                space.put("lock", name);
                System.out.println("added new component: "+ component.toString());
                lobbySpace.put(command, "added new component: "+ name);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteComponent(){
        try {
            Object[] item = space.getp(new ActualField(name), new FormalField(Object.class));
            if (item != null){
                space.get(new ActualField("lock"),new ActualField(name));
                System.out.println("deleted item "+ item[0].toString());
                lobbySpace.put(command, "deleted component: "+ item[0].toString());
            }else{
                System.out.println("No component with that name");
                lobbySpace.put(command, "No component with that name");
            }
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
}