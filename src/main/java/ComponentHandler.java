import org.jspace.*;

import java.util.List;

public class ComponentHandler implements Runnable{
    private final String name;
    private final String status;
    private final Space space;
    private final String command;

    public ComponentHandler(String name, String status, Space space, String command) {
        this.name = name;
        this.status = status;
        this.space = space;
        this.command = command;
    }


    @Override
    public void run() {
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
            List<Object[]> list = space.queryAll(new FormalField(Object.class));
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