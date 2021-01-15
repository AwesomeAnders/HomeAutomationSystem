import com.google.gson.Gson;
import com.google.gson.JsonSerializer;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;

import java.io.IOException;
import java.util.*;

public class Client {
    private final static String REMOTE_URI = "tcp://127.0.0.1:9001/lobbySpace?keep";
    //For testing and autologin
    private final static String ADMIN_USERID = "admin";
    private final static String ADMIN_PWD = "admin123";

    private static String userName;
    private static String pwd;

    private static User user;

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("--- Welcome ---");
        loginPromt();
    }

    private static void loginPromt() throws IOException, InterruptedException {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        while(running) {
            System.out.println("Press 1 to login");
            System.out.println("Press 2 to create user");
            System.out.println("Press 3 to see existing users");
            System.out.println("Press 4 to login as admin");

            String scanned = scan.next();
            RemoteSpace remoteSpace = new RemoteSpace(REMOTE_URI);
            Gson gson = new Gson();
            //String userName, pwd;
            int choice = Integer.parseInt(scanned);
            switch (choice) {
                case 1:
                    System.out.println("Please enter userName");
                    userName = scan.next();
                    System.out.println("Please enter password");
                    pwd = scan.next();
                    user = new User(userName,pwd);
                    remoteSpace.put(gson.toJson(new Message(user,"login")));

                    Object[] loggedInResponse = remoteSpace.get(new ActualField("loggedInResponse"),new FormalField(Boolean.class));
                    System.out.println("-- log-in successful?: "+ loggedInResponse[1]+ " --");
                    if((Boolean) loggedInResponse[1]) {
                        mainPrompt();
                    }
                    break;
                case 2:
                    System.out.println("Please enter a new userName");
                    userName = scan.next();
                    System.out.println("Please enter a new password");
                    pwd = scan.next();
                    user = new User(userName,pwd);
                    remoteSpace.put(gson.toJson(new Message(user,"createUser")));
                    break;
                case 3:
                    //Sends request
                    remoteSpace.put(gson.toJson(new Message("showUsers")));

                    //Retrieve response
                    List response = formatResponse(remoteSpace, "userList");

                    if(response.isEmpty()) {
                        System.out.println("-- No users exist -- ");
                    } else {
                        System.out.println("-- There is " + response.size() + " user(s) available");
                        for (int i = 0; i < response.size(); i++) {
                            System.out.println("-- User " + (i+1) + " named: \"" + response.get(i) + "\" --");
                        }
                        System.out.println("\n");
                    }
                    break;
                case 4:
                    user = new User(ADMIN_USERID,ADMIN_PWD);
                    remoteSpace.put(gson.toJson(new Message(user,"login")));
                    adminPromt();
                    break;
            }
        }
    }



    private static void mainPrompt() throws IOException, InterruptedException {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        Gson gson = new Gson();
        String role;
        String spaceName;
        String componentName;
        String jsonMsg;
        Object[] response;

        while (running) {
            System.out.println("Press 1 to see all rooms");
            System.out.println("Press 2 to add a new room");
            System.out.println("Press 3 to add a new component");
            System.out.println("Press 4 to delete a component");
            System.out.println("Press 5 to update a component");
            System.out.println("Press 6 to request role");
            System.out.println("Press 7 to quit");

            String scanned = scan.next();
            Message msg;
            RemoteSpace remoteSpace = new RemoteSpace(REMOTE_URI);
            int choice = Integer.parseInt(scanned);
            switch (choice) {
                case 1:
                    //Sends request
                    msg = new Message("list");
                    remoteSpace.put(gson.toJson(msg));

                    //Retrieve listResponse
                    List listResponse = formatResponse(remoteSpace, "list");

                    if(listResponse.isEmpty()) {
                        System.out.println("-- No rooms is available -- ");
                    } else {
                        System.out.println("-- There is " + listResponse.size() + " room(s) available");
                        for (int i = 0; i < listResponse.size(); i++) {
                            System.out.println("-- room " + (i+1) + " named: \"" + listResponse.get(i) + "\" --");
                        }
                        System.out.println("\n");
                    }
                    break;
                case 2:
                    System.out.println("Please enter new room name");
                    spaceName = scan.next();
                    msg = new Message(spaceName, "add");
                    remoteSpace.put(gson.toJson(msg));

                    getResponse(msg.func, remoteSpace);

                    break;
                case 3:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    Message addCompMsg = new Message(componentName, "addComp", spaceName);
                    jsonMsg = gson.toJson(addCompMsg);
                    remoteSpace.put(jsonMsg);

                    getResponse(addCompMsg.func, remoteSpace);
                    break;

                case 4:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    msg = new Message(componentName, "deleteComp", spaceName);
                    jsonMsg = gson.toJson(msg);
                    remoteSpace.put(jsonMsg);

                    getResponse(msg.func, remoteSpace);
                    break;

                case 5:
                    System.out.println("Enter name of space");
                    spaceName = scan.next();
                    System.out.println("Enter name of component");
                    componentName = scan.next();
                    msg = new Message(componentName, "updateComp", spaceName);
                    jsonMsg = gson.toJson(msg);
                    remoteSpace.put(jsonMsg);

                    getResponse(msg.func, remoteSpace);
                    break;

                case 6:
                    System.out.println("Choose between current roles:");
                    System.out.println("-- admin, user --");
                    role = scan.next();
                    msg = new Message(user, "requestRole", role);

                    System.out.println("-- awaiting response from admin --");
                    remoteSpace.put(gson.toJson(msg));

                    Object[] isRoleUpdated = remoteSpace.get(new ActualField("updatedResponse"),new FormalField(Boolean.class));
                    if((Boolean) isRoleUpdated[1]) {
                        user.setRole(User.Roles.valueOf(role));
                        System.out.println("-- Role have been granted and updated --\n");
                    } else {
                        System.out.println("-- Role request have been denied --\n");
                    }
                    break;
                case 7:
                    System.out.println("-- Logged out --\n");
                    running = false;
                    break;
            }
        }
    }

    private static void adminPromt() throws IOException {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        RemoteSpace remoteSpace = new RemoteSpace(REMOTE_URI);

        while (running) {
            System.out.println("Press 1 to await inbound role change requests");
            System.out.println("Press 2 to quit admin panel");

            String scanned = scan.next();
            int choice = Integer.parseInt(scanned);
            switch (choice) {
                case 1:
                    System.out.println("-- Awaiting inbound request --");
                    try {
                        Object[] response = remoteSpace.get(new ActualField("roleChange"), new FormalField(User.class), new FormalField(String.class));
                        System.out.println("-- the user: " + ((User) response[1]).getUserName() + " with role: " + ((User) response[1]).getRole() + " wants to change role to: " + response[2] + " --");
                        System.out.println("Do you accept (y/n)");
                        String answer = scan.next().toLowerCase();
                        if(answer.equals("y") || answer.equals("yes")) {
                            remoteSpace.put("roleResponse", true);
                        } else {
                            remoteSpace.put("roleResponse", false);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    running = false;
                    break;
            }
        }
    }

    private static List formatResponse(RemoteSpace remoteSpace, String msg) throws InterruptedException {
        Gson gson = new Gson();
        Object[] r = remoteSpace.get(new ActualField(msg),new FormalField(Object.class));
        return gson.fromJson(r[1].toString(), List.class);
    }

    public static void getResponse(String func, RemoteSpace remoteSpace){

        boolean msgReceived = false;

        while (!msgReceived) {
            Object[] response = new Object[0];
            try {
                response = remoteSpace.getp(new ActualField("Error"), new FormalField(String.class));
                if (response == null)
                    response = remoteSpace.getp(new ActualField(func), new FormalField(String.class));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (response != null){
                System.out.println(response[1]);
                msgReceived = true;
            }
        }
    }
}
