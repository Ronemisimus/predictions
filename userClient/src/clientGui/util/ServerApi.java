package clientGui.util;

import clientGui.scene.details.tree.WorldDetailsItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ShowWorldDto;
import clientGui.scene.details.ComparableDeserializer;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ServerApi {
    public static volatile ServerApi instance = null;

    private final OkHttpClient client;

    private final SimpleCookieManager cookieManager;
    private final static String HOST = "http://localhost:8080/server_Web_exploded";

    private String username;
    private ServerApi() {
        cookieManager = new SimpleCookieManager();
        //noinspection KotlinInternalInJava
        client = new OkHttpClient.Builder().cookieJar(cookieManager)
                .callTimeout(15, TimeUnit.SECONDS).build();
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

    public boolean tryLogin() {
        AtomicBoolean result = new AtomicBoolean(false);

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username Input");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your username:");
        dialog.showAndWait().ifPresent(username -> {
            this.username = username;
            String storageUsername = Username.wrap(username);

            //noinspection KotlinInternalInJava
            Call call = client.newCall(new Request.Builder()
                    .url(HttpUrl.get(HOST).newBuilder()
                            .addQueryParameter("username", storageUsername)
                            .build())
                    .build());

            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    cookieManager.loadForRequest(call.request().url()).forEach(cookie -> {
                        if (cookie.name().equals("JSESSIONID")) {
                            result.set(true);
                        }
                    });
                }
                if (!result.get()) {
                    String reason = response.body() != null ? response.body().string() : "unknown";
                    Alert("Error", "Cannot login","Reason: " + reason);
                }
            } catch (IOException e) {
                Alert("Connection Error", "Cannot login","Reason: " + e.getMessage());
            }
        });

        return result.get();
    }

    public TreeItem<String> showLoadedWorld(String name) {
        // create call to show world dto
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("showWorld")
                        .addQueryParameter("username", Username.wrap(username))
                        .addQueryParameter("worldName", name)
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comparable.class, new ComparableDeserializer())
                    .create();
            ShowWorldDto showWorldDto;
            if (response.body() != null) {
                showWorldDto = gson.fromJson(response.body().string(), ShowWorldDto.class);
                return new WorldDetailsItem(showWorldDto);
            }
            return null;
        } catch (IOException e) {
            Alert("Error", "Cannot show world", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
            return null;
        }
    }
}
