package main.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.subdto.SingleRunHistoryDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.showWorld.ComparableSerializer;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "getSingleRunPropertyData", urlPatterns = {"/getSingleRunPropertyData", "/admin/getSingleRunPropertyData"})
public class getSingleRunPropertyData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
            if (req.getParameter("runIdentifier") != null && req.getParameter("runIdentifier").matches("-?\\d+")
            && req.getParameter("entity") != null && req.getParameter("prop") != null) {
                SingleRunHistoryDto runHistoryDto = EngineApi.getInstance().getRunPropertyHist(
                        Integer.parseInt(req.getParameter("runIdentifier")),
                        req.getParameter("entity"), req.getParameter("prop"));
                Gson gson = new GsonBuilder().registerTypeAdapter(Comparable.class, new ComparableSerializer()).create();
                resp.getWriter().print(gson.toJson(runHistoryDto));
            }
            else {
                resp.setStatus(400);
                resp.getWriter().println("bad run identifier/entity name/prop name");
            }
        }
        else {
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
    }
}
