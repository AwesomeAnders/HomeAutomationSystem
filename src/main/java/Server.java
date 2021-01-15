import com.google.gson.Gson;
import org.jspace.*;

public class Server {
    //Connection
    public final static String LOCAL_HOST = "tcp://127.0.0.1:9001/?keep";
    private static Space lobbySpace;
    private static Space rooms;
    private static Space clientSpace;

    public static void main(String[] argv) throws InterruptedException, IllegalStateException {
        // Space exposed to external client
        SpaceRepository lobby = new SpaceRepository();
        lobby.addGate(LOCAL_HOST);

        // Rooms available in lobby space
        lobbySpace = new SequentialSpace();

        rooms = new SequentialSpace();
        clientSpace = new SequentialSpace();

        // Adding a space called Room to manage all lobbySpace in spaceRepo
        lobby.add("lobbySpace", lobbySpace);

        System.out.println("Remote space created");

        Gson gson = new Gson();
        String jsonString;
        Message msg;

        while (true) {

            jsonString = lobbySpace.get(new FormalField(Object.class))[0].toString();
            msg = gson.fromJson(jsonString, Message.class);

            switch (msg.func) {
                case "list":
                    System.out.println("Message was: " + msg.func);
                    new Thread(new RoomHandler(lobbySpace, rooms, msg.func)).start();
                    break;
                case "add":
                    System.out.println("Message was: " + msg.func);
                    new Thread(new RoomHandler(msg.spaceName, lobby, rooms, msg.func, lobbySpace)).start();
                    break;
                case "addComp":
                case "deleteComp":
                case "updateComp":
                case "showAll":
                    System.out.println("Message was: " + msg.func);
                    new Thread(new ComponentHandler(msg.componentName,"off", msg.func, rooms, msg.spaceName,lobbySpace)).start();
                    break;
                case "showUsers":
                    System.out.println("Message was: " + msg.func);
                    new Thread(new UserHandler(msg.func,lobbySpace, clientSpace)).start();
                    break;
                case "requestRole":
                    System.out.println("username: " + msg.user.getUserName() + " userId: " + msg.user.getUserID() + " role: " + msg.user.getRole() + " new role: " + msg.newRole);
                    new Thread(new UserHandler(msg.user, msg.func, User.Role.valueOf(msg.newRole), lobbySpace, clientSpace)).start();
                    break;
                case "createUser":
                case "login":
                    System.out.println("Message was: " + msg.func);
                    new Thread(new UserHandler(msg.user,msg.func,clientSpace,lobbySpace)).start();
                    break;
            }
        }
    }
}