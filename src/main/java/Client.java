import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.*;

public class Client {
    public final static String REMOTE_URI = "tcp://127.0.0.1:9001/lobbySpace?keep";

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("--- Welcome ---");
        mainPrompt();
    }

    private static void mainPrompt() throws IOException, InterruptedException {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        Gson gson = new Gson();
        String spaceName;
        String componentName;
        String jsonMsg;

        while (running) {
            System.out.println("Press 1 to see all rooms");
            System.out.println("Press 2 to add a new room");
            System.out.println("Press 3 to add a new component");
            System.out.println("Press 4 to delete a component");
            System.out.println("Press 5 to update a component");
            System.out.println("Press 6 to quit");

            String scanned = scan.next();
            Message msg;
            RemoteSpace remoteSpace = new RemoteSpace(REMOTE_URI);
            int choice = Integer.parseInt(scanned);
            switch (choice) {
                case 1:
                    //Sends request
                    msg = new Message("list");
                    remoteSpace.put(gson.toJson(msg));

                    //Retrieve response
                    List response = formatResponse(remoteSpace);

                    if(response.isEmpty()) {
                        System.out.println("-- No rooms is available -- ");
                    } else {
                        System.out.println("-- There is " + response.size() + " rooms available");
                        for (int i = 0; i < response.size(); i++) {
                            System.out.println("-- room " + (i+1) + " named: \"" + response.get(i) + "\" --");
                        }
                        System.out.println("\n");
                    }
                    break;
                case 2:
                    System.out.println("Please enter new room name");
                    spaceName = scan.next();
                    msg = new Message(spaceName, "add");
                    remoteSpace.put(gson.toJson(msg));
                    break;

                case 3:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    Message addCompMsg = new Message(componentName, "addComp", spaceName);
                    jsonMsg = gson.toJson(addCompMsg);
                    remoteSpace.put(jsonMsg);
                    break;

                case 4:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    Message deleteCompMsg = new Message(componentName, "deleteComp", spaceName);
                    jsonMsg = gson.toJson(deleteCompMsg);
                    remoteSpace.put(jsonMsg);
                    break;

                case 5:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    Message updateCompMsg = new Message(componentName, "updateComp", spaceName);
                    jsonMsg = gson.toJson(updateCompMsg);
                    remoteSpace.put(jsonMsg);
                    break;
            }
        }
    }

    private static List formatResponse(RemoteSpace remoteSpace) throws InterruptedException {
        Gson gson = new Gson();
        Object[] r = remoteSpace.get(new ActualField("list"),new FormalField(Object.class));
        return gson.fromJson(r[1].toString(), List.class);
    }
}
