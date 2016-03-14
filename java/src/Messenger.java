/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.PreparedStatement;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;
import java.util.Date;
 
/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

    // reference to physical database connection.
    private Connection _connection = null;
    static User authorisedUser = null;

    // handling the keyboard inputs through a BufferedReader
    // This variable can be global for convenience.
    static BufferedReader in = new BufferedReader(
                                 new InputStreamReader(System.in));

    /**
     * Creates a new instance of Messenger
     *
     * @param hostname the MySQL or PostgreSQL server hostname
     * @param database the name of the database
     * @param username the user name used to login to the database
     * @param password the user login password
     * @throws java.sql.SQLException when failed to make a connection.
     */
    public Messenger (String dbname, String dbport, String user, String passwd) throws SQLException {

       System.out.print("Connecting to database...");
       try{
          // constructs the connection URL
          String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
          System.out.println ("Connection URL: " + url + "\n");

          // obtain a physical connection
          this._connection = DriverManager.getConnection(url, user, passwd);
          System.out.println("Done");
       }catch (Exception e){
          System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
          System.out.println("Make sure you started postgres on this machine");
          System.exit(-1);
       }//end catch
    }//end Messenger

    /**
     * Method to execute an update SQL statement.  Update SQL instructions
     * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
     *
     * @param sql the input SQL string
     * @throws java.sql.SQLException when update failed
     */
    public void executeUpdate (String sql) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the update instruction
       stmt.executeUpdate (sql);

       // close the instruction
       stmt.close ();
    }//end executeUpdate

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and outputs the results to
     * standard out.
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQueryAndPrintResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;

       // iterates through the result set and output them to standard out.
       boolean outputHeader = true;
       while (rs.next()){
      if(outputHeader){
         for(int i = 1; i <= numCol; i++){
     	System.out.print(rsmd.getColumnName(i) + "\t");
         }
         System.out.println();
         outputHeader = false;
      }
          for (int i=1; i<=numCol; ++i)
             System.out.print (rs.getString (i) + "\t");
          System.out.println ();
          ++rowCount;
       }//end while
       stmt.close ();
       return rowCount;
    }//end executeQuery

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the results as
     * a list of records. Each record in turn is a list of attribute values
     *
     * @param query the input query string
     * @return the query result as a list of records
     * @throws java.sql.SQLException when failed to execute the query
     */
    public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       /*
        ** obtains the metadata object for the returned result set.  The metadata
        ** contains row and column info.
        */
       ResultSetMetaData rsmd = rs.getMetaData ();
       int numCol = rsmd.getColumnCount ();
       int rowCount = 0;

       // iterates through the result set and saves the data returned by the query.
       boolean outputHeader = false;
       List<List<String>> result  = new ArrayList<List<String>>();
       while (rs.next()){
           List<String> record = new ArrayList<String>();
           for (int i=1; i<=numCol; ++i) {
               String temp = rs.getString(i);
               if (rs.wasNull()) {
                   temp = "";
               }
               record.add(temp.trim());
//              System.out.format("%d: "+rs.getString(i).trim(), i);
          }
          
          result.add(record);
       }//end while
       stmt.close ();
       return result;
    }//end executeQueryAndReturnResult

    /**
     * Method to execute an input query SQL instruction (i.e. SELECT).  This
     * method issues the query to the DBMS and returns the number of results
     *
     * @param query the input query string
     * @return the number of rows returned
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int executeQuery (String query) throws SQLException {
        // creates a statement object
        Statement stmt = this._connection.createStatement ();

        // issues the query instruction
        ResultSet rs = stmt.executeQuery (query);

        int rowCount = 0;

        // iterates through the result set and count nuber of results.
        if(rs.next()){
           rowCount++;
        }//end while
        stmt.close ();
        return rowCount;
    }

    /**
     * Method to fetch the last value from sequence. This
     * method issues the query to the DBMS and returns the current
     * value of sequence used for autogenerated keys
     *
     * @param sequence name of the DB sequence
     * @return current value of a sequence
     * @throws java.sql.SQLException when failed to execute the query
     */
    public int getCurrSeqVal(String sequence) throws SQLException {
     Statement stmt = this._connection.createStatement ();

     ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
     if (rs.next())
     	return rs.getInt(1);
     return -1;
    }

    /**
     * Method to close the physical connection if it is open.
     */
    public void cleanup(){
       try{
          if (this._connection != null){
             this._connection.close ();
          }//end if
       }catch (SQLException e){
          // ignored.
       }//end try
    }//end cleanup

    // This function is to inti and refresh the authorisedUser object
    public void refresh() {
        try{
            // get contact list and block list
            String getContacts = String.format("SELECT login, phoneNum, status FROM USR WHERE login IN (SELECT list_member FROM USR, USER_LIST_CONTAINS WHERE contact_list = list_id AND login = '%s')", authorisedUser.getLogin());
            String getBlocks = String.format("SELECT login, phoneNum, status FROM USR WHERE login IN (SELECT list_member FROM USR, USER_LIST_CONTAINS WHERE block_list = list_id AND login = '%s')", authorisedUser.getLogin());
            
            List<List<String>> contacts = executeQueryAndReturnResult(getContacts);
            List<List<String>> blocks = executeQueryAndReturnResult(getBlocks);
           
            this.authorisedUser.set_contact_list(contacts);
            this.authorisedUser.set_block_list(blocks);
            
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
       }
    }
    
    // This function is to init and refresh chat list and message list
    public void refreshChats() {
        try {
            // get chat list
            String getChats = String.format("SELECT c.chat_type, c.init_sender, c.chat_id FROM "
                                        + "CHAT c, CHAT_LIST l WHERE c.chat_id = l.chat_id "
                                        + "AND l.member = '%s'", authorisedUser.getLogin());
                
            List<List<String>> chats = executeQueryAndReturnResult(getChats);
            this.authorisedUser.set_chat_list(chats);
            // set message list
            List<Chat> chat_list = authorisedUser.get_chat_list();
            for(int i = 0; i < chat_list.size(); ++i) {
                String getMessages = String.format("SELECT msg_text, msg_timestamp, "
                                                    + "sender_login, msg_id FROM "
                                                    + "MESSAGE WHERE chat_id = %d "
                                                    + "ORDER BY msg_timestamp DESC",
                                                    chat_list.get(i).getChatId());
                List<List<String>> messages = executeQueryAndReturnResult(getMessages);
                chat_list.get(i).setMsgList(messages);
                
                // use receiver's login as chat name if it's a private chat
                if (chat_list.get(i).getType().equals("private")) {
                    String getMember = String.format("SELECT member FROM CHAT_LIST WHERE "
                                                    + "chat_id = %d AND NOT(member = '%s')",
                                                    chat_list.get(i).getChatId(), 
                                                    authorisedUser.getLogin());
                    List<List<String>> member = executeQueryAndReturnResult(getMember);
                    chat_list.get(i).setChatName(member.get(0).get(0));
                }
                // use "Group Chat(number of members)" as chat name if it's a group chat
                else {
                    String getNumber = String.format("SELECT COUNT(*) FROM CHAT_LIST "
                                                    + "WHERE chat_id = %d", 
                                                    chat_list.get(i).getChatId());
                    List<List<String>> number = executeQueryAndReturnResult(getNumber);
                    String name = "Group Chat(" + number.get(0).get(0) + ")";
                    chat_list.get(i).setChatName(name);
                }
            }
            // sort chat list according to its latest message
            Collections.sort(chat_list, new Chat());
            
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
       }    
    }
    /**
     * The main execution method
     *
     * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
     */
    public static void main (String[] args) {
       if (args.length != 3) {
          System.err.println (
             "Usage: " +
             "java [-classpath <classpath>] " +
             Messenger.class.getName () +
             " <dbname> <port> <user>");
          return;
       }//end if

       Greeting();
       Messenger esql = null;
       try{
          // use postgres JDBC driver.
          Class.forName ("org.postgresql.Driver").newInstance ();
          // instantiate the Messenger object and creates a physical
          // connection.
          String dbname = args[0];
          String dbport = args[1];
          String user = args[2];
          esql = new Messenger (dbname, dbport, user, "");

          boolean keepon = true;
          while(keepon) {
             authorisedUser = null; // reset authorisedUser each time 
             // These are sample SQL statements
             System.out.println("MAIN MENU");
             System.out.println("---------");
             System.out.println("1. Create user");
             System.out.println("2. Log in");
             System.out.println("9. < EXIT");
             switch (readChoice()){
                case 1: CreateUser(esql); break;
                case 2: LogIn(esql); break;
                case 9: keepon = false; break;
                default : System.out.println("Unrecognized choice!"); break;
             }//end switch
             if (authorisedUser != null) {
               boolean usermenu = true;
               while(usermenu) {
                 esql.refresh();
                 esql.refreshChats();
                 System.out.println("\nMAIN MENU");
                 System.out.println("---------");
                 System.out.println("1. Add to contact list");
                 System.out.println("2. Browse contact list");
                 System.out.println("3. Add to block list");
                 System.out.println("4. Browse block list");
                 System.out.println("5. Write a new message");
                 System.out.println("6. Current chats");
                 System.out.println("7. Delete account");
                 System.out.println(".........................");
                 System.out.println("9. Log out");
                 switch (readChoice()){
                    case 1: AddToContact(esql); break;
                    case 2: ListContacts(esql); break;
                    case 3: AddToBlock(esql); break;
                    case 4: ListBlocks(esql); break;
                    case 5: NewMessage(esql); break;
                    case 6: ListChats(esql); break;
                    case 7: DeleteAccount(esql); break;
                    case 9: usermenu = false; break;
                    default : System.out.println("Unrecognized choice!"); break;
                 }
               }
             }
          }//end while
       }catch(Exception e) {
          System.err.println (e.getMessage ());
       }finally{
          // make sure to cleanup the created table and close the connection.
          try{
             if(esql != null) {
                System.out.print("Disconnecting from database...");
                esql.cleanup ();
                System.out.println("Done\n\nBye !");
             }//end if
          }catch (Exception e) {
             // ignored.
          }//end try
       }//end try
    }//end main

    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting

    /*
     * Reads the users choice given from the keyboard
     * @int
     **/
    public static int readChoice() {
       int input;
       // returns only if a correct value is given.
       do {
          System.out.print("Please make your choice: ");
          try { // read the integer, parse it and break.
             input = Integer.parseInt(in.readLine());
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }//end try
       }while (true);
       return input;
    }//end readChoice

    public static boolean isInteger(String s) {
        try{
            Integer.parseInt(s);
            return true;
            }catch(NumberFormatException nfe) {
                return false;
            }
    }
    /*
     * Creates a new user with privided login, passowrd and phoneNum
     * An empty block and contact list would be generated and associated with a user
     **/
    public static void CreateUser(Messenger esql){
       try{
          System.out.print("\tEnter user login: ");
          // find if the login already exits
          String login;
          int loginNum;
          do {
             login = in.readLine();
             String query = String.format("SELECT * FROM usr WHERE login = '%s'", login);
             loginNum = esql.executeQuery(query);
             if (loginNum > 0) {
                 System.out.println("\tThis login is already existed, please try another.\n");
                 System.out.print("\tEnter user login: ");
             }
          } while(loginNum > 0);

          System.out.print("\tEnter user password: ");
          String password = in.readLine();
          System.out.print("\tEnter user phone: ");
          // find if the phone number already exits
          String phone;
          int userNum;
          do {
             phone = in.readLine();
             String query = String.format("SELECT * FROM usr WHERE phoneNum = '%s'", phone);
             userNum = esql.executeQuery(query);
             if (userNum > 0) {
                 System.out.println("\tThis phone number is already existed, please try another.\n");
                 System.out.print("\tEnter user phone: ");
             }
          } while(userNum > 0);

      //Creating empty contact\block lists for a user
          esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('block')");
          int block_id = esql.getCurrSeqVal("user_list_list_id_seq");
          esql.executeUpdate("INSERT INTO USER_LIST(list_type) VALUES ('contact')");
          int contact_id = esql.getCurrSeqVal("user_list_list_id_seq");

          String query = String.format("INSERT INTO USR (phoneNum, login, password, block_list, contact_list) VALUES ('%s','%s','%s',%s,%s)", phone, login, password, block_id, contact_id);

          esql.executeUpdate(query);
          System.out.println ("User successfully created!");
       }catch(Exception e){
          System.err.println (e.getMessage ());
       }
    }//end

    /*
     * Check log in credentials for an existing user
     * @return User login or null is the user does not exist
     **/
    public static void LogIn(Messenger esql){
        try{
            System.out.print("\tEnter user login: ");
            String login = in.readLine();
            login = login.replace("'", "''");
            System.out.print("\tEnter user password: ");
            String password = in.readLine();
              
              
              
            String query = String.format("SELECT phoneNum FROM USR WHERE login = '%s' AND password = '%s'", login, password);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
    
            if (!result.isEmpty()) {
            // initialize authorisedUser
                authorisedUser = new User(login, password, result.get(0).get(0));
                esql.refresh();
                esql.refreshChats();
         	    return;
            }
            System.out.println("Incorrect username or password.");
            return;
        }catch(Exception e){
            System.err.println (e.getMessage ());
            return;
        }
    }//end
    
    
    public static void AddToContact(Messenger esql){
        try{
        
            System.out.print("\tEnter the user's phone number(b to go back): ");
            String phone;
            List<List<String>> userToAdd;
            do {
                phone = in.readLine();

                if (phone.equals("b")) {
                    return;
                }

                String selectUser = String.format("SELECT login, phoneNum, status FROM USR WHERE phoneNum = '%s'", phone);
                userToAdd = esql.executeQueryAndReturnResult(selectUser);
                if (userToAdd.isEmpty()) {
                    System.out.println("\tUser not exists, please try another.\n");
                    System.out.print("\tEnter the user's phone number(b to go back): ");
                }
               
                else {
                    for (int i = 0; i < authorisedUser.get_contact_list().size(); i++) {
                        if (authorisedUser.get_contact_list().get(i).getLogin().equals(userToAdd.get(0).get(0))) {
                            System.out.println("\tUser is in your contact list!\n");
                            System.out.print("\tEnter the user's phone number(b to go back): ");
                            userToAdd.clear();
                            break;
                        }
                    }
                }
            }while(userToAdd.isEmpty());
           
            User contact = new User(userToAdd.get(0).get(0), "", userToAdd.get(0).get(1));
            contact.setStatus(userToAdd.get(0).get(2));
            AddToContact(esql, contact);
            return;
       }catch(Exception e) {
           System.err.println(e.getMessage());
           return;
       }

    }//end
    
    public static void AddToContact(Messenger esql, User contact) {
        try {
            String selectContactList = String.format("SELECT contact_list FROM USR WHERE login = '%s'", authorisedUser.getLogin());
            List<List<String>> contact_list = esql.executeQueryAndReturnResult(selectContactList);
            
            // check if the contact is in the block list
            String getBlock = String.format("SELECT u.block_list FROM USR u, USER_LIST_CONTAINS c"
                                + " WHERE u.block_list = c.list_id AND c.list_member = '%s'"
                                + " AND u.login = '%s'", contact.getLogin(), authorisedUser.getLogin());
            List<List<String>> inBlock = esql.executeQueryAndReturnResult(getBlock);
            // delete from block list first
            if(!inBlock.isEmpty()){
                String delete = String.format("DELETE FROM USER_LIST_CONTAINS WHERE "
                                + "list_id  = '%s' AND list_member = '%s'",
                                inBlock.get(0).get(0), contact.getLogin());
                esql.executeUpdate(delete);
                authorisedUser.deleteBlock(contact.getLogin());
            }
                    
            String queryUpdate = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES('%s', '%s')", contact_list.get(0).get(0), contact.getLogin());
            esql.executeUpdate(queryUpdate);
               
            // update authorisedUser also
            //authorisedUser.addContact(contact);
            esql.refresh();
               
            System.out.println("User added to contact list successfully!\n");
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void AddToBlock(Messenger esql) {
        try {
            List<List<String>> usr_block_check;
            String usr_block_num;
            do {
                System.out.print("\tEnter the user's phone number(b to go back): ");
                
                //get input from user as to who is to be blocked
                usr_block_num = in.readLine();
                
                if (usr_block_num.equals("b")) {
                    return;
                }
                //Check USR table to ensure valid phone # to be blocked
                String check_phone = String.format("SELECT U.login FROM USR U" 
                                        + " WHERE U.phoneNum = '%s'" , usr_block_num);
                usr_block_check = esql.executeQueryAndReturnResult(check_phone);
                if(usr_block_check.isEmpty()){
                    System.out.println("\tUser not exists\n");
                }
                else {
                    break;
                }
            } while(true);
            
            User block = new User(usr_block_check.get(0).get(0), "", usr_block_num);
            
            AddToBlock(esql, block);
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    } 
    
    public static void AddToBlock(Messenger esql, User block) {
        try {
            String getBlock = String.format("SELECT u.block_list FROM USR u, USER_LIST_CONTAINS c"
                            + " WHERE u.block_list = c.list_id AND c.list_member = '%s'"
                            + " AND u.login = '%s'", block.getLogin(), authorisedUser.getLogin());
            List<List<String>> inBlock = esql.executeQueryAndReturnResult(getBlock);
            
            if(!inBlock.isEmpty()){
                System.out.println("\tUser already in block list.");
                return;
            }
            
            //check if member is part of USR contact list
            String getContact = String.format("SELECT u.contact_list FROM USR u, USER_LIST_CONTAINS c"
                            + " WHERE u.contact_list = c.list_id AND c.list_member = '%s'"
                            + " AND u.login = '%s'", block.getLogin(), authorisedUser.getLogin());
            List<List<String>> inContact = esql.executeQueryAndReturnResult(getContact);
            
            // delete the user to be blocked from contact list first
            if(!inContact.isEmpty()){
                
                String delete = String.format("DELETE FROM USER_LIST_CONTAINS WHERE "
                                + "list_id  = '%s' AND list_member = '%s'",
                                inContact.get(0).get(0), block.getLogin());
                esql.executeUpdate(delete);
                authorisedUser.deleteContact(block.getLogin());
            }
            
            //ADD member to USR block_list
            String getList = String.format("SELECT block_list FROM USR WHERE login = '%s'", authorisedUser.getLogin());
            List<List<String>> block_list_id = esql.executeQueryAndReturnResult(getList);
            String addToBlock = String.format("INSERT INTO USER_LIST_CONTAINS"
                                + "(list_id, list_member) VALUES('%s', '%s')",
                                block_list_id.get(0).get(0),
                                block.getLogin());
            esql.executeUpdate(addToBlock);
            // update authorisedUser
            esql.refresh();
            //authorisedUser.addBlock(block);
            System.out.println("User added to block list successfully!");
            return;

        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void DeleteContact(Messenger esql, User contact) {
        try {
            String delete = String.format("SELECT contact_list FROM USR WHERE login = '%s'", authorisedUser.getLogin());
            List<List<String>> list_id = esql.executeQueryAndReturnResult(delete);
            String queryDelete = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id = '%s' AND list_member = '%s'", list_id.get(0).get(0), contact.getLogin());
            esql.executeUpdate(queryDelete);
            esql.refresh();
            //authorisedUser.deleteContact(contact.getLogin());
            
            System.out.println("Contact deleted successfully!");
            
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void DeleteBlock(Messenger esql, User block) {
        try {
            String delete = String.format("SELECT block_list FROM USR WHERE login = '%s'", authorisedUser.getLogin());
            List<List<String>> list_id = esql.executeQueryAndReturnResult(delete);
            String queryDelete = String.format("DELETE FROM USER_LIST_CONTAINS WHERE list_id = '%s' AND list_member = '%s'", list_id.get(0).get(0), block.getLogin());
            esql.executeUpdate(queryDelete);
            esql.refresh();
            //authorisedUser.deleteBlock(block.getLogin());
            
            System.out.println("Block deleted successfully!");
            
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void ListContacts(Messenger esql){
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            
            while (true) {
                esql.refresh();
                esql.refreshChats();
                List<User> contacts = authorisedUser.get_contact_list();
                // check empty list
                if (contacts.isEmpty()) {
                    System.out.println("\nEmpty");
                    return;
                }
                
                k += 10;
                System.out.println(String.format("\n%-23sStatus", "Contact"));
                for (i = k-10; i < contacts.size() && i < k; ++i) {
                    System.out.println(String.format("%d. %-20s"
                                        + contacts.get(i).getStatus(),
                                        i, contacts.get(i).getLogin()));
                }
                
                boolean isGoing = true;
                do {
                    
                    System.out.print("\nChoose a contact(b to go back, m to view more): ");
                    String choice = in.readLine();

                    if (choice.equals("b")) {
                        if (i > 10) {
                            k -= 20;
                            i = k;
                            break;
                        }
                        else {
                            return;
                        }
                    }
                    else if(choice.equals("m")) {
                        if (i <= k && contacts.size() <= k) {
                            System.out.println("No more contacts.");
                        }
                        else {
                            break;
                        }
                    }
                    else if (isInteger(choice)) {
                        int index = Integer.parseInt(choice);
                        if (index < i && index >= k-10) {
                            System.out.println(contacts.get(index).getLogin() + ":");
                            System.out.println("1. Send message");
                            System.out.println("2. Add to block list");
                            System.out.println("3. Delete contact");
                            switch (readChoice()) {
                                case 1: NewMessage(esql, contacts.get(index)); isGoing = false;
                                        k -= 10; i = k; break;
                                case 2: AddToBlock(esql, contacts.get(index)); isGoing = false;
                                        k -= 10; i = k; break;
                                case 3: DeleteContact(esql, contacts.get(index)); isGoing = false;
                                        k -= 10; i = k; break;
                                default: System.out.println("Unrecognized choice!"); break;
                                        
                            }
                        }
                    }
                    else {
                        System.out.println("Unrecognized choice!");
                        continue;
                    }
                }while (isGoing);

            }

        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }//end
    
    public static void ListBlocks(Messenger esql){
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            
            while (true) {
                List<User> blocks = authorisedUser.get_block_list();
                // check empty list
                if (blocks.isEmpty()) {
                    System.out.println("\nEmpty");
                    return;
                }
                
                k += 10;
                System.out.print("\nBlocks\n");
                for (i = k-10; i < blocks.size() && i < k; ++i) {
                    System.out.format("%d. ", i);
                    System.out.println(blocks.get(i).getLogin());
                }
                
                boolean isGoing = true;
                do {
                    
                    System.out.print("\nChoose a block(b to go back, m to view more): ");
                    String choice = in.readLine();

                    if (choice.equals("b")) {
                        if (i > 10) {
                            k -= 20;
                            i = k;
                            break;
                        }
                        else {
                            return;
                        }
                    }
                    else if(choice.equals("m")) {
                        if (i <= k && blocks.size() <= k) {
                            System.out.println("No more blocks.");
                        }
                        else {
                            break;
                        }
                    }
                    else if (isInteger(choice)) {
                        int index = Integer.parseInt(choice);
                        if (index < i && index >= k-10) {
                            System.out.println(blocks.get(index).getLogin() + ":");
                            System.out.println("1. Add to contact list");
                            System.out.println("2. Delete block");
                            
                            switch (readChoice()) {
                                case 1: AddToContact(esql, blocks.get(index)); isGoing = false;
                                        k -= 10; i = k; break;
                                case 2: DeleteBlock(esql, blocks.get(index)); isGoing = false;
                                        k -= 10; i = k; break;
                                default: System.out.println("Unrecognized choice!"); break;
                            }
                        }
                    }
                    else {
                        System.out.println("Unrecognized choice!");
                        continue;
                    }
                }while (isGoing);

            }

        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }

    public static void NewMessage(Messenger esql) {
        try {
            System.out.println("1. Enter user's login");
            System.out.println("2. Choose from contact list");
            System.out.println("3. Back");
            
            int choice = readChoice();
            if (choice == 3) {
                return;
            }
            
            else if (choice == 1) {
                List<List<String>> user;
                do {
                    System.out.print("\tEnter the login name of user: ");
                    String getUser = in.readLine();
                    
                    String exist = String.format("SELECT login, phoneNum FROM USR WHERE login = '%s'", getUser);
                    user = esql.executeQueryAndReturnResult(exist);
                    if (user.isEmpty()) {
                        System.out.println("\tUser not exist!");
                    }
                }while (user.isEmpty());
                
                User receiver = new User(user.get(0).get(0), "", user.get(0).get(1));
                NewMessage(esql, receiver);
                return;
            }
            
            else if (choice == 2) {
                // add user from contact list
                List<User> contact_list = authorisedUser.get_contact_list();
                List<String> user_list = new ArrayList<String>();
                
                for (int i = 0; i < contact_list.size(); ++i) {
                    user_list.add(contact_list.get(i).getLogin());
                }
                
                List<String> receivers = ChooseUsers(user_list);
                // add user himself to the chat member
                receivers.add(authorisedUser.getLogin());
                NewMessage(esql, receivers);
            }
            
            else {
                System.out.println("Unrecognized choice!");
            }
                
            
            
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    // this function is to start a private chat
    public static void NewMessage(Messenger esql, User receiver){
        try {
            System.out.println("\nEnter text(type BBB to go back): ");
            String text = in.readLine();
            if (text.equals("BBB")) {
                return;
            }
            
            int chat_id = -1;
            // find if the user had a chat with the receiver
            String findChat = String.format("SELECT chat_id, count(*) FROM chat_list "
                            + "WHERE chat_id IN (SELECT c1.chat_id FROM chat_list c1, "
                            + "chat_list c2 WHERE c1.chat_id = c2.chat_id AND "
                            + "c1.member = '%s' AND c2.member = '%s') GROUP BY chat_id",
                             authorisedUser.getLogin(), receiver.getLogin());
            List<List<String>> chatId = esql.executeQueryAndReturnResult(findChat);
            // check if the chat has 2 members
            if (!chatId.isEmpty()) {
                for (int i = 0; i < chatId.size(); ++i) {
                    if (chatId.get(i).get(1).equals("2")) {
                        chat_id = Integer.parseInt(chatId.get(i).get(0));
                        break;
                    }
                }
            }

            if (chat_id == -1) {
                // insert a new chat into database
                String newChat = String.format("INSERT INTO CHAT(chat_type, init_sender) VALUES('private', '%s')",
                                                authorisedUser.getLogin());
                esql.executeUpdate(newChat);
                chat_id = esql.getCurrSeqVal("chat_chat_id_seq");
                String update1 = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES(%d, '%s')",
                                                        chat_id, authorisedUser.getLogin());
                String update2 = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES(%d, '%s')",
                                                        chat_id, receiver.getLogin());
                esql.executeUpdate(update1);
                esql.executeUpdate(update2);
            }
            
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            
            String sendMsg = String.format("INSERT INTO MESSAGE(msg_text, msg_timestamp, sender_login, chat_id) "
                                        + "VALUES('%s', ?, '%s', %d)", text, authorisedUser.getLogin(), chat_id);
            PreparedStatement pstmt = esql._connection.prepareStatement(sendMsg);
            pstmt.setTimestamp(1, ts);
            pstmt.executeUpdate();
            
            System.out.println("Message sent!");
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }


    }//end
    
    public static void NewMessage(Messenger esql, List<String> receivers){
        try {
            System.out.println("\nEnter text(type BBB to go back): ");
            String text = in.readLine();
            if (text.equals("BBB")) {
                return;
            }
            
            String type = null;
            if (receivers.size() > 2) {
                type = "group";
            }
            else {
                type = "private";
            }
            
            String newChat = String.format("INSERT INTO CHAT(chat_type, init_sender) VALUES('%s', '%s')",
                                            type, authorisedUser.getLogin());
            esql.executeUpdate(newChat);
            int chat_id = esql.getCurrSeqVal("chat_chat_id_seq");
            
            for (int i = 0; i < receivers.size(); ++i) {
                String update = String.format("INSERT INTO CHAT_LIST(chat_id, member) VALUES(%d, '%s')",
                                                        chat_id, receivers.get(i));
                esql.executeUpdate(update);
            }
            
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            
            String sendMsg = String.format("INSERT INTO MESSAGE(msg_text, msg_timestamp, sender_login, chat_id) "
                                        + "VALUES('%s', ?, '%s', %d)", text, authorisedUser.getLogin(), chat_id);
            PreparedStatement pstmt = esql._connection.prepareStatement(sendMsg);
            pstmt.setTimestamp(1, ts);
            pstmt.executeUpdate();
            
            System.out.println("Message sent!");
            
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void NewMessage(Messenger esql, Chat chat){
        try {
            System.out.println("\nEnter text(Type BBB to go back): ");
            String text = in.readLine();
            if (text.equals("BBB")) {
                return;
            }
            
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);
            String sendMsg = String.format("INSERT INTO MESSAGE(msg_text, msg_timestamp, sender_login, chat_id) "
                                        + "VALUES('%s', ?, '%s', %d)", text, authorisedUser.getLogin(), chat.getChatId());
            PreparedStatement pstmt = esql._connection.prepareStatement(sendMsg);
            pstmt.setTimestamp(1, ts);
            pstmt.executeUpdate();
            System.out.println("Message sent!");
            esql.refreshChats();
            
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }


    }//end

    public static void DeleteAccount(Messenger esql){
        try{
            System.out.print("\tDo you want to delete your account? (type y to confirm) ");
            String choice = in.readLine();
            if(!choice.equals("y")) {
                return;
            }

            String query = String.format("SELECT init_sender FROM CHAT WHERE init_sender = '%s'", authorisedUser.getLogin());
            int userNum = esql.executeQuery(query);

            if(userNum > 0){
                System.out.print("\tSorry, there are linked information to this account. It cannot be deleted");
                return;
            }
            String deletion = String.format("DELETE FROM USR WHERE login = '%s'", authorisedUser.getLogin());
            esql.executeUpdate(deletion);
            System.out.println("\tUser deleted successfully!\nBye!");
            System.exit(0);
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }

    public static void ListChats(Messenger esql){
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            while (true) {  
                esql.refreshChats();
                List<Chat> chat_list = authorisedUser.get_chat_list();
                if (chat_list.isEmpty()) {
                    System.out.println("\nEmpty");
                    return;
                }
                
                k += 10;
                System.out.println(String.format("\n%-23s%-23sType", "Chat", "Last updated"));
                for (i = k-10; i < chat_list.size() && i < k; ++i) {
                    String timestamp = chat_list.get(i).get_msg_list().get(0).getTimestamp();
                    timestamp = timestamp.substring(0, 19);
                    System.out.println(String.format("%d. %-20s%-23s"
                                        + chat_list.get(i).getType(),
                                        i, chat_list.get(i).getChatName(),
                                        timestamp));
                }
                
                boolean isGoing = true;
                do {
                    System.out.print("\nChoose a chat(b to go back, m to view more): ");
                    String choice = in.readLine();

                    if (choice.equals("b")) {
                        if (i > 10) {
                            k -= 20;
                            i = k;
                            break;
                        }
                        else {
                            return;
                        }
                    }
                    else if(choice.equals("m")) {
                        if (i <= k && chat_list.size() <= k) {
                            System.out.println("No more chats.");
                        }
                        else {
                            break;
                        }
                    }
                    else if (isInteger(choice)) {
                        int index = Integer.parseInt(choice);
                        if (index < i && index >= k-10) {
                            System.out.println("\n" + chat_list.get(index).getChatName() + ":");
                            int n = 0;
                            System.out.format("%d. Read chat messages\n", ++n);
                            System.out.format("%d. Browse chat members\n", ++n);
                            if(chat_list.get(index).getInitSender().equals(authorisedUser.getLogin())) {
                                //Only display if OWNER of chat
                                System.out.format("%d. Delete chat\n", ++n);
                            }
                            System.out.format("%d. Back\n", ++n);
                            
                            int c = readChoice();
                            if (c == 1) {
                                ListMessages(esql, chat_list.get(index)); 
                                k -= 10; 
                                i = k;
                                break;
                            }
                            else if (c == 2) {
                                ListMembers(esql, chat_list.get(index));
                                k -= 10; 
                                i = k;
                                break;
                            }
                            else if (n > 2 && c == 3) {
                                DeleteChat(esql, chat_list.get(index));
                                k -= 10; 
                                i = k;
                                break;
                            }
                            else if (n > 2 && c == 4) {
                                k -= 10; 
                                i = k;
                                break;
                            }
                                
                            else if(c == 3) {
                                k -= 10; 
                                i = k;
                                break;
                            }
                        }
                    }
                    else {
                        System.out.println("Unrecognized choice!");
                        continue;
                    }
                }while (isGoing);
            }
                
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }//end
    
    public static String[] wrap(String input, int maxCharInLine){

        StringTokenizer tok = new StringTokenizer(input, " ");
        StringBuilder output = new StringBuilder(input.length());
        int lineLen = 0;
        while (tok.hasMoreTokens()) {
            String word = tok.nextToken();
    
            while(word.length() > maxCharInLine){
                output.append(word.substring(0, maxCharInLine-lineLen) + "\n");
                word = word.substring(maxCharInLine-lineLen);
                lineLen = 0;
            }
    
            if (lineLen + word.length() > maxCharInLine) {
                output.append("\n");
                lineLen = 0;
            }
            output.append(word + " ");
    
            lineLen += word.length() + 1;
        }
        
        return output.toString().split("\n");
    }
    
    public static void ListMessages(Messenger esql, Chat chat) {
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            while (true) {  
                esql.refreshChats();
                List<Message> msg_list = chat.get_msg_list();
                if (msg_list.isEmpty()) {
                    System.out.println("\nEmpty");
                    return;
                }
                
                k += 10;
                System.out.println("");
                for (i = k-10; i < msg_list.size() && i < k; ++i) {
                    // output timestamp only to minute
                    String timestamp = msg_list.get(i).getTimestamp();
                    timestamp = timestamp.substring(0, 19);
                    System.out.println(String.format("%d. %-20s"+ timestamp + ":",
                                        i, msg_list.get(i).getSender()));
                    // output message text on multiple lines without wrap
                    String[] msgTxt = wrap(msg_list.get(i).getText(), 40);
                    System.out.println(Arrays.toString(msgTxt));
                }
                
                boolean isGoing = true;
                do {
                    System.out.println("\n1. Create new message");
                    System.out.println("2. Edit a message");
                    System.out.print("Please make a choice(b to go back, m to view more): ");
                    String choice = in.readLine();

                    if (choice.equals("b")) {
                        if (i > 10) {
                            k -= 20;
                            i = k;
                            break;
                        }
                        else {
                            return;
                        }
                    }
                    else if(choice.equals("m")) {
                        if (i <= k && msg_list.size() <= k) {
                            System.out.println("No more messages.");
                        }
                        else {
                            break;
                        }
                    }
                    
                    else if (choice.equals("1")) {
                        NewMessage(esql, chat);
                        k -= 10; 
                        i = k;
                        break;
                    }
                    
                    else if (choice.equals("2")) {
                        System.out.print("Choose a message(b to go back): ");
                        String in_str = in.readLine();
                        if (in_str.equals("b")) {
                            k -= 10; 
                            i = k;
                            break;
                        }
                        
                        else if (!isInteger(in_str)) {
                            System.out.println("Unrecognized choice!");
                            continue;
                        }
                        
                        int index = Integer.parseInt(in_str);
                        if (index < i && index >= k-10) {
                            String[] msgTxt = wrap(msg_list.get(index).getText(), 50);
                            System.out.println(Arrays.toString(msgTxt) + ":");
                            
                            int n = 0;
                            
                            if(msg_list.get(index).getSender().equals(authorisedUser.getLogin())) {
                                System.out.format("%d. Edit message\n", ++n);
                                System.out.format("%d. Delete message\n", ++n);
                            }
                            System.out.format("%d. Back\n", ++n);
                            
                            int c = readChoice();
                            // n>1 indicates the author of the message
                            if (n > 1) {
                                if (c == 1) {
                                    EditMsg(esql, msg_list.get(index));
                                    k -= 10; 
                                    i = k;
                                    break;
                                }
                                else if (c == 2) {
                                    DeleteMsg(esql, msg_list.get(index));
                                    k -= 10; 
                                    i = k;
                                    break;
                                }
                                // go back
                                else if (c == 3) {
                                    k -= 10; 
                                    i = k;
                                    break;
                                }
                            }
                            // have no access to the message
                            else {
                                if (c == 1) {
                                    k -= 10; 
                                    i = k;
                                    break;
                                }
                            }
                        }
                        
                    }
                    
                    else {
                        System.out.println("Unrecognized choice!");
                        continue;
                    }
                }while (isGoing);
            }
                
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void ListMembers(Messenger esql, Chat c) {
        try {
            while (true) {
                System.out.println("\n " + c.getChatName() + ":");
                
                String getMember = String.format("SELECT member FROM CHAT_LIST WHERE "
                                                + "chat_id = %d", c.getChatId());
                List<List<String>> members = esql.executeQueryAndReturnResult(getMember);
                for (int i = 0; i < members.size(); ++i) {
                    System.out.println(String.format("%d. " + members.get(i).get(0), i));
                }
                
                // only the initial sender of the chat can modify it
                if (!c.getInitSender().equals(authorisedUser.getLogin())) {
                    return;
                }
                
                System.out.println("\n1. Add new members");
                System.out.println("2. Delete members");
                System.out.println("3. Back");
                int choice = readChoice();
                 
                if (choice == 3) {
                    return;
                }   
                else if (choice == 1) {
                    // add user from contact list
                    List<User> contact_list = authorisedUser.get_contact_list();
                    List<String> member_list = new ArrayList<String>();
                    // don't add users already are chat members
                    for (int i = 0; i < contact_list.size(); ++i) {
                        boolean isAdd = true;
                        for (int j = 0; j < members.size(); ++j) {
                            if (contact_list.get(i).getLogin().equals(members.get(j).get(0))) {
                                isAdd = false;
                                break;
                            }
                        }
                        if (isAdd) {
                            member_list.add(contact_list.get(i).getLogin());
                        }
                    }
                    List<String> memberToAdd = ChooseUsers(member_list);
                    if (!memberToAdd.isEmpty()) {
                        AddMember(esql, c, memberToAdd);
                    }
                    return;
                }
                else if (choice == 2) {  
                    if (c.getType().equals("private")) {
                        System.out.println("Can't delete members from private chat!");
                        return;
                    }
                    // make a member list 
                    List<String> member_list = new ArrayList<String>();
                    for (int i = 0; i < members.size(); ++i) {
                        // skip initial sender who is the user himself
                        if (members.get(i).get(0).equals(c.getInitSender())) {
                            continue;
                        }
                        member_list.add(members.get(i).get(0));
                    }
                    List<String> memberToDelete = ChooseUsers(member_list);
                    if (!memberToDelete.isEmpty()) {
                        DeleteMember(esql, c, memberToDelete);
                    }
                    return;
                }
                else {
                    System.out.println("Unrecognized choice!");
                    continue;
                }
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static List<String> ChooseUsers(List<String> user_list) {
        try {
            int i = 0;
            int j = 0;
            int k = 0;
            // store the user that be chosen
            List<String> chosenUser = new ArrayList<String>();
            
            while (true) {
                k += 10;
                System.out.println("");
                for (i = k-10; i < user_list.size() && i < k; ++i) {
                    System.out.println(String.format("%d. " + user_list.get(i), i));
                }
                
                boolean isGoing = true;
                do {
                    
                    System.out.print("\nChoose a user(b to go back, m to view more, f to finish choosing): ");
                    String choice = in.readLine();
    
                    if (choice.equals("b")) {
                        if (i > 10) {
                            k -= 20;
                            i = k;
                            break;
                        }
                        else {
                            return null;
                        }
                    }
                    else if(choice.equals("m")) {
                        if (i <= k && user_list.size() <= k) {
                            System.out.println("No more users.");
                        }
                        else {
                            break;
                        }
                    }
                    else if(choice.equals("f")) {
                        return chosenUser;
                    }
                    else if (isInteger(choice)) {
                        int index = Integer.parseInt(choice);
                        if (index < i && index >= k-10) {
                            chosenUser.add(user_list.get(index));
                            user_list.remove(index);
                        }
                    }
                    else {
                        System.out.println("Unrecognized choice!");
                        continue;
                    }
                }while (isGoing);
    
            }
        }catch(Exception e){
            System.err.println(e.getMessage());
            return null;
        }

    }
    
    public static void AddMember(Messenger esql, Chat c, List<String> mToAdd) {
        try {
            for (int i = 0; i < mToAdd.size(); ++i) {
                String addUser = String.format("INSERT INTO CHAT_LIST(chat_id, member) "
                                                + "VALUES(%d, '%s')", c.getChatId(), 
                                                mToAdd.get(i));
                esql.executeUpdate(addUser);
            }
            if (c.getType().equals("private")) {
                String getMember = String.format("SELECT member FROM CHAT_LIST WHERE "
                                                + "chat_id = %d", c.getChatId());
                int members = esql.executeQuery(getMember);
                if (members > 2) {
                    // update chat type to group
                    String updateType = String.format("UPDATE CHAT SET chat_type = 'group' WHERE chat_id = %d",
                                                        c.getChatId());
                    esql.executeUpdate(updateType);
                }
            }
            System.out.println("Members added successfully!");
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void DeleteMember(Messenger esql, Chat c, List<String> mToDelete) {
        try {
            String getMember = String.format("SELECT member FROM CHAT_LIST WHERE "
                                                + "chat_id = %d", c.getChatId());
            int members = esql.executeQuery(getMember);
            for (int i = 0; i < mToDelete.size(); ++i) {
                if (members == 2) {
                    String updateType = String.format("UPDATE CHAT SET chat_type = 'private' WHERE chat_id = %d",
                                                        c.getChatId());
                    esql.executeUpdate(updateType);
                    return;
                }
                String deleteUser = String.format("DELETE FROM CHAT_LIST WHERE "
                                                + "chat_id = %d AND member = '%s'",
                                                c.getChatId(), mToDelete.get(i));
                esql.executeUpdate(deleteUser);
            }
            System.out.println("Members deleted successfully!");
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void DeleteChat(Messenger esql, Chat c) {
        try {
            System.out.print("Are you sure to delete this chat? (y/n): ");
            String d_choice = in.readLine();
            if(d_choice.equals("y")){
                String d_chat = String.format("DELETE FROM CHAT WHERE "
                                            + "chat_id = %d", c.getChatId());
                esql.executeUpdate(d_chat);
                System.out.println("Chat deleted successfully!");
            }
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void EditMsg(Messenger esql, Message msg) {
        try {
            System.out.println("\nEnter text(type BBB to go back): ");
            String text = in.readLine();
            if (text.equals("BBB")) {
                return;
            }
            String update = String.format("UPDATE MESSAGE SET msg_text = '%s' WHERE msg_id = %d",
                                            text, msg.getMsgId());
            esql.executeUpdate(update);
            System.out.println("Message edited!");
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }
    }
    
    public static void DeleteMsg(Messenger esql, Message msg) {
        try {
            System.out.print("Are you sure want to delete this message?(y/n): ");
            String choice = in.readLine();
            if (!choice.equals("y")) {
                return;
            }
            String delete = String.format("DELETE FROM MESSAGE WHERE msg_id = %d", msg.getMsgId());
            esql.executeUpdate(delete);
            System.out.println("Message deleted!");
            return;
        }catch(Exception e){
            System.err.println(e.getMessage());
            return;
        }    
    }
    

}//end Messenger
