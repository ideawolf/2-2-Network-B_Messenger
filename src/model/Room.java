package model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Room {
    private static final AtomicInteger count = new AtomicInteger(0);
    private final int room_id;
    private ClientUser owner;
    private ArrayList<ClientUser> users_in_room;
    private ArrayList<Message> messages;

    public Room(ClientUser owner, ArrayList<ClientUser> users_in_room, ArrayList<Message> messages) {
        room_id = count.incrementAndGet();
        this.owner = owner;
        this.users_in_room = users_in_room;
        this.messages = messages;
    }
}
