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

  static Scanner scanner = new Scanner(System.in);
  private String serverName;
  private int serverPort;
  private Socket socket;
  private DataOutputStream clientOut;
  private DataInputStream clientIn;
  //username who logged in
  private String username;
  //client logging status
  private boolean logged;

  /**
   * Client constructor
   * @param serverName server name
   * @param serverPort server port number
   */
  public Client(String serverName, int serverPort) {
    this.logged = false;
    this.serverName = serverName;
    this.serverPort = serverPort;
  }

  /**
   * Entrance of the client.
   * @param args users input from terminal
   */
  public static void main(String[] args) {
    boolean connected = false;
    Client client = null;
    do {
      //ask users to enter serverName and portNumber
      checkInMenu();
      String server = scanner.nextLine();
      int port = Integer.parseInt(scanner.nextLine());
      client = new Client(server, port);
      connected = client.connect();
    } while (!connected);
    System.out.println("connect successful");
    client.startUIThread(client);
    client.startHandleThread();
  }

  /**
   * Print out this menu to ask users to input server name and port number
   */
  private static void checkInMenu() {
    System.out.println("Enter server name and port number: ");
  }

  /**
   * print out this instruction menu when user types in ?
   */
  private static void printMenu() {
    System.out.println("logoff: sends a DISCONNECT_MESSAGE to the server");
    System.out.println("who: sends a QUERY_CONNECTED_USERS to the server");
    System.out.println("@user: sends a DIRECT_MESSAGE to the specified user to the server");
    System.out.println(
        "@all: sends a BROADCAST_MESSAGE to the server, to be sent to all users connected");
    System.out.println(
        "!user: sends a SEND_INSULT message to the server, to be sent to the specified user");
  }

  /**
   * UI thread is used to listening to command from terminal
   * @param client the client
   */
  private void startUIThread(Client client) {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          handleCmdFromUser(client);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
  }

  /**
   * Start a thread to handle response from server.
   */
  private void startHandleThread() {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          handleResponseFromServer();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
  }

  /**
   * Handle response from server based on protocol
   * @throws IOException throw an IO exception
   */
  private void handleResponseFromServer() throws IOException {
    while (true) {
      int identifier = clientIn.readInt();
      switch (identifier) {
        case Identifiers.CONNECT_RESPONSE:
          boolean success = clientIn.readBoolean();
          int msgSize = clientIn.readInt();
          String msgBody = StringByteArrayTransfer.byteArrayToString(clientIn, msgSize);
          System.out.println("Response from server: " + msgBody);
          break;
        case Identifiers.DISCONNECT_RESPONSE:
          success = clientIn.readBoolean();
          msgSize = clientIn.readInt();
          msgBody = StringByteArrayTransfer.byteArrayToString(clientIn, msgSize);
          if (success){
            System.out.println("Response from server: " + msgBody);
            this.logged = false;
          }
          break;
        case Identifiers.QUERY_USER_RESPONSE:
          int activeUsers = clientIn.readInt();
          if (activeUsers == 0) {
            System.out.println("You're the only active user in chat room");
            break;
          }
          for (int i = 0; i < activeUsers; i++) {
            int sizeOfName = clientIn.readInt();
            String name = StringByteArrayTransfer.byteArrayToString(clientIn, sizeOfName);
            System.out.println("Active user: " + name);
          }
          break;
        case Identifiers.DIRECT_MESSAGE:
          int sizeOfMsg = clientIn.readInt();
          String receive = StringByteArrayTransfer.byteArrayToString(clientIn, sizeOfMsg);
          System.out.println(receive);
          break;
        case Identifiers.FAILED_MESSAGE:
          int failureMsgSize = clientIn.readInt();
          String failureMsg = StringByteArrayTransfer.byteArrayToString(clientIn, failureMsgSize);
          System.out.println("Failure message: " + failureMsg);
          break;
      }
    }
  }

  /**
   * Handle command typed in via terminal by users
   * @param client current client connecting to the server
   * @throws IOException throw an IO exception
   */
  public void handleCmdFromUser(Client client) throws IOException {
    //read user command from terminal
    while (true) {
      String cmd = "";
      System.out.println("Enter cmd: ");
      System.out.println("You can enter ? to see instruction of using chat room");
      cmd = scanner.nextLine();
      //String[] tokens = cmd.split(" ");
      if (cmd.equals("?")) {
        printMenu();
      } else if (cmd.contains("login")) {//one client can only log once
        //handle login process
        if (!this.logged) {
          String[] tokens = cmd.split(" ", 2);
          client.handleLogin(tokens);
        } else {
          System.out.println("You must log in before moving forward!!");
        }
      } else if ((cmd.contains("logoff") || cmd.contains("quit")) && this.logged) {
        //handle logoff process
        String[] tokens = cmd.split(" ");
        client.handleLogoff(tokens);
      } else if (cmd.contains("@user") && this.logged) {
        String[] tokens = cmd.split(" ", 3);
        client.handleDirectMsg(tokens);
      } else if (cmd.contains("@all") && this.logged) {
        String[] tokens = cmd.split(" ", 2);
        client.handleBroadcastMsg(tokens);
      } else if (cmd.contains("who") && this.logged) {
        client.handleQueryUsers();
      } else if (cmd.contains("!user") && this.logged) {
        String[] tokens = cmd.split(" ", 2);
        client.handleInsultMsg(tokens);
      } else {
        System.out.println("You must log in before moving forward!!");
      }
    }
  }

  /**
   * Send a random insult to recipient
   * @param tokens user input cmd
   */
  private void handleInsultMsg(String[] tokens) throws IOException {
    String recipient = tokens[1];
    String out = Identifiers.SEND_INSULT + " " + this.username.length() + " "
        + this.username + " " + recipient.length() + " " + recipient + " ";
    this.clientOut.writeInt(Identifiers.SEND_INSULT);
    this.clientOut.writeInt(this.username.length());
    this.clientOut.writeChars(this.username);
    this.clientOut.writeInt(recipient.length());
    this.clientOut.writeChars(recipient);
  }

  /**
   * Handle user query request(who)
   */
  private void handleQueryUsers() throws IOException {
    this.clientOut.writeInt(Identifiers.QUERY_CONNECTED_USERS);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Handle sending a broadcast msg request
   *
   * @param tokens user input cmd
   */
  private void handleBroadcastMsg(String[] tokens) throws IOException {
    String msgBody = tokens[1];
    String out = Identifiers.BROADCAST_MESSAGE + " " + this.username.length() + " "
        + this.username + " " + msgBody.length() + " " + msgBody;
    this.clientOut.writeInt(Identifiers.BROADCAST_MESSAGE);
    this.clientOut.writeInt(this.username.length());
    this.clientOut.writeChars(this.username);
    this.clientOut.writeInt(msgBody.length());
    this.clientOut.writeChars(msgBody);
  }

  /**
   * Handle sending a msg directly to specific user
   *
   * @param tokens user input cmd
   */
  private void handleDirectMsg(String[] tokens) throws IOException {
    String recipient = tokens[1];
    String msgBody = tokens[2];//这里有问题，应该是tokens【1】之后的所有信息都要传给msgBody
    String out = Identifiers.DIRECT_MESSAGE + " " + this.username.length() + " "
        + this.username + " " + recipient.length() + " " + recipient + " " + msgBody.length() + " "
        + msgBody;
    this.clientOut.writeInt(Identifiers.DIRECT_MESSAGE);
    this.clientOut.writeInt(this.username.length());
    this.clientOut.writeChars(this.username);
    this.clientOut.writeInt(recipient.length());
    this.clientOut.writeChars(recipient);
    this.clientOut.writeInt(msgBody.length());
    this.clientOut.writeChars(msgBody);

  }

  /**
   * Handle user logging off request
   *
   * @param tokens user input cmd
   */
  private void handleLogoff(String[] tokens) throws IOException {
    this.clientOut.writeInt(Identifiers.DISCONNECT_MESSAGE);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Handle user logging in request
   *
   * @param tokens user input cmd
   */
  private void handleLogin(String[] tokens) throws IOException {
    this.logged = true;
    String username = tokens[1];
    this.username = username;
    this.clientOut.writeInt(Identifiers.CONNECT_MESSAGE);
    this.clientOut.writeInt(username.length());
    this.clientOut.writeChars(username);
  }

  /**
   * Connect client to server
   *
   * @return return true if it connects successfully otherwise return false
   */
  private boolean connect() {
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