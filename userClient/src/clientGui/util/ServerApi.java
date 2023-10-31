package clientGui.util;

import clientGui.execution.environment.EntityAmountGetter;
import clientGui.execution.environment.EnvironmentVariableGetter;
import clientGui.history.data.PropertyData;
import clientGui.history.data.RunState;
import clientGui.history.display.RunDisplayed;
import clientGui.scene.details.ComparableDeserializer;
import clientGui.scene.details.tree.WorldDetailsItem;
import clientGui.scene.newExecution.RequestSelection;
import clientGui.scene.requests.RequestsDetailsRow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import dto.EnvDto;
import dto.RunHistoryDto;
import dto.ShowWorldDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.requests.RequestDetailsDto;
import dto.subdto.requests.RequestEntryDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.interactive.RunProgressDto;
import dto.subdto.show.world.EntityDto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                Type type = new TypeToken<List<String>>(){}.getType();
                return gson.fromJson(response.body().string(), type);
            }
        }catch (IOException | JsonSyntaxException e) {
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
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getEntityAmounts")
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comparable.class, new ComparableDeserializer())
                    .create();
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                Type type = new TypeToken<List<EntityDto>>(){}.getType();
                List<EntityDto> entities = gson.fromJson(body, type);
                return entities.stream()
                        .map(EntityAmountGetter::new)
                        .collect(Collectors.toList());
            }
            else {
                Alert("Error", "Cannot get entity amounts",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            }
        }
        catch (IOException | JsonSyntaxException e) {
            Alert("Error", "Cannot get entity amounts",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return new ArrayList<>();
    }

    public List<EnvironmentVariableGetter> getEnvironmentVariables() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getEnvironmentVariables")
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comparable.class, new ComparableDeserializer())
                    .create();
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                Type type = new TypeToken<EnvDto>(){}.getType();
                EnvDto env = gson.fromJson(body, type);
                return env.getEnvironment().stream()
                        .map(EnvironmentVariableGetter::new)
                        .collect(Collectors.toList());
            }
            else {
                Alert("Error", "Cannot get environment variables",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            }
        }
        catch (IOException | JsonSyntaxException e) {
            Alert("Error", "Cannot get environment variables",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return new ArrayList<>();
    }

    public void runSimulation() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("runSimulation")
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot run simulation",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot run simulation");
            }
        }catch (IOException e) {
            Alert("Error", "Cannot run simulation",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void setEntityAmount(String name, int amount) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("setEntityAmount")
                        .addQueryParameter("username", username)
                        .addQueryParameter("name", name)
                        .addQueryParameter("amount", String.valueOf(amount))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot set entity amount",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot set entity amount");
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot set entity amount",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    public void setEnvironmentVariable(String name, String value) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("setEnvironmentVariable")
                        .addQueryParameter("username", username)
                        .addQueryParameter("name", name)
                        .addQueryParameter("value", value)
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot set environment variable",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot set environment variable");
            }
        } catch (IOException e) {
            Alert("Error", "Cannot set environment variable",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public List<RequestSelection> getApprovedOpenRequests() {
        return getRequests().stream()
                .filter(req->req.getStatus().equals("APPROVED_OPEN"))
                .map(RequestSelection::new)
                .collect(Collectors.toList());
    }

    public void clearSimulation(int requestId) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("clearSimulation")
                        .addQueryParameter("requestId", String.valueOf(requestId))
                        .addQueryParameter("username", username)
                        .build())
                .build());
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot cancel request",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            Alert("Error", "Cannot cancel request",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void setSimulation(int requestId) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("setSimulation")
                        .addQueryParameter("requestId", String.valueOf(requestId))
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
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

    public List<RunDisplayed> getRunHistory() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getRunHistory")
                        .addQueryParameter("username", username)
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot get run history",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot get run history");
            }
            Gson gson = new Gson();
            RunHistoryDto runHistory = response.body() != null ? gson.fromJson(response.body().string(), RunHistoryDto.class): null;

            return runHistory != null ? runHistory.getRunStates().keySet().stream()
                    .map(runId -> new RunDisplayed(
                            new AbstractMap.SimpleEntry<>(runId, runHistory.getRunList().get(runId)),
                            runHistory.getRunStates().get(runId)))
                    .collect(Collectors.toList()) : new ArrayList<>();
        }catch (IOException e) {
            Alert("Error", "Cannot get run history",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }

    public Map<String, Map<Integer, Integer>> getSingleRunHistoryEntityAmount(Integer runIdentifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getSingleRunHistoryEntityAmount")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(runIdentifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = checkSingleRunHistory(response);
            SingleRunHistoryDto singleRunHistory;
            if (response.body() != null) {
                singleRunHistory = gson.fromJson(response.body().string(), SingleRunHistoryDto.class);
            } else {
                singleRunHistory = null;
            }
            return singleRunHistory==null ? new HashMap<>() : IntStream.range(0, singleRunHistory.getEntity().size()).boxed()
                    .collect(Collectors.toMap(
                       i -> singleRunHistory.getEntity().get(i),
                       i -> singleRunHistory.getCounts().get(i)
                    ));
        } catch (IOException e) {
            Alert("Error", "Cannot get run history",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return new HashMap<>();
        }
    }

    private Gson checkSingleRunHistory(Response response) throws IOException {
        if (!response.isSuccessful()) {
            Alert("Error", "Cannot get run history",
                    "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
            throw new RuntimeException("Cannot get run history");
        }
        return new GsonBuilder().registerTypeAdapter(Comparable.class, new ComparableDeserializer()).create();
    }

    public Map<String, Map<String, PropertyData>> getSingleRunHistoryPropertyData(Integer runIdentifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getEntityList")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(runIdentifier))
                        .build())
                .build());

        EntityListDto entityList;
        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot get Entity List",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot get run history");
            }
            Gson gson = new GsonBuilder().registerTypeAdapter(Comparable.class, new ComparableDeserializer()).create();
            if (response.body() != null) {
                entityList = gson.fromJson(response.body().string(), EntityListDto.class);
            } else {
                entityList = null;
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot get Entity List",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return new HashMap<>();
        }

        return entityList == null ? new HashMap<>() : entityList.getEntities().stream()
                .map(entity -> new AbstractMap.SimpleEntry<>(entity.getName(), entity.getProps()))
                .map(entry -> new AbstractMap.SimpleEntry<>(
                        entry.getKey(),
                        entry.getValue().stream()
                                .map(prop -> new AbstractMap.SimpleEntry<>(
                                        prop.getName(),
                                        new PropertyData(
                                                entry.getKey(),
                                                prop,
                                                getSingleRunPropertyData(
                                                        runIdentifier,
                                                        entry.getKey(),
                                                        prop.getName()
                                                )
                                        )
                                )).collect(Collectors.toMap(
                                        AbstractMap.SimpleEntry::getKey,
                                        AbstractMap.SimpleEntry::getValue
                                ))
                ))
                .collect(Collectors.toMap(
                        AbstractMap.SimpleEntry::getKey,
                        AbstractMap.SimpleEntry::getValue
                ));
    }

    private SingleRunHistoryDto getSingleRunPropertyData(Integer runIdentifier, String entity, String prop) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getSingleRunPropertyData")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(runIdentifier))
                        .addQueryParameter("entity", entity)
                        .addQueryParameter("prop", prop)
                        .build())
                .build());

        try (Response response = call.execute()) {
            Gson gson = checkSingleRunHistory(response);
            if (response.body() != null) {
                return gson.fromJson(response.body().string(), SingleRunHistoryDto.class);
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot get run history",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return new SingleRunHistoryDto(null, null, 0, new HashMap<>(), 0.0, 0.0);
    }

    public RunProgressDto getRunProgress(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getRunProgress")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot get run history",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot get run history");
            }
            Gson gson = new Gson();
            if (response.body() != null) {
                return gson.fromJson(response.body().string(), RunProgressDto.class);
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot get run history",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
        return new RunProgressDto(null, 0, Duration.ZERO, Duration.ZERO, RunState.STOPPED.name());
    }

    public boolean stopSimulation(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("stopSimulation")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot stop simulation",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot stop simulation");
            }
            Gson gson = new Gson();
            if (response.body() != null) {
                return gson.fromJson(response.body().string(), Boolean.class);
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot stop simulation",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return false;
        }
        return false;
    }

    public void pauseSimulation(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("pauseSimulation")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot pause simulation",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot pause simulation");
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot pause simulation",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void resumeSimulation(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("resumeSimulation")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot resume simulation",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot resume simulation");
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot resume simulation",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void reRunSimulation(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("reRunSimulation")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot re-run simulation",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot re-run simulation");
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot re-run simulation",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public void copyEnvironment(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("copyEnvironment")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot copy environment",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot copy environment");
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot copy environment",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
        }
    }

    public List<EntityDto> getCurrentEntityAmounts(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getCurrentEntityAmounts")
                        .addQueryParameter("username", username)
                        .addQueryParameter("runIdentifier", String.valueOf(identifier))
                        .build())
                .build());

        try (Response response = call.execute()) {
            if (!response.isSuccessful()) {
                Alert("Error", "Cannot get current entity amounts",
                        "reason: " + (response.body() != null ? response.body().string() : "unknown"), Alert.AlertType.ERROR);
                throw new RuntimeException("Cannot get current entity amounts");
            }
            Gson gson = new GsonBuilder().registerTypeAdapter(Comparable.class, new ComparableDeserializer()).create();
            if (response.body() != null) {
                return gson.fromJson(response.body().string(), EntityListDto.class).getEntities();
            }
        }
        catch (IOException e) {
            Alert("Error", "Cannot get current entity amounts",
                    "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            throw new RuntimeException("Cannot get current entity amounts");
        }
        return new ArrayList<>();
    }
}
