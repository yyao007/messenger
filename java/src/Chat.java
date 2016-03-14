// Chat.java
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

// Chat class

public class Chat implements Comparator<Chat>, Comparable<Chat>{
    // A chat contains...
    int chat_id = 0;
    String chat_type = null;
    String init_sender = null;
    String chat_name = null;
    List<Message> msg_list = null;
    
    public Chat() {}
    
    public Chat(String type, String sender) {
        this.chat_type = type;
        this.init_sender = sender;
    }
    
    public void setChatId(int chat_id) {
        this.chat_id = chat_id;
        return;
    }
    
    public void setChatName(String name) {
        this.chat_name = name;
    }
    
    public void setMsgList(List<List<String>> list) {
        this.msg_list = new ArrayList<Message>();
        for (int i = 0; i < list.size(); ++i) {
            Message temp = new Message(list.get(i).get(0), list.get(i).get(1), 
                                        list.get(i).get(2), this.chat_id);
            temp.setMsgId(Integer.parseInt(list.get(i).get(3)));
            this.msg_list.add(temp);
        }
        return;
    }
    
    public int getChatId() {
        return this.chat_id;
    }
    
    public String getType() {
        return this.chat_type;
    }
    
    public String getInitSender() {
        return this.init_sender;
    }
    
    public String getChatName() {
        return this.chat_name;
    }
    
    public List<Message> get_msg_list() {
        return this.msg_list;
    }
    
    // Overriding the compareTo method
    public int compareTo(Chat c){
        return (this.get_msg_list().get(0).getTimestamp()).compareTo(c.get_msg_list().get(0).getTimestamp());
    }

    // Overriding the compare method 
    public int compare(Chat chat1, Chat chat2) {
        return (chat2.get_msg_list().get(0).getTimestamp().compareTo(chat1.get_msg_list().get(0).getTimestamp()));
           
    }
    
}