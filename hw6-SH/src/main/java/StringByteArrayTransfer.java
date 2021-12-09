import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 */
public class StringByteArrayTransfer {
    public static String byteArrayToString(DataInputStream is, int size) throws IOException {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < size; i++) {
            res.append(is.readChar());
        }
        return res.toString();
    }
}
