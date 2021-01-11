public class Message {
    String componentName, func, spaceName;

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

}
