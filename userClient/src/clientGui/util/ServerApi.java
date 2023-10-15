package clientGui.util;

import clientGui.execution.environment.EntityAmountGetter;
import clientGui.execution.environment.EnvironmentVariableGetter;
import clientGui.scene.details.ComparableDeserializer;
import clientGui.scene.details.tree.WorldDetailsItem;
import clientGui.scene.newExecution.RequestSelection;
import clientGui.scene.requests.RequestsDetailsRow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ShowWorldDto;
import dto.subdto.requests.RequestDetailsDto;
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
import java.util.stream.Collectors;

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

    private void Alert(String title, String headerText, String contentText, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.setAlertType(type);

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

    public String tryLogin() {
        String[] result = {null};

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Username Input");
        dialog.setHeaderText(null);
        dialog.setContentText("Enter your username:");
        dialog.showAndWait().ifPresent(username -> {
            this.username = Username.wrap(username);

            //noinspection KotlinInternalInJava
            Call call = client.newCall(new Request.Builder()
                    .url(HttpUrl.get(HOST).newBuilder()
                            .addQueryParameter("username", this.username)
                            .build())
                    .build());

            try (Response response = call.execute()) {
                if (response.isSuccessful()) {
                    cookieManager.loadForRequest(call.request().url()).forEach(cookie -> {
                        if (cookie.name().equals("JSESSIONID")) {
                            result[0] = this.username;
                        }
                    });
                }
                if (result[0] == null) {
                    String reason = response.body() != null ? response.body().string() : "unknown";
                    Alert("Error", "Cannot login","Reason: " + reason, Alert.AlertType.ERROR);
                }
            } catch (IOException e) {
                Alert("Connection Error", "Cannot login","Reason: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace(System.err);
            }
        });

        return result[0];
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
            Alert("Error", "Cannot show world", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
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
            Alert("Error", "Cannot get loaded worlds", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return null;
    }

    public void submitRequest(String worldName, Integer runAmount, Boolean userTermination, Integer ticksTermination, Integer secondsTermination) {
        if (runAmount == null || runAmount <= 0) {
            Alert("Invalid Run Amount", "Run Amount must be greater than 0", "Please select a valid run amount", Alert.AlertType.ERROR);
            return;
        }
        if (secondsTermination != null && secondsTermination < 0) {
            Alert("Invalid Seconds Termination", "Seconds Termination must be greater than or equal to 0",
                    "Please select a valid seconds termination", Alert.AlertType.ERROR);
            return;
        }
        if (ticksTermination != null && ticksTermination < 0) {
            Alert("Invalid Ticks Termination", "Ticks Termination must be greater than or equal to 0",
                    "Please select a valid ticks termination", Alert.AlertType.ERROR);
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
                Alert("Success", "Request Submitted", "Your request has been submitted", Alert.AlertType.INFORMATION);
            }
        } catch (IOException e) {
            Alert("Error", "Cannot submit request", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public List<RequestsDetailsRow> getRequestsRows() {
        return getRequests().stream()
                .map(RequestsDetailsRow::new)
                .collect(Collectors.toList());
    }

    private List<RequestDetailsDto> getRequests() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("requests")
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = new Gson();
            if (response.body() != null) {
                String body = response.body().string();

                java.lang.reflect.Type listType = new com.google.gson.reflect.TypeToken<ArrayList<RequestDetailsDto>>(){}.getType();

                return gson.fromJson(body, listType);
            }
        } catch (Exception e) {
            Alert("Error", "Cannot get requests", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return new ArrayList<>();
    }

    public List<EntityAmountGetter> getEntityAmounts() {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public List<EnvironmentVariableGetter> getEnvironmentVariables() {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public void runSimulation() {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public void setEntityAmount(String name, int amount) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public void setEnvironmentVariable(String name, String value) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    public List<RequestSelection> getApprovedOpenRequests() {
        return getRequests().stream()
                .filter(req->req.getStatus().equals("APPROVED_OPEN"))
                .map(RequestSelection::new)
                .collect(Collectors.toList());
    }

    public void stopRequest(int requestId) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("cancelRequest")
                        .addQueryParameter("requestId", String.valueOf(requestId))
                        .addQueryParameter("username", username)
                        .build())
                .build());
        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                Alert("Success",
                        "Request Cancelled",
                        "Your request has been cancelled", Alert.AlertType.INFORMATION);
            }else{
                Alert("Error", "Cannot cancel request",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            Alert("Error", "Cannot cancel request",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void addRequest(int requestId) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("addRequest")
                        .addQueryParameter("requestId", String.valueOf(requestId))
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (response.isSuccessful()) {
                Alert("Success",
                        "Request Added",
                        "Your request has been added", Alert.AlertType.INFORMATION);
            }else{
                Alert("Error", "Cannot add request",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot add request",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }
}
