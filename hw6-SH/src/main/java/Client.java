import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


/**
 * chat room client
 */
public class Client {

  private String serverName;
  private int serverPort;
  private Socket socket;
  private DataOutputStream clientOut;
  private DataInputStream clientIn;
  private String username;
  private boolean logged;
  static Scanner scanner = new Scanner(System.in);


  public Client(String serverName, int serverPort) {
    this.logged = false;
    this.serverName = serverName;
    this.serverPort = serverPort;
  }

  public static void main(String[] args) {
    boolean connected = false;
    Client client = null;
    do {
      //ask users to enter serverName and portNumber
      checkInMenu();
      String server= scanner.nextLine();
      int port = Integer.parseInt(scanner.nextLine());
      client = new Client(server, port);
      //goes to main menu(user interface)
      connected = client.connect();
    }while (!connected);
    System.out.println("connect successful");
    try {
      client.handleResponseFromServer(client);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleResponseFromServer(Client client) throws IOException {
    while(true){
      handleCmdFromUser(client);
      int identifier = clientIn.readInt();
      switch (identifier){
        case Identifiers.CONNECT_RESPONSE:
          boolean success = clientIn.readBoolean();
          int msgSize = clientIn.readInt();
          StringBuilder msgBody = new StringBuilder();
          for (int i = 0; i < msgSize; i++) {
            char c = clientIn.readChar();
            msgBody.append(c);
          }
          System.out.println("Response from server: " + msgBody);
          break;
        case Identifiers.QUERY_USER_RESPONSE:
          int activeUsers = clientIn.readInt();
          if (activeUsers == 0){
            System.out.println("You're the only active user in chat room");
            break;
          }
          for (int i = 0; i < activeUsers; i++) {
            int sizeOfName = clientIn.readInt();
            StringBuilder name = new StringBuilder();
            for (int j = 0; j < sizeOfName; j++) {
              name.append(clientIn.readChar());
            }
            System.out.println("Active user: " + name);
          }
          break;
        case Identifiers.FAILED_MESSAGE:
          int failureMsgSize = clientIn.readInt();
          StringBuilder failureMsg = new StringBuilder();
          for (int i = 0; i < failureMsgSize; i++) {
            failureMsg.append(clientIn.readChar());
          }
          System.out.println("Failure message from server: " + failureMsg);
          break;
      }
    }
    //System.out.println("About to close socket...Good Bye!");
    //socket.close();
  }

  private void handleCmdFromUser(Client client) throws IOException {
    //read user command from terminal
    String cmd = "";
    System.out.println("Enter cmd: ");
    System.out.println("You can enter ? to see instruction of using chat room");
    cmd = scanner.nextLine();
    String[] tokens = cmd.split(" ");
    if (cmd.equals("?")){
      printMenu();
    }else if (cmd.contains("login") && !client.logged ){//one client can only log once
      //handle login process
      client.handleLogin(tokens);
    }else if (cmd.contains("logoff") || cmd.contains("quit")){
      //handle logoff process
      client.handleLogoff(tokens);
    }else if(cmd.contains("@user")){
      client.handleDirectMsg(tokens);
    }else if(cmd.contains("@all")){
      client.handleBroadcastMsg(tokens);
    }else if (cmd.contains("who")){
      client.handleQueryUsers(tokens);
    }else if (cmd.contains("!user")){
      client.handleInsultMsg(tokens);
    }
  }

  /**
   * Send a random insult to recipient
   * @param tokens user input cmd
   */
  private void handleInsultMsg(String[] tokens) {
    String recipient = tokens[1];
    String out = Identifiers.SEND_INSULT + " " + this.username.length() + " "
        + this.username + " " + recipient.length() + " " + recipient + " ";
    //this.clientOut.println(out);
  }

  /**
   * Handle user query request(who)
   * @param tokens user input cmd
   */
  private void handleQueryUsers(String[] tokens) throws IOException {
    String out = Identifiers.QUERY_CONNECTED_USERS + " " + this.username.length() + " " + this.username;
    this.clientOut.writeInt(Identifiers.QUERY_CONNECTED_USERS);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Handle sending a broadcast msg request
   * @param tokens user input cmd
   */
  private void handleBroadcastMsg(String[] tokens) {
    String msgBody = tokens[1];
    String out = Identifiers.BROADCAST_MESSAGE + " "+ this.username.length() + " "
        + this.username + " " + msgBody.length() + " " + msgBody;
    //this.clientOut.println(out);
  }

  /**
   * Handle sending a msg directly to specific user
   * @param tokens user input cmd
   */
  private void handleDirectMsg(String[] tokens) {
    String recipient = tokens[1];
    String msgBody = tokens[2];
    String out = Identifiers.DIRECT_MESSAGE + " " + this.username.length() + " "
        + this.username + " " + recipient.length() + " " + recipient + " " + msgBody.length() + " " + msgBody;
    //this.clientOut.println(out);

  }

  /**
   * Handle user logging off request
   * @param tokens user input cmd
   */
  private void handleLogoff(String[] tokens) throws IOException {
    String out = Identifiers.DISCONNECT_MESSAGE + " " + username.length() + " " + username;
    this.clientOut.writeInt(Identifiers.DISCONNECT_MESSAGE);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Handle user logging in request
   * @param tokens user input cmd
   */
  private void handleLogin(String[] tokens) throws IOException {
    this.logged = true;
    String username = tokens[1];
    this.username = username;
    String out = Identifiers.CONNECT_MESSAGE + " " + username.length() + " " + username;
    //this.clientOut.println(out);
    this.clientOut.writeInt(Identifiers.CONNECT_MESSAGE);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Print out this menu to ask users to input server name and port number
   */
  private static void checkInMenu(){
    System.out.println("Enter server name and port number: ");
  }

  /**
   * print out this instruction menu when user types in ?
   */
  private static void printMenu() {
    System.out.println("logoff: sends a DISCONNECT_MESSAGE to the server");
    System.out.println("who: sends a QUERY_CONNECTED_USERS to the server");
    System.out.println("@user: sends a DIRECT_MESSAGE to the specified user to the server");
    System.out.println("@all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected");
    System.out.println("!user: sends a SEND_INSULT message to the server, to be sent to the specified user");
  }

  /**
   * Connect client to server
   * @return return true if it connects successfully otherwise return false
   */
  private boolean connect(){
    try {
      //create a socket connect to the server
      socket = new Socket(InetAddress.getByName(serverName), serverPort);
      //client writes to the server
      clientOut = new DataOutputStream(socket.getOutputStream());
      //receive message from the server
      clientIn = new DataInputStream(socket.getInputStream());
      return true;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }
}