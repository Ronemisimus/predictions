package clientGui.util;

import clientGui.scene.details.tree.WorldDetailsItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ShowWorldDto;
import clientGui.scene.details.ComparableDeserializer;
import dto.subdto.requests.RequestEntryDto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    public List<String> getLoadedWorlds() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getLoadedWorlds")
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = new Gson();
            if (response.body() != null) {
                //noinspection unchecked
                return (List<String>)gson.fromJson(response.body().string(), ArrayList.class);
            }
        }catch (IOException e) {
            Alert("Error", "Cannot get loaded worlds", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void submitRequest(String worldName, Integer runAmount, Boolean userTermination, Integer ticksTermination, Integer secondsTermination) {
        if (runAmount == null || runAmount <= 0) {
            Alert("Invalid Run Amount", "Run Amount must be greater than 0", "Please select a valid run amount");
            return;
        }
        if (secondsTermination != null && secondsTermination < 0) {
            Alert("Invalid Seconds Termination", "Seconds Termination must be greater than or equal to 0", "Please select a valid seconds termination");
            return;
        }
        if (ticksTermination != null && ticksTermination < 0) {
            Alert("Invalid Ticks Termination", "Ticks Termination must be greater than or equal to 0", "Please select a valid ticks termination");
            return;
        }

        RequestEntryDto requestEntryDto = new RequestEntryDto(username,
                worldName,
                runAmount,
                ticksTermination,
                secondsTermination,
                userTermination);

        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("requests")
                        .build())
                .post(RequestBody.create(new Gson().toJson(requestEntryDto), MediaType.parse("application/json")))
                .build());

        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                Alert("Success", "Request Submitted", "Request successfully submitted");
            }
        } catch (IOException e) {
            Alert("Error", "Cannot submit request", "reason: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
