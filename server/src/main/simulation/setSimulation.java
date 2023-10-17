package main.simulation;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.requests.request.Request;
import main.requests.request.RequestStatus;
import main.requests.requestManager.RequestManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "setSimulation", urlPatterns = {"/setSimulation"})
public class setSimulation extends HttpServlet {
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
            if (requestId==null){
                resp.setStatus(400);
                resp.getWriter().println("missing requestId");
                return;
            }
            if (!requestId.matches("\\d+")) {
                resp.setStatus(400);
                resp.getWriter().println("requestId invalid");
                return;
            }
            int requestIdInt = Integer.parseInt(requestId);
            if (RequestManager.getInstance().getRequest(requestIdInt) == null){
                resp.setStatus(404);
                resp.getWriter().println("request not found");
                return;
            }
            Request request = RequestManager.getInstance().getRequest(requestIdInt);
            if (username == null){
                resp.setStatus(400);
                resp.getWriter().println("missing username");
                return;
            }
            if (!request.requestingUser().equals(username)){
                resp.setStatus(401);
                resp.getWriter().println("unauthorized for selected request");
                return;
            }
            if (!request.getStatus().equals(RequestStatus.APPROVED_OPEN)){
                resp.setStatus(400);
                resp.getWriter().println("request not available to run");
                return;
            }
            if (!UserManager.getInstance().sameUser(username, session)){
                resp.setStatus(401);
                resp.getWriter().println("unauthorized for claimed user");
                return;
            }
            EngineApi.getInstance().setSimulation(request.requestingUser(), request.getWorldName());
            UserRequestManager.getInstance().addRequest(username, requestIdInt);
            resp.setStatus(200);
        }
    }
}
