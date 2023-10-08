package gui.util;

import com.google.gson.Gson;
import dto.ReadFileDto;
import gui.readFileError.ReadFileError;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import okhttp3.*;

import java.io.*;
import java.util.Objects;

public class ServerApi {
    public static volatile ServerApi instance = null;

    private final OkHttpClient client;

    private final SimpleCookieManager cookieManager;
    private final static String HOST = "http://localhost:8080/server_Web_exploded/admin";
    private ServerApi() {
        cookieManager = new SimpleCookieManager();
        //noinspection KotlinInternalInJava
        client = new OkHttpClient.Builder().cookieJar(cookieManager).build();
    }

    public static ServerApi getInstance() {
        if (instance == null) {
            synchronized (ServerApi.class) {
                if (instance == null)
                    instance = new ServerApi();
            }
        }
        return instance;
    }

    private void Alert(String title, String headerText, String contentText) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);

            // Calculate preferred width based on content
            Text text = new Text(contentText);
            double contentWidth = text.getLayoutBounds().getWidth();
            double contentHeight = text.getLayoutBounds().getHeight();
            alert.getDialogPane().setMinWidth(contentWidth + 60); // Adjust padding and margins
            alert.setWidth(contentWidth + 60);
            alert.setHeight(contentHeight + 60);
            alert.setResizable(true);

            alert.showAndWait();
        });

    }

    public String LoadFile(String filePath) {
        // get file string content
        File file = new File(filePath);
        if (!file.exists()) {
            Alert("Invalid File","File does not exist","Please select a valid file");
            return null;
        }
        if (!file.isAbsolute()) {
            Alert("Invalid File","File is not an absolute path","Please select a valid file");
            return null;
        }
        if (!file.isFile()) {
            Alert("Invalid File","File is not a file","Please select a valid file");
            return null;
        }
        if (!file.canRead()) {
            Alert("Invalid File","File cannot be read","Please select a valid file");
            return null;
        }

        // send file content to server at localhost:8080/server/readFile using post request with okhttp client
        Call call = client.newCall(new okhttp3.Request.Builder()
                // xml media type
                .post(FormBody.create(file, MediaType.parse("application/xml")))
                .url(HOST + "/readFile").build());

        String result;

        try (Response response = call.execute()) {
            Gson gson = new Gson();
            ReadFileDto readFileDto;
            if (response.body() != null) {
                readFileDto = gson.fromJson(response.body().string(), ReadFileDto.class);
            } else {
                readFileDto = null;
            }
            result = readFileDto != null && readFileDto.isFileLoaded()? readFileDto.getName(): null;
            if (result==null){
                Platform.runLater(() -> {
                    ReadFileError readFileError = ReadFileError.build(readFileDto);
                    readFileError.show();
                });
            }
        } catch (IOException e) {
            Alert("Error", "Cannot use file", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
        return result;
    }

    public void SetThreadCount(int enteredInteger) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(new HttpUrl.Builder()
                        .host(HOST)
                        .addPathSegment("admin")
                        .addPathSegment("setThreadCount")
                        .addQueryParameter("threadCount", String.valueOf(enteredInteger))
                        .build())
                .build());
        try (Response response = call.execute()) {
            if (response.isSuccessful())
            {
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Thread Count Set to " + enteredInteger);
                success.showAndWait();
            }
            else{
                Alert("Error", "can't send thread count", "thread count: " + enteredInteger);
            }
        } catch (IOException e) {
            Alert("Error", "can't send thread count", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public boolean tryLogin() {
        byte[] publicKey;
        try {
            publicKey = loadPublicKeyFromFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(HOST + "/")
                .post(FormBody.create(publicKey)).build());

        boolean result = false;

        try (Response response = call.execute()) {
            for (Cookie cookie : cookieManager.loadForRequest(response.request().url())) {
                if (cookie.name().equals("JSESSIONID")) {
                    result = true;
                    break;
                }
            }
            if (!result)
                Alert("Error",
                        "Cannot authenticate as admin",
                        "reason: " + "no session found - an admin already logged in");
        } catch (IOException e) {
            Alert("Error", "Cannot authenticate as admin", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
            return false;
        }

        return result;
    }

    private static byte[] loadPublicKeyFromFile() throws Exception {
        FileInputStream fis = new FileInputStream(Objects.requireNonNull(ServerApi.class.getResource("publicKey.pub")).getFile());
        byte[] publicKeyBytes = new byte[fis.available()];
        //noinspection ResultOfMethodCallIgnored
        fis.read(publicKeyBytes);
        fis.close();

        return publicKeyBytes;
    }
}
