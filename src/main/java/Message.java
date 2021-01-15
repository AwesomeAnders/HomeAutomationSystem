public class Message {
    String componentName, func, spaceName, newRole;
    User user;

    public Message(String spaceName, String func) {
        this.spaceName = spaceName;
        this.func = func;
    }

    public Message(String func) {
        this.func = func;
    }

    public Message(String componentName, String func, String spaceName) {
        this.componentName = componentName;
        this.func = func;
        this.spaceName = spaceName;

    }

    public Message(User user, String func) {
        this.func = func;
        this.user = user;
    }

    public Message(User user, String func, String newRole) {
        this.func = func;
        this.user = user;
        this.newRole = newRole;
    }

    public Message(String componentName, String func, String spaceName, User user) {
        this.componentName = componentName;
        this.func = func;
        this.spaceName = spaceName;
        this.user = user;
    }

    public Message(String list, User user) {
        this.func = list;
        this.user = user;
    }

    public Message(String spaceName, String add, User user) {
        this.spaceName = spaceName;
        this.func = add;
        this.user = user;
    }
}
