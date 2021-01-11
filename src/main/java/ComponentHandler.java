import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.Tuple;

import java.util.List;

public class ComponentHandler implements Runnable{

    private String name;
    private String status;
    private Space space;
    private String command;

    public ComponentHandler(Space space){
        this.space = space;
    }

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

    private void addComponent(){
        Tuple component = new Tuple(name, status);
        try {
            if ( space.put(component)){
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
            Object[] item = space.getp(new FormalField(Object.class));
            if (item != null){
                System.out.println("deleted item "+ (String) item[0].toString());
            }else{
                System.out.println("Something went wrong...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}