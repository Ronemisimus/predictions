package main.history;

import com.google.gson.Gson;
import dto.RunHistoryDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "GetRunHistory", urlPatterns = {"/getRunHistory", "/admin/getRunHistory"})
public class GetRunHistory extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else if (getServletContext().getAttribute("adminSession") != null &&
                getServletContext().getAttribute("adminSession").equals(session)) {
            RunHistoryDto runHistoryDto = EngineApi.getInstance().getRunHistory();
            Gson gson = new Gson();
            resp.getWriter().print(gson.toJson(runHistoryDto));
        }
        else {
            String username = req.getParameter("username");
            if (UserManager.getInstance().userExists(username) &&
            UserManager.getInstance().sameUser(username, session)) {
                RunHistoryDto runHistoryDto = EngineApi.getInstance().getRunHistoryPerUser(username);
                Gson gson = new Gson();
                resp.getWriter().print(gson.toJson(runHistoryDto));
            }
            else {
                resp.setStatus(401);
                resp.getWriter().println("unauthorized");
            }
        }
    }
}
