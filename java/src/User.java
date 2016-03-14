// User.java
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
// User class 

public class User {
    // user object 
    String login = null;
    String phoneNum = null;
    String password = null;
    String status = null;
    List<User> contact_list = null; // contains a list of users in contact list
    List<User> block_list = null;  // contians a list of users in block list
    List<Chat> chat_list = null; // a chat list
    
    public User(String login, String password, String phoneNum) {
        this.login = login;
        this.password = password;
        this.phoneNum = phoneNum;
    }
    
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
        return;
    }
    
    public void setStatus(String status) {
        this.status = status;
        return;
    }
    
    public String getLogin() {
        return this.login;
    }
    
    public String getPhoneNum() {
        return this.phoneNum;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public void set_contact_list(List<List<String>> list) {
        this.contact_list = new ArrayList<User>();
        for (int i = 0; i < list.size(); ++i) {
            User contact = new User(list.get(i).get(0), "", list.get(i).get(1));
            if (list.get(i).size() > 2) {
                contact.setStatus(list.get(i).get(2));
            }
            this.contact_list.add(contact);
        }
        return;
    }
    
    public void set_block_list(List<List<String>> list) {
        this.block_list = new ArrayList<User>();
        for (int i = 0; i < list.size(); ++i) {
            User block = new User(list.get(i).get(0), "", list.get(i).get(1));
            if (list.get(i).size() > 2) {
                block.setStatus(list.get(i).get(2));
            }
            this.block_list.add(block);
        }
        return;
    }
    
    public void set_chat_list(List<List<String>> list) {
        this.chat_list = new ArrayList<Chat>();
        for (int i = 0; i < list.size(); ++i) {
            Chat temp = new Chat(list.get(i).get(0), list.get(i).get(1));
            temp.setChatId(Integer.parseInt(list.get(i).get(2)));
            this.chat_list.add(temp);
        }
        return;
    }
    
    public List<User> get_contact_list() {
        return this.contact_list;
    }
    
    public List<User> get_block_list() {
        return this.block_list;
    }
    
    public List<Chat> get_chat_list() {
        return this.chat_list;
    }
    
    public void addContact(User contact) {
            //User contactToAdd = new User(contact.get(0).get(0), "", contact.get(0).get(1));
            this.contact_list.add(contact);
    }
    
    public void addBlock(User block) {
           // User blockToAdd = new User(login, "", phoneNum);
            this.block_list.add(block);
    }
    
    public void deleteContact(String login) {
        for(int i = 0; i < contact_list.size(); ++i) {
            if (contact_list.get(i).getLogin().equals(login)) {
                contact_list.remove(i);
                return;
            }
        }
        return;
    }
    
    public void deleteBlock(String login) {
        for(int i = 0; i < block_list.size(); ++i) {
            if (block_list.get(i).getLogin().equals(login)) {
                block_list.remove(i);
                return;
            }
        }
        return;
    }
    
}