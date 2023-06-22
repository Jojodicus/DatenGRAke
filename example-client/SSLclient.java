import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SSLclient {
    private static final String PUBLICKEY = "<BASE64 ENCODED PUBLIC KEY>";
    private static final String DATA = "am69ogus this is my secret message";
    private static final String HOST = "localhost";
    private static final int PORT = 7181;

    public static void main(String[] args) throws Exception {
        // load public key from base64 encoded string
        byte[] publicKeyBytes = Base64.getDecoder().decode(PUBLICKEY);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyBytes));

        // generate AES key
        SecretKey secretKey = KeyGenerator.getInstance("AES").generateKey();

        // encrypt AES key using RSA
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(secretKey.getEncoded());

        // connect to server
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();

        // send length of encrypted key
        outputStream.write(encryptedKey.length >> 8);
        outputStream.write(encryptedKey.length);
        outputStream.flush();

        // send encrypted symmetric key
        outputStream.write(encryptedKey);
        outputStream.flush();

        // -- handshake complete --

        // encrypt the data using AES
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedData = aesCipher.doFinal(DATA.getBytes());

        // Send the encrypted data
        outputStream.write(encryptedData);
        outputStream.flush();

        socket.close();
    }
}