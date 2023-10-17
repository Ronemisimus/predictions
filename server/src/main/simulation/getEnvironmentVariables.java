package main.simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.showWorld.ComparableSerializer;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "getEnvironmentVariables", urlPatterns = {"/getEnvironmentVariables"})
public class getEnvironmentVariables extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            String username = req.getParameter("username");
            if (!UserApproval.approveUser(username,session,req,resp)){
                return;
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comparable.class, new ComparableSerializer())
                    .create();
            resp.getWriter().print(gson.toJson(EngineApi.getInstance().getEnv(username)));
            resp.setStatus(200);
        }
    }
}
