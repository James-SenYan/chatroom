import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class Protocol {
  private String username;
  private ConcurrentHashMap<String, ServerThread> clientMap;
  private DataInputStream is;
  private DataOutputStream os;
  public static final int MAXIMUM_CONNECTIONS = 10;
  static final String dir = System.getProperty("user.dir");

  /**
   * Protocol class constructor.
   *
   * @param clientMap a ConcurrentHashMap contains all info on connected clients.
   * @param is       server input stream.
   * @param os       server output stream.
   */
  public Protocol(ConcurrentHashMap<String, ServerThread> clientMap, DataInputStream is, DataOutputStream os) {
    this.clientMap = clientMap;
    this.is = is;
    this.os = os;
    this.username = null;
  }

  /**
   * Returns this Protocol's username.
   *
   * @return username.
   */
  public String getUsername() {
    return username;
  }

  /**
   * Sets this Protocol's username to input string.
   *
   * @param username a string representing username.
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * Returns this Protocol's client map.
   *
   * @return username.
   */
  public ConcurrentHashMap<String, ServerThread> getClientMap() {
    return clientMap;
  }

  /**
   * Sets this Protocol's client map.
   *
   * @param username a string representing username.
   */
  public void setClientMap(String username) {
    ConcurrentHashMap<String, ServerThread> copy = this.clientMap;
    copy.remove(username);
    this.clientMap = copy;
  }

  /**
   * Processes input identifier and write response based on the protocol.
   *
   * @param identifier an integer representing message type based on given chat room protocol.
   * @throws IOException handles all messages that in incorrect format.
   */
  public void processInput(int identifier) throws IOException {
    switch (identifier){
      case Identifiers.CONNECT_MESSAGE:
        handleLogin();
        break;
      case Identifiers.DISCONNECT_MESSAGE:
        handleLogoff();
        break;
      case Identifiers.QUERY_CONNECTED_USERS:
        handleQueryUsers();
        break;
      case Identifiers.DIRECT_MESSAGE:
        handleDirectMsg();
        break;
      case Identifiers.BROADCAST_MESSAGE:
          handleBroadcastMsg();
          break;
      case Identifiers.SEND_INSULT:
          handleInsultMsg();
          break;
    }
  }




  private void handleLogin() throws IOException {
    int sizeOfUser = is.readInt();
    String username = StringByteArrayTransfer.byteArrayToString(is, sizeOfUser);
    String out;
    boolean isConnected = false;
    if (this.clientMap.containsKey(username.toString()))
      out = "Username has been used.";
    else {
      int size = this.clientMap.size();
      if (size < MAXIMUM_CONNECTIONS) {
        setUsername(username.toString());
        out = "Hello, " + username + ". There are " + size + " other connected clients.";
        isConnected = true;
      } else {
        out = "Chat room is full. Retry later.";
      }
    }
    os.writeInt(Identifiers.CONNECT_RESPONSE);
    os.writeBoolean(isConnected);
    os.writeInt(out.length());
    os.writeChars(out);
  }

  private void handleLogoff() throws IOException {
    int sizeOfUsername = is.readInt();
    String username = StringByteArrayTransfer.byteArrayToString(is, sizeOfUsername);
    String out;
    boolean isDisconnected = false;
    if (!this.clientMap.containsKey(username.toString()))
      out = "User doesn't exist.";
    else {
      this.setClientMap(username.toString());
      this.setUsername(null);
      out = "You are no longer connected";
      isDisconnected = true;
    }
    os.writeInt(Identifiers.DISCONNECT_RESPONSE);
    os.writeBoolean(isDisconnected);
    os.writeInt(out.length());
    os.writeChars(out);
  }

  private void handleQueryUsers() throws IOException {
    int sizeOfName = is.readInt();
    //person who is requesting
    String queryName = StringByteArrayTransfer.byteArrayToString(is, sizeOfName);
    boolean found = false;
    for (String s: this.clientMap.keySet()) {
      if (s.equals(queryName.toString())) {
        found = true;
        break;
      }
    }
    if (!found){
      os.writeInt(Identifiers.FAILED_MESSAGE);
      String out = "Non-exist username";
      os.writeInt(out.length());
      os.writeChars(out);
    }else{
      Set<String> activeUsers = this.clientMap.keySet();
      os.writeInt(Identifiers.QUERY_USER_RESPONSE);
      os.writeInt(activeUsers.size() - 1);//deduct the person who is requesting himself
      for (String user : activeUsers){
        if (user.equals(queryName)){
          continue;
        }
        os.writeInt(user.length());
        os.writeChars(user);
      }
    }
  }


  private void handleDirectMsg() throws IOException {
    int sizeOfSender = is.readInt();
    String senderName = StringByteArrayTransfer.byteArrayToString(is, sizeOfSender);
    int sizeOfRecipient = is.readInt();
    String recipientName = StringByteArrayTransfer.byteArrayToString(is, sizeOfRecipient);
    int lengthOfMsg = is.readInt();
    String msgBody = StringByteArrayTransfer.byteArrayToString(is, lengthOfMsg);
    if (!this.clientMap.containsKey(senderName) || !this.clientMap.containsKey(recipientName)){
      os.writeInt(Identifiers.FAILED_MESSAGE);
      String out = "Fail to send message, plz check you've logged in and using valid recipient name";
      os.writeInt(out.length());
      os.writeChars(out);
    }else{
      ServerThread serverThread = this.clientMap.get(recipientName);
      serverThread.getProtocol().sentMsg(senderName, msgBody);
      os.writeInt(Identifiers.DIRECT_MESSAGE);
      String out = "message sent successful";
      os.writeInt(out.length());
      os.writeChars(out);
    }
  }

  private void sentMsg(String sender, String msgBody) throws IOException {
    os.writeInt(Identifiers.DIRECT_MESSAGE);
    String out = "You got a message from " + sender + " : " + msgBody;
    os.writeInt(out.length());
    os.writeChars(out);
  }

  private void handleBroadcastMsg() throws IOException {
    int sizeOfSenderName = is.readInt();
    String senderName = StringByteArrayTransfer.byteArrayToString(is, sizeOfSenderName);
    int sizeOfMsg = is.readInt();
    String msgBody = StringByteArrayTransfer.byteArrayToString(is, sizeOfMsg);
    if (!this.clientMap.containsKey(senderName)) {
      os.writeInt(Identifiers.FAILED_MESSAGE);
      String out = "Fail to send message, plz check you've logged in and using valid recipient name";
      os.writeInt(out.length());
      os.writeChars(out);
    } else {
      for (ServerThread serverThread : this.clientMap.values()) {
        if (serverThread.getProtocol().getUsername().equals(senderName)) {
          continue;
        }
        serverThread.getProtocol().sentMsg(senderName, msgBody);
      }
      os.writeInt(Identifiers.DIRECT_MESSAGE);
      String out = "message sent successful";
      os.writeInt(out.length());
      os.writeChars(out);
    }
  }


  private void handleInsultMsg() throws IOException {
    int sizeOfSender = is.readInt();
    String senderName = StringByteArrayTransfer.byteArrayToString(is, sizeOfSender);
    int sizeOfRecipient = is.readInt();
    String recipientName = StringByteArrayTransfer.byteArrayToString(is, sizeOfRecipient);
    String path = dir + "/src/main/resources/insult_grammar.json";
    System.out.println(dir);
    Grammar grammar = new Grammar(path);
    Random random = new Random();
    RandomSentenceGenerator generator = new RandomSentenceGenerator(grammar);
    String insultBody = generator.generateASentence(random);
    //String insultBody = "You're trash";
    if (!this.clientMap.containsKey(senderName) || !this.clientMap.containsKey(recipientName)){
      os.writeInt(Identifiers.FAILED_MESSAGE);
      String out = "Fail to send message, plz check you've logged in and using valid recipient name";
      os.writeInt(out.length());
      os.writeChars(out);
    }else{
      ServerThread serverThread = this.clientMap.get(recipientName);
      serverThread.getProtocol().sentMsg(senderName, insultBody);
      os.writeInt(Identifiers.DIRECT_MESSAGE);
      String out = "message sent successful";
      os.writeInt(out.length());
      os.writeChars(out);
    }
  }



}
