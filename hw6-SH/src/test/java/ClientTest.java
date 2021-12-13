import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
  void setUp() throws IOException, InterruptedException {
    //mock a server
    String text = """
        localhost
        8000
        ？
        who
        login sen
        who
        @all hello
        @user sen hello
        logoff
        login sen
        !user haoyu
        @user haoyu hello
        @all hello
        """;

    InputStream stream = new ByteArrayInputStream(text.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    dos = new DataOutputStream(out);
    System.setIn(stream);
    //System.setOut(ps);
    dis = new DataInputStream(stream);
    startServer();
    System.out.println("*****");
    Client.main(new String[]{});
    //mockClient = new Client("localhost", 8000);
    //startClient();
    String standardOut = "19 3 sen";
    assertNotEquals(standardOut, out.toString());
  }

  private void startServer() throws InterruptedException {
    Thread thread = new Thread() {
      @Override
      public void run() {
        try {
          server = new Server(8000);
        } catch (IOException e) {
          e.printStackTrace();
        }
        try {
          server.run();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    };
    thread.start();
  }

  private void startClient() {
    Thread thread = new Thread() {
      @Override
      public void run() {
        String text = """
            localhost
            8000
            login haoyu
            """;
        InputStream stream = new ByteArrayInputStream(text.getBytes());
        System.setIn(stream);
        Client.main(new String[]{});
      }
    };
    thread.start();
  }


  @Test
  void main() throws IOException {

    //mockClient.handleCmdFromUser(mockClient);
    //assertEquals(standardOut, out.toString());
  }


}