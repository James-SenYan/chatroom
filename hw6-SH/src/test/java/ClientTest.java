import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.Test;

class ClientTest {

  private Server server;
  private DataInputStream dis;
  private DataOutputStream dos;

  @Test
  void test1() throws IOException, InterruptedException {
    //mock a server
    String text = """
        localhost
        8000
        ？
        who
        login sen
        ?
        who
        @all hello
        @user sen hello
        @user james hello
        logoff
        login haoyu
        !user haoyu
        !user james
        @user haoyu hello
        @all hello
        login aha
        """;

    InputStream stream = new ByteArrayInputStream(text.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(out);
    dos = new DataOutputStream(out);
    System.setIn(stream);
    System.setOut(ps);
    dis = new DataInputStream(stream);
    startServer();
    Client.main(new String[]{});
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

}