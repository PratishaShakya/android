package susankyatech.com.consultancymanagement.Model;

public class Message {
    public int sender_id;
    public int receiver_id;
    public String message;

    public Message(int sender_id, int receiver_id, String message) {
        this.sender_id = sender_id;
        this.receiver_id = receiver_id;
        this.message = message;
    }
}
