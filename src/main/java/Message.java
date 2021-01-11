import com.google.gson.Gson;

public class Message {
    String nameID, func;

    public Message(String nameID, String func) {
        this.nameID = nameID;
        this.func = func;
    }

    public Message(String func) {
        this.func = func;
    }

}
