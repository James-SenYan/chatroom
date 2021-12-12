import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {

  private Client mockClient;
  private Server server;
  private DataInputStream dis;
  private DataOutputStream dos;
  private ServerThread serverThread;

  @BeforeEach
  void setUp() throws IOException {
    //mock a server
    server = new Server(8000);
    mockClient = new Client("localhost", 8000);

  }

  @Test
  void main() throws IOException {
    String text = "login sen";
    InputStream stream = new ByteArrayInputStream(text.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    dos = new DataOutputStream(out);
    System.setIn(stream);
    System.setOut(ps);
    dis = new DataInputStream(stream);
    String standardOut = "19 3 sen";
    mockClient.handleCmdFromUser(mockClient);
    assertEquals(standardOut, out.toString());
  }
}