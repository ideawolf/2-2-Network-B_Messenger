package model;

public class Message {
    ClientUser sender;
    String msg;

    public Message(ClientUser sender, String msg) {
        this.sender = sender;
        this.msg = msg;
    }
}
