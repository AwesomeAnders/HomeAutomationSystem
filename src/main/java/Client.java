import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

        while (running) {
            System.out.println("Press 1 to see all rooms");
            System.out.println("Press 2 to add a new room");
            System.out.println("Press 3 to join a room");
            System.out.println("Press 4 to quit");

            String scanned = scan.next();
            int choice = Integer.parseInt(scanned);
            switch (choice) {
                case 1:
                    break;
                case 2:
                    String name = scan.next();
                    Message msg = new Message(name, "add");
                    String jsonMsg = gson.toJson(msg);
                    RemoteSpace rooms = new RemoteSpace(REMOTE_URI);
                    System.out.println("Json message was: " + jsonMsg);
                    System.out.println("Inserted room named: " + name);
                    rooms.put(jsonMsg);
                    break;
            }
        }
    }
}
