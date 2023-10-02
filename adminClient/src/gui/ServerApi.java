package gui;

import com.google.gson.Gson;
import dto.ReadFileDto;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.stream.Collectors;

public class ServerApi {
    public static volatile ServerApi instance = null;

    private final OkHttpClient client;
    private final static String HOST = "http://localhost:8080/server";
    private ServerApi() {
        client = new OkHttpClient();
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
            alert.showAndWait();
        });
    }

    public boolean LoadFile(String filePath) {
        // get file string content
        File file = new File(filePath);
        if (!file.exists()) {
            Alert("Invalid File","File does not exist","Please select a valid file");
            return false;
        }
        if (!file.isAbsolute()) {
            Alert("Invalid File","File is not an absolute path","Please select a valid file");
            return false;
        }
        if (!file.isFile()) {
            Alert("Invalid File","File is not a file","Please select a valid file");
            return false;
        }
        if (!file.canRead()) {
            Alert("Invalid File","File cannot be read","Please select a valid file");
            return false;
        }

        // read file content to string
        String fileContent = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            fileContent = reader.lines().collect(Collectors.joining("\n"));
        } catch (FileNotFoundException ignored) {
        }

        // validate file content
        if (fileContent.isEmpty()) {
            Alert("Invalid File","File is empty","Please select a valid file");
            return false;
        }

        // send file content to server at localhost:8080/server/readFile using post request with okhttp client

        Call call = client.newCall(
                new okhttp3.Request.Builder()
                        .url(HOST+"/readFile")
                        .post(okhttp3.FormBody.create(
                                "fileContent",
                                MediaType.parse(fileContent))).build());

        final boolean[] result = {false};
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Alert("unsuccessful","File upload failed","response code: " + response.code());
                }
                Gson gson = new Gson();
                if (response.body() != null) {
                    ReadFileDto readFileDto = gson.fromJson(response.body().string(), ReadFileDto.class);
                    result[0] = readFileDto.isFileLoaded();
                    //noinspection StatementWithEmptyBody
                    if (!readFileDto.isFileLoaded())
                    {
                        // TODO: handle errors
                    }
                }
                else{
                    Alert("unsuccessful","File upload failed","response body is null");
                }
            }

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Alert("unsuccessful","File upload failed","failed to reach server: " + e.getMessage());
                result[0] = false;
                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (call) {
                    call.notifyAll();
                }
            }
        });

        try {
            synchronized (call) {
                call.wait();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result[0];
    }

    public void SetThreadCount(int enteredInteger) {

    }
}
