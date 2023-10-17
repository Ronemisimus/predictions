package main.simulation;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "clearSimulation", urlPatterns = {"/clearSimulation"})
public class clearSimulation extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else{
            String username = req.getParameter("username");
            String requestId = req.getParameter("requestId");
            if (UserRequestManager.getInstance().getRequestId(username) == null){
                resp.setStatus(404);
                resp.getWriter().println("request not set for user");
                return;
            }
            if (!requestId.matches("\\d+")) {
                resp.setStatus(400);
                resp.getWriter().println("requestId invalid");
                return;
            }
            int requestIdInt = Integer.parseInt(requestId);
            if (UserRequestManager.getInstance().getRequestId(username) != requestIdInt){
                resp.setStatus(401);
                resp.getWriter().println("selected request isn't set");
                return;
            }
            if (!UserManager.getInstance().sameUser(username, session)) {
                resp.setStatus(401);
                resp.getWriter().println("unauthorized");
                return;
            }
            UserRequestManager.getInstance().removeRequest(username);
            EngineApi.getInstance().clearSimulation(username);
            resp.setStatus(200);
        }
    }
}
