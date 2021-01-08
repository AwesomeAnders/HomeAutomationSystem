import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

public class RoomHandler implements Runnable{
    private Space room;
    private String name;

    public RoomHandler(String name, SpaceRepository lobby) {
        this.name = name;

        room = new SequentialSpace();

        lobby.add(this.name, room);
    }

    @Override
    public void run() {
        Object[] request;
        //try {
            // Keep reading chat messages and printing them
            while (true) {
                /*request = room.get(new FormalField(String.class));
                if(request != null) {
                    room.put(request[0]);
                    request[0] = null;
                }*/
            }
        /*} catch (InterruptedException e) {
            e.printStackTrace();
        }*/


    }
}
