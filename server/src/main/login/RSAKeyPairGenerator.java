package main.login;

import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RSAKeyPairGenerator {
    public static final String PUBLIC_KEY = "publicKey.pub";
    public static final String PRIVATE_KEY = "privateKey.pem";
    public static void main(String[] args) {
        try {
            // Generate an RSA key pair
            KeyPair keyPair = generateKeyPair();

            // Save private and public keys to files
            saveKeyToFile(keyPair.getPrivate(), PRIVATE_KEY);
            saveKeyToFile(keyPair.getPublic(), PUBLIC_KEY);

            // Load keys from files
            PrivateKey privateKey = loadPrivateKeyFromFile();
            PublicKey publicKey = loadPublicKeyFromFile();

            // Verify encryption and decryption
            String originalMessage = "Hello, RSA!";
            byte[] encryptedMessage = encrypt(publicKey, originalMessage);
            String decryptedMessage = decrypt(privateKey, encryptedMessage);

            System.out.println("Original Message: " + originalMessage);
            System.out.println("Decrypted Message: " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    // Generate an RSA key pair
    private static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    // Save a key to a file
    private static void saveKeyToFile(Key key, String fileName) throws IOException {
        byte[] keyBytes = key.getEncoded();
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(keyBytes);
        fos.close();
    }

    // Load a private key from a file
    private static PrivateKey loadPrivateKeyFromFile() throws Exception {
        FileInputStream fis = new FileInputStream(RSAKeyPairGenerator.PRIVATE_KEY);
        byte[] privateKeyBytes = new byte[fis.available()];
        //noinspection ResultOfMethodCallIgnored
        fis.read(privateKeyBytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(keySpec);
    }

    // Load a public key from a file
    private static PublicKey loadPublicKeyFromFile() throws Exception {
        FileInputStream fis = new FileInputStream(RSAKeyPairGenerator.PUBLIC_KEY);
        byte[] publicKeyBytes = new byte[fis.available()];
        //noinspection ResultOfMethodCallIgnored
        fis.read(publicKeyBytes);
        fis.close();

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    // Encrypt a message using a public key
    private static byte[] encrypt(PublicKey publicKey, String message) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    // Decrypt a message using a private key
    private static String decrypt(PrivateKey privateKey, byte[] encryptedMessage) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes);
    }
}
