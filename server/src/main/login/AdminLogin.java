package main.login;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="AdminLogin", urlPatterns={"/admin/"})
public class AdminLogin extends HttpServlet {

    private Boolean adminLoggedIn = false;
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int contentLength = req.getContentLength();
        byte[] publicKey = new byte[contentLength];
        //noinspection ResultOfMethodCallIgnored
        req.getInputStream().read(publicKey);
        byte[] privateKey;
        try (FileInputStream privateKeyFile = new FileInputStream(Objects.requireNonNull(AdminLogin.class.getResource("privateKey.pem")).getPath())) {
            privateKey = new byte[privateKeyFile.available()];
            //noinspection ResultOfMethodCallIgnored
            privateKeyFile.read(privateKey);
        }catch (IOException e){
            throw new RuntimeException(e);
        }

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKey);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKey);
        try {
            KeyFactory rsaFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKeyObject = rsaFactory.generatePrivate(privateKeySpec);
            PublicKey publicKeyObject = rsaFactory.generatePublic(publicKeySpec);

            if(verify(privateKeyObject, publicKeyObject)){
                //noinspection SynchronizeOnNonFinalField
                synchronized (adminLoggedIn) {
                    if (!adminLoggedIn) {
                        HttpSession session = req.getSession(true);
                        session.setAttribute("type", "admin");
                        resp.getWriter().println("admin");
                        adminLoggedIn = true;
                        getServletContext().setAttribute("adminSession", session);
                    }
                    else {
                        Optional.ofNullable(req.getSession(false)).ifPresent(HttpSession::invalidate);
                        resp.getWriter().println("invalid");
                    }
                }
            }
            else {
                Optional.ofNullable(req.getSession(false)).ifPresent(HttpSession::invalidate);
                resp.getWriter().println("invalid");
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException |
                 NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
            Logger.getAnonymousLogger().log(Level.WARNING,e.getMessage());
        }
    }

    private boolean verify(PrivateKey privateKey, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        // Original data to be signed
        String originalMessage = "Hello, World!";
        byte[] encryptedMessage = encrypt(publicKey, originalMessage);
        String decryptedMessage = decrypt(privateKey, encryptedMessage);

        return originalMessage.equals(decryptedMessage);
    }

    private static byte[] encrypt(PublicKey publicKey, String message) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(message.getBytes());
    }

    // Decrypt a message using a private key
    private static String decrypt(PrivateKey privateKey, byte[] encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
        return new String(decryptedBytes);
    }
}
