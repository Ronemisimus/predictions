package main.history;

import com.google.gson.Gson;

import dto.subdto.show.interactive.RunProgressDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name="getRunProgress", urlPatterns={"/getRunProgress", "/admin/getRunProgress"})
public class getRunProgress extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else if ( (getServletContext().getAttribute("adminSession") != null &&
                getServletContext().getAttribute("adminSession").equals(session)) ||
                (UserManager.getInstance().userExists(req.getParameter("username")) &&
                        UserManager.getInstance().sameUser(req.getParameter("username"), session))){
            if (req.getParameter("runIdentifier") != null && req.getParameter("runIdentifier").matches("-?\\d+")) {
                RunProgressDto runProgressDto = EngineApi.getInstance().getRunProgress(Integer.parseInt(req.getParameter("runIdentifier")));
                Gson gson = new Gson();
                resp.getWriter().print(gson.toJson(runProgressDto));
            }
            else {
                resp.setStatus(400);
                resp.getWriter().println("bad run identifier");
            }
        }
        else {
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
    }
}
