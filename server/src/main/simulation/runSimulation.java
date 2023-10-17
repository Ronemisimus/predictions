package main.simulation;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "runSimulation", urlPatterns = {"/runSimulation"})
public class runSimulation extends HttpServlet {
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
            EngineApi.getInstance().runSimulation(username);
            resp.setStatus(200);
        }
    }
}
