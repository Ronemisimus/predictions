package main.login;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ClientLogin", urlPatterns = {"/"})
public class ClientLogin extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(true);
        String username = req.getParameter("username");
        if (UserManager.getInstance().userExists(username) && !UserManager.getInstance().sameUser(username, session)) {
            resp.setStatus(401);
            session.invalidate();
            resp.getWriter().println("username taken");
        } else if (UserManager.getInstance().userExists(username)) {
            resp.setStatus(200);
            resp.getWriter().println("already logged in");
        }
        else {
            UserManager.getInstance().addUser(username, session);
            resp.setStatus(200);
            resp.getWriter().println("logged in");
        }
    }
}
