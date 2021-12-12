import java.io.DataInputStream;
import java.io.IOException;

/**
 * Utility class which is used to address string-byte[] transferring problems
 */
public class StringByteArrayTransfer {

  /**
   * Used to transfer from byte array to string with a given input stream and a fixed size
   *
   * @param is   input stream
   * @param size size of the final output string
   * @return string built by data from input stream
   * @throws IOException throw an IO exception
   */
  public static String byteArrayToString(DataInputStream is, int size) throws IOException {
    StringBuilder res = new StringBuilder();
    for (int i = 0; i < size; i++) {
      res.append(is.readChar());
    }
    return res.toString();
  }
}
