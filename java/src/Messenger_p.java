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
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.TextEvent;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Messenger {

   // reference to physical database connection.
   private Connection _connection = null;

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

         for (int i=1; i<=numCol; ++i){
                 record.add(rs.getString(i));
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
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\nMAIN MENU");
                System.out.println("---------");
                System.out.println("1. Add to contact list");
                System.out.println("2. Browse contact list");
                System.out.println("3. Write a new message");
                System.out.println("4. Delete account");
                System.out.println("5. Browse Contact" + "\\" + "Block List");
                System.out.println("6. Chats ");
                System.out.println(".........................");
                System.out.println("9. Log out");
                switch (readChoice()){
                   case 1: AddToContact(esql, authorisedUser); break;
                   case 2: ListContacts(esql); break;
                   case 3: NewMessage(esql); break;
                   case 4: DeleteAccount(esql,authorisedUser); break;
                   case 5: BrowseCBList(esql); break;
                   case 6: Chats(esql, authorisedUser); break;
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
   public static String LogIn(Messenger esql){
      try{
         System.out.print("\tEnter user login: ");
         String login = in.readLine();
         System.out.print("\tEnter user password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM Usr WHERE login = '%s' AND password = '%s'", login, password);
         int userNum = esql.executeQuery(query);
	 if (userNum > 0)
		return login;
         return null;
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end


   public static void AddToContact(Messenger esql, String currLogin){
      try{
          System.out.print("\tEnter the user's phone number: ");
          String phone;
          List<List<String>> userToAdd;
          do {
              phone = in.readLine();
              String selectUser = String.format("SELECT login FROM USR WHERE phoneNum = '%s'", phone);
              userToAdd = esql.executeQueryAndReturnResult(selectUser);
              if (userToAdd.isEmpty()) {
                  System.out.println("User not exists, please try another.\n");
                  System.out.print("\tEnter the user's phone number: ");
              }
          }while(userToAdd.isEmpty());

          String selectContactList = String.format("SELECT contact_list FROM USR WHERE login = '%s'", currLogin);
          List<List<String>> contact_list = esql.executeQueryAndReturnResult(selectContactList);

          String queryUpdate = String.format("INSERT INTO USER_LIST_CONTAINS(list_id, list_member) VALUES('%s', '%s')", contact_list.get(0).get(0), userToAdd.get(0).get(0));
          esql.executeUpdate(queryUpdate);
          System.out.println("User added successfully!\n");
      }catch(Exception e) {
          System.err.println (e.getMessage());
          return;
      }

   }//end

   public static void ListContacts(Messenger esql){

   }//end

   public static void NewMessage(Messenger esql){
      // Your code goes here.
      // ...
      // ...
   }//end

   public static void DeleteAccount(Messenger esql, String authorizedUser){
       try{
           System.out.print("\tDo you want to delete your account? (type y to confirm) ");
           String choice = in.readLine();
           if(!choice.equals("y")) {
               return;
           }

           String query = String.format("SELECT init_sender FROM CHAT WHERE init_sender = '%s'", authorizedUser);
           int userNum = esql.executeQuery(query);

           if(userNum > 0){
               System.out.print("\tSorry, there are linked information to this account. It cannot be deleted");
               return;
           }
           String deletion = String.format("DELETE FROM USR WHERE login = '%s'", authorizedUser);
           esql.executeUpdate(deletion);
           System.out.println("\tUser deleted successfully!\nBye!");
           System.exit(0);
       }catch(Exception e){
           System.err.println(e.getMessage ());
           return;
       }
   }


   public static void BrowseCBList(Messenger esql){
       try{
        System.out.print("\tWhich list would you like to view?\n1): Contact List\n2): Block List\n");
        String choice = in.readLine();
        if(choice.equals("1"))
        {
            System.out.print("Yay this works::::: Contact List");
            return;
        }
        System.out.print("This works too::: Block list");
        return;
       }catch(Exception e) {
                System.err.println (e.getMessage());
                return;
       
   }//end Query6
   }

   public static void Chats(Messenger esql, String authorisedUser){
      try{
        boolean cont = true;
        while (cont) {
            //WRITE the chats that user belongs to... Automatically
            System.out.println("\nCHATS MENU:\n");
            
            
          //  List<HashMap<String, String>> ownerHold; 
            List<List<String>> permitCheck;
            String query = String.format("SELECT chat_id FROM CHAT_LIST CL WHERE CL.member = '%s' order by chat_id DESC", authorisedUser);
            List<List<String>>ownerHold = esql.executeQueryAndReturnResult(query);
            
            //check to see if they do not belong to any CHATS
            if(ownerHold.isEmpty()){
                System.out.println("Sorry, you do not have any CHATS. Choose below to get started: \n");
            }
            //else portion to above conditional
            //If they belong to any chat, let them select a chat to do something with
            System.out.println("Please choose a CHAT:\n");
            for(int i=0; i < ownerHold.size(); i++)
            {
                System.out.println(ownerHold.get(i).get(0) + "\n");
            }
            System.out.println("Chat choice: ");

            //receive input from user about which chat they wish to look at
            //chatNum holds which chat user wishes to interact with
            int chat_num = Integer.parseInt(in.readLine()); 
            //Validate CHAT selection input and either output error or display
            // **OWNER** Privileges
            //System.out.println(chat_num + "\n"); 

            String query2 = String.format("SELECT C.init_sender FROM CHAT C " 
                                + "WHERE C.chat_id = '%s' ", chat_num);
            permitCheck = esql.executeQueryAndReturnResult(query2);

            System.out.println("\nCHAT MENU:");
            System.out.println("----------");
            int i = 0;
            //replaces all whitespace 
            String Temp = permitCheck.get(0).get(0).replaceAll("\\s+","");
            if(Temp.equals(authorisedUser)){
                
                //Only display if OWNER of chat
                System.out.format("%d) Add member to chat\n", ++i);
                System.out.format("%d) Delete member from chat\n", ++i);
            }

            List<List<String>> chat_option;
            
            System.out.format("%d) Read chat messages\n", ++i);
            System.out.format("%d) Create new message\n", ++i);
            System.out.format("%d) Edit message\n", ++i);
            System.out.format("%d) Delete message\n", ++i);
            System.out.format("%d) Delete chat\n", ++i );
            System.out.format("%d) Return to MAIN MENU\n", ++i);
           
            System.out.println("\nSelection Number: ");
            int chat_choice = Integer.parseInt(in.readLine());

            if(chat_choice == 8)
            {
                return;
            }

        }

    
        }catch(Exception e) {
                System.err.println (e.getMessage());
                return;


   }
}
}//end Messenger
