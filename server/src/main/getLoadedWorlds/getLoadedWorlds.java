package main.getLoadedWorlds;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.util.EngineApi;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "getLoadedWorlds", urlPatterns = "/getLoadedWorlds")
public class getLoadedWorlds extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            List<String> loadedWorlds = EngineApi.getInstance().getLoadedWorlds();
            Gson gson = new Gson();
            resp.getWriter().print(gson.toJson(loadedWorlds));
        }
    }
}
