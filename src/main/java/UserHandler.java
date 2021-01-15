import com.google.gson.Gson;
import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserHandler implements Runnable{
    private String command;
    private Space clientSpace;
    private Space lobbySpace;
    private User user;
    private User.Role newRole;
    private final Gson gson = new Gson();

    public UserHandler(String func, Space lobbySpace, Space clientSpace) {
        this.command = func;
        this.lobbySpace = lobbySpace;
        this.clientSpace = clientSpace;
    }

    public UserHandler(User user, String command, Space clientSpace, Space lobbySpace) {
        this.user = user;
        this.clientSpace = clientSpace;
        this.command = command;
        this.lobbySpace = lobbySpace;
    }

    public UserHandler(User user, String command, User.Role newRole, Space lobbySpace, Space clientSpace){
        this.user = user;
        this.command = command;
        this.newRole = newRole;
        this.lobbySpace = lobbySpace;
        this.clientSpace = clientSpace;
    }

    @Override
    public void run() {
        switch (command) {
            case "showUsers":
                showUsers();
                break;
            case "createUser":
                createUser();
                break;
            case "login":
                login();
                break;
            case "requestRole":
                updateRole();
                break;
        }
    }

    private void updateRole() {
        System.out.println("username: " + user.getUserName() + " userId: " + user.getUserID() + " role: " + user.getRole() + " new role: " + newRole);
        try {
            lobbySpace.put("roleChange", user,newRole);

            Object[] response = lobbySpace.get(new ActualField("roleResponse"), new FormalField(Boolean.class));
            if((Boolean) response[1]) {
                Object[] exist = clientSpace.getp(new ActualField(user.getUserName()),new ActualField(user.getUserID()),new ActualField(user.getRole()),new ActualField(user.getPwd()));
                if(exist[0] != null) {
                    clientSpace.put(user.getUserName(), user.getUserID(), newRole,user.getPwd());
                }
                lobbySpace.put("updatedResponse", true);
            } else {
                lobbySpace.put("updatedResponse", false);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createUser() {
        try {
            System.out.println("username: " + user.getUserName() + " userId: " + user.getUserID() + " role: " + user.getRole() + " pwd: " +user.getPwd());
            clientSpace.put(user.getUserName(), user.getUserID(), user.getRole(),user.getPwd());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        System.out.println("username: " + user.getUserName() + " userId: " + user.getUserID() + " role: " + user.getRole() + " pwd: " + user.getPwd());
        try {
            Object[] exist = clientSpace.queryp(new ActualField(user.getUserName()),new FormalField(Integer.class), new FormalField(Enum.class),new ActualField(user.getPwd()));
            if(!user.getRole().equals(User.Role.admin)) {
                if (exist != null) {
                    lobbySpace.put("loggedInResponse", true);
                } else {
                    lobbySpace.put("loggedInResponse", false);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showUsers() {
        List<String> responseList = new ArrayList<>();

        //testing only
        //responseList.add("hej");

        try {
            List<Object[]> list = clientSpace.queryAll(new FormalField(String.class),new FormalField(Integer.class),new FormalField(Enum.class),new FormalField(String.class));
            for (int i = 0; i < list.size(); i++) {
                Object[] indv = list.get(i);
                System.out.println(Arrays.toString(list.get(i)));
                responseList.add(indv[0].toString());
            }
            lobbySpace.put("userList",gson.toJson(responseList));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
