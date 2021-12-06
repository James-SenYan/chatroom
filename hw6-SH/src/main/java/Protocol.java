import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class Protocol {
  private String username;
  private ConcurrentHashMap<String, ServerThread> clientMap;
  private DataInputStream dis;
  private DataOutputStream dos;

  /**
   * Protocol class constructor.
   *
   * @param clientMap a ConcurrentHashMap contains all info on connected clients.
   * @param dis       server input stream.
   * @param dos       server output stream.
   */
  public Protocol(ConcurrentHashMap<String, ServerThread> clientMap, DataInputStream dis, DataOutputStream dos) {
    this.clientMap = clientMap;
    this.dis = dis;
    this.dos = dos;
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
   * Processes input identifier and write response based on the protocol.
   *
   * @param identifier an integer representing message type based on given chat room protocol.
   * @throws IOException handles all messages that in incorrect format.
   */
  public void processInput(int identifier) throws IOException {
    System.out.println("processInput");
  }
}
