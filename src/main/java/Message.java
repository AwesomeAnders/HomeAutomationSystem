import com.google.gson.Gson;

public class Message {
    String nameID, func;

    public Message(String nameID, String func) {
        this.nameID = nameID;
        this.func = func;
        //this.args = args;
    }

    /*public String toJSON() {
        Gson gson = new Gson();
        return gson.toJson(Message);
    }*/
}
