package main.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.subdto.show.EntityListDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.showWorld.ComparableSerializer;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "getEntityList", urlPatterns = { "/getEntityList", "/admin/getEntityList" })
public class getEntityList extends HttpServlet {
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
                EntityListDto entityListDto = EngineApi.getInstance().getEntityList(Integer.parseInt(req.getParameter("runIdentifier")));
                Gson gson = new GsonBuilder().registerTypeAdapter(Comparable.class, new ComparableSerializer()).create();
                resp.getWriter().print(gson.toJson(entityListDto));
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
