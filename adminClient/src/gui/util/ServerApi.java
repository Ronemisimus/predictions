package gui.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.ReadFileDto;
import dto.RunHistoryDto;
import dto.ShowWorldDto;
import dto.subdto.SingleRunHistoryDto;
import dto.subdto.requests.RequestDetailsDto;
import dto.subdto.show.EntityListDto;
import dto.subdto.show.instance.RunStateDto;
import dto.subdto.show.interactive.RunProgressDto;
import dto.subdto.show.world.EntityDto;
import gui.history.data.PropertyData;
import gui.history.data.RunState;
import gui.history.display.RunDisplayed;
import gui.readFileError.ReadFileError;
import gui.scene.allocations.RequestDetailsRow;
import gui.scene.management.ComparableDeserializer;
import gui.scene.management.RunStateRow;
import gui.scene.management.tree.WorldDetailsItem;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import okhttp3.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    private void Alert(String title, String headerText, String contentText, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
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
            Alert("Invalid File","File does not exist","Please select a valid file", Alert.AlertType.ERROR);
            return null;
        }
        if (!file.isAbsolute()) {
            Alert("Invalid File","File is not an absolute path","Please select a valid file", Alert.AlertType.ERROR);
            return null;
        }
        if (!file.isFile()) {
            Alert("Invalid File","File is not a file","Please select a valid file", Alert.AlertType.ERROR);
            return null;
        }
        if (!file.canRead()) {
            Alert("Invalid File","File cannot be read","Please select a valid file", Alert.AlertType.ERROR);
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
            Alert("Error", "Cannot use file", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return null;
        }
        return result;
    }

    public void SetThreadCount(int enteredInteger) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("setThreadCount")
                        .addQueryParameter("threadCount", String.valueOf(enteredInteger))
                        .build())
                .build());
        try (Response response = call.execute()) {
            if (response.isSuccessful())
            {
                Alert("Success", "set thread count", "Thread Count Set to " + enteredInteger, Alert.AlertType.INFORMATION);

            }
            else{
                Alert("Error", "can't send thread count", "thread count: " + enteredInteger, Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            Alert("Error", "can't send thread count", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
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
                        "reason: " + "no session found - an admin already logged in", Alert.AlertType.ERROR);
        } catch (IOException e) {
            Alert("Error", "Cannot authenticate as admin", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
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

    public TreeItem<String> showLoadedWorld(String name) {
        // create call to show world dto
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new okhttp3.Request.Builder()
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

    public List<RunStateRow> getRunStates() {
        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(HOST + "/getRunStates").build());

        try (Response response = call.execute()) {
            RunHistoryDto res = null;
            if (response.body() != null) {
                res = new Gson().fromJson(response.body().string(), RunHistoryDto.class);
            }
            List<RunStateDto> states;
            if (res != null) {
                states = new ArrayList<>(res.getRunStates().values());
            }
            else{
                states = new ArrayList<>();
            }
            return Arrays.stream(RunState.values())
                    .map(run -> new RunStateRow(run.name(), (int) states.stream()
                            .filter(s -> RunState.getRunState(s).equals(run)).count()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            Alert("Error", "Cannot get run states", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
            return new ArrayList<>();
        }
    }

    public List<RequestDetailsRow> getRequests() {
        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(HOST + "/requests").build());

        List<RequestDetailsRow> result = new ArrayList<>();
        try (Response response = call.execute()) {
            if (response.body() != null) {
                java.lang.reflect.Type listType = new TypeToken<ArrayList<RequestDetailsDto>>(){}.getType();
                List<RequestDetailsDto> requestDto = new Gson().fromJson(response.body().string(), listType);
                result.addAll(requestDto.stream()
                        .map(RequestDetailsRow::new)
                        .collect(Collectors.toList()));
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        return result;
    }

    public void setRequestStatus(int requestId, String status) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new okhttp3.Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("requests")
                        .addQueryParameter("requestId", String.valueOf(requestId))
                        .addQueryParameter("status", status)
                        .build())
                .post(new FormBody.Builder().build())
                .build());

        try (Response response = call.execute()) {
            if (response.isSuccessful())
            {
                Alert success = new Alert(Alert.AlertType.INFORMATION, "Request Status Set to " + status);
                success.showAndWait();
            }
        } catch (IOException e) {
            Alert("Error", "Cannot set request status", "reason: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace(System.err);
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

    public List<RunDisplayed> getRunHistory() {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getRunHistory")
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

    private SingleRunHistoryDto getSingleRunPropertyData(Integer runIdentifier, String entity, String prop) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getSingleRunPropertyData")
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

    public List<EntityDto> getCurrentEntityAmounts(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("getCurrentEntityAmounts")
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

    public boolean stopSimulation(Integer identifier) {
        //noinspection KotlinInternalInJava
        Call call = client.newCall(new Request.Builder()
                .url(HttpUrl.get(HOST).newBuilder()
                        .addPathSegment("stopSimulation")
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
}
