import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
   * Sets this KKProtocol's username to input string.
   *
   * @param username a string representing username.
   */
  public void setUsername(String username) {
    this.username = username;
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
        handleLogoff(tokens);
        break;
    }
    return response;
  }

  private String handleLogin(String[] tokens) {
    String username = tokens[2];
    String out;
    if (this.clientMap.containsKey(username))
      out = "Username has been used.";
    else {
      int size = this.clientMap.size();
      if (size < 10) {
        setUsername(username);
        out = "There are " + size + " other connected clients.";
      } else {
        out = "Chat room is full. Retry later.";
      }
    }
    return out;
  }

  private void handleLogoff(String[] tokens) {
    String msg = "You are no longer connected";
    String out = Identifiers.CONNECT_RESPONSE + " " + msg.length() + " " + msg;
    this.out.println(out);
  }

}
