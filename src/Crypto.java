import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
    private static PrivateKey privateKey;
    private static SecretKey secretKey;

    public static void init() {
        // load private key from base64 encoded file

        // key path
        Path path = Path.of("private.key");

        // exists?
        if (!Files.exists(path)) {
            System.err.println("Could not find private key file private.key");
            System.exit(1);
        }

        try {
            // read key
            String pk64 = Files.readString(path);

            // decode key
            byte[] privateKeyBytes = Base64.getDecoder().decode(pk64);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static boolean handshake(InputStream inputStream, OutputStream outputStream) {
        try {
            // read length of encrypted key
            int encryptedKeyLength = (inputStream.read() << 8) | inputStream.read();

            // receive encrypted symmetric key
            byte[] encryptedKey = new byte[encryptedKeyLength];
            inputStream.read(encryptedKey);

            // decrypt symmetric key using RSA
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedKey = rsaCipher.doFinal(encryptedKey);
            secretKey = new SecretKeySpec(decryptedKey, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static byte[] decrypt(byte[] encrypted, int length) {
        try {
            // decrypt data using AES
            Cipher aesCipher = Cipher.getInstance("AES");
            aesCipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedData = aesCipher.doFinal(encrypted, 0, length);
            return decryptedData;
        } catch (Exception e) {
            return null;
        }
    }
}
