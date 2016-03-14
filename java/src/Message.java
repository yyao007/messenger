// Message.java
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;

// Message class
public class Message {
    // a message contains...
    int msg_id = -1; // -1 as non-exist
    String msg_text = null;
    String msg_timestamp = null;
    String sender = null;
    int chat_id = 0;
    
    public Message(String text, String timestamp, String sender, int chat_id) {
        this.msg_text = text;
        this.msg_timestamp = timestamp;
        this.sender = sender;
        this.chat_id = chat_id;
    }
    
    public void setMsgId(int msg_id) {
        this.msg_id = msg_id;
        return;
    }
    
    public int getMsgId() {
        return this.msg_id;
    }
    
    public String getText() {
        return this.msg_text;
    }
    
    public String getTimestamp() {
        return this.msg_timestamp;
    }
    
    public String getSender() {
        return this.sender;
    }
    
    public int getChatId() {
        return this.chat_id;
    }
    
}