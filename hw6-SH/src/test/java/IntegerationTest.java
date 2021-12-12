import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 */
public class IntegerationTest {
  private Server server;
  private Client client;
  private ByteArrayOutputStream byteArrayOutputStream;
  private ByteArrayInputStream byteArrayInputStream;

  @BeforeEach
  void setup() throws IOException {

  }

  @Test
  public void test() throws IOException {
    server = new Server(8000);
    server.run();
    String userInput = """
        localhost
        8000
        login sen
        """;
    byteArrayInputStream = new ByteArrayInputStream(userInput.getBytes());
    System.setIn(byteArrayInputStream);
    byteArrayOutputStream = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(byteArrayOutputStream);
    System.setOut(ps);
    Client.main(new String[]{});
    System.setIn(System.in);
    System.setOut(System.out);
    assertEquals("Hello, sen. There are", byteArrayInputStream.toString());
  }

}
