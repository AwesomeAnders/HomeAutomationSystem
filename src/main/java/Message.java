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

}
