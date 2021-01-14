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

    public ComponentHandler(String name, String status, String command, Space rooms, String spaceName) {
        this.name = name;
        this.status = status;
        this.command = command;
        this.rooms = rooms;
        this.spaceName = spaceName;
    }


    @Override
    public void run() {

        try {
            Object [] theRoom = rooms.queryp(new ActualField(spaceName));
            if (theRoom != null){
                String roomURI = "tcp://127.0.0.1:9001/" + spaceName + "?keep";
                space = new RemoteSpace(roomURI);
            }else
                System.out.println("No room by that name");
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
        try {
           Object[] item = space.getp(new ActualField(name), new FormalField(Object.class));
            space.get(new ActualField("lock"),new ActualField(name));
           if (item != null){
               Tuple tuple = (Tuple) item[1];
               boolean componentStatus =  Boolean.parseBoolean(tuple.getElementAt(1).toString());
               componentStatus = !componentStatus;
               space.put(name, new Tuple(name, componentStatus));
               space.put("lock",name);
               System.out.println("Updated component to ["+name+"] "+componentStatus);
           }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addComponent(){
        Tuple component = new Tuple(name, status);
        try {
            if ( space.put(name, component)){
                space.put("lock", name);
                System.out.println("added new component: "+ component.toString());

            }else{
                System.out.println("Something went wrong...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void deleteComponent(){
        try {
            Object[] item = space.getp(new ActualField(name), new FormalField(Object.class));
            space.get(new ActualField("lock"),new ActualField(name));
            if (item != null){
                System.out.println("deleted item "+ item[0].toString());
            }else{
                System.out.println("Something went wrong...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}