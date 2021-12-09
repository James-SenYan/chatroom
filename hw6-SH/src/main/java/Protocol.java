import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.SizeSequence;

public class Protocol {
  private String username;
  private ConcurrentHashMap<String, ServerThread> clientMap;
  private DataInputStream is;
  private DataOutputStream os;
  public static final int MAXIMUM_CONNECTIONS = 10;

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
        //handleDirectMsg(is);
        break;
    }
  }

  private void handleLogin() throws IOException {
    int sizeOfUser = is.readInt();
    StringBuilder username = new StringBuilder();
    for (int i = 0; i < sizeOfUser; i++) {
      char c = is.readChar();
      username.append(c);
    }
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
    StringBuilder username = new StringBuilder();
    for (int i = 0; i < sizeOfUsername; i++) {
      username.append(is.readChar());
    }
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

//  private List<String> handleQuery(String[] tokens) {
//    List<String> responseList = new ArrayList<>();
//    String username = tokens[2];
//    if (!this.clientMap.containsKey(username))
//      responseList.add("Non-exist username.");
//    else {
//      for (String name : this.clientMap.keySet()) {
//        if (!name.equals(username))
//          responseList.add(name);
//      }
//    }
//    return responseList;
//  }

  private void handleQueryUsers() throws IOException {
    int sizeOfName = is.readInt();
    //person who is requesting
    StringBuilder queryName = new StringBuilder();
    for (int i = 0; i < sizeOfName; i++) {
      queryName.append(is.readChar());
    }
    if (!this.clientMap.containsKey(queryName)){
      os.writeInt(Identifiers.FAILED_MESSAGE);
      String out = "Non-exist username";
      os.writeInt(out.length());
      os.writeChars(out);
    }else{
      Set<String> activeUsers = this.clientMap.keySet();
      os.writeInt(Identifiers.QUERY_USER_RESPONSE);
      os.writeInt(activeUsers.size() - 1);//deduct the person who is requesting himself
      for (String user : activeUsers){
        os.writeInt(user.length());
        os.writeChars(user);
      }
    }
  }

  private String handleDirectMsg(String[] tokens){
    String sizeOfSender = tokens[1];
    String senderName = tokens[2];
    String sizeOfRecipient = tokens[3];
    String recipientName = tokens[4];
    String lengthOfMsg = tokens[5];
    return "final out";

  }
}
