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
import java.util.concurrent.ConcurrentHashMap;

public class Protocol {
  private String username;
  private ConcurrentHashMap<String, ServerThread> clientMap;
  private BufferedReader reader;
  private PrintWriter out;

  /**
   * Protocol class constructor.
   *
   * @param clientMap a ConcurrentHashMap contains all info on connected clients.
   * @param reader       server input stream.
   * @param out       server output stream.
   */
  public Protocol(ConcurrentHashMap<String, ServerThread> clientMap, BufferedReader reader, PrintWriter out) {
    this.clientMap = clientMap;
    this.reader = reader;
    this.out = out;
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
   * Processes input identifier and write response based on the protocol.
   *
   * @param identifier an integer representing message type based on given chat room protocol.
   * @throws IOException handles all messages that in incorrect format.
   */
  public String processInput(int identifier, String[] tokens) throws IOException {
    String response = "";
    switch (identifier){
      case 19:
        response = handleLogin(tokens);
        break;
      case 21:
        response = handleLogoff(tokens);
        break;
      case 22:
        List<String> users = handleQuery(tokens);
        for (String user: users) {
          response += user + ", ";
        }
        response = response.substring(0, response.length() - 2);
        break;
    }
    return response;
  }

  private String handleLogin(String[] tokens) {
    String username = tokens[2];
    String out;
    boolean isConnected = false;
    if (this.clientMap.containsKey(username))
      out = "Username has been used.";
    else {
      int size = this.clientMap.size();
      if (size < 10) {
        setUsername(username);
        out = "Hello, " + username + ". There are " + size + " other connected clients.";
        isConnected = true;
      } else {
        out = "Chat room is full. Retry later.";
      }
    }
    String finalout = Identifiers.CONNECT_RESPONSE + " " + isConnected + " "
        + out.length() + " " + out;
    return finalout;
  }

  private String handleLogoff(String[] tokens) {
    String username = tokens[2];
    String out;
    if (!this.clientMap.containsKey(username))
      out = "User doesn't exist.";
    else {
      out = username + ", you are logged off";
    }
    String finalout = Identifiers.CONNECT_RESPONSE + " " + out.length() + " " + out;
    return finalout;
  }

  private List<String> handleQuery(String[] tokens) {
    List<String> responseList = new ArrayList<>();
    String username = tokens[2];
    if (!this.clientMap.containsKey(username))
      responseList.add("Non-exist username.");
    else {
      for (String name : this.clientMap.keySet()) {
        if (!name.equals(username))
          responseList.add(name);
      }
    }

    return responseList;
  }

}
