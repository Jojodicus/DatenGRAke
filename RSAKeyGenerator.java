import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class RSAKeyGenerator {
    public static void main(String[] args) throws Exception {
        // generate RSA key pair
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048); // Key size of 2048 bits
        KeyPair keyPair = keyGen.generateKeyPair();

        // retrieve public and private keys
        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

        // print keys
        System.out.println("Public Key:");
        System.out.println(publicKey);
        System.out.println();

        System.out.println("Private Key:");
        System.out.println(privateKey);

        // save to file
        Files.writeString(Path.of("public.key"), publicKey);
        Files.writeString(Path.of("private.key"), privateKey);
    }
}
