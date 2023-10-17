package main.simulation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dto.subdto.show.world.EntityDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.showWorld.ComparableSerializer;
import main.util.EngineApi;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "getEntityAmounts", urlPatterns = {"/getEntityAmounts"})
public class getEntityAmounts extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            String username = req.getParameter("username");
            if (!UserApproval.approveUser(username, session, req, resp)) {
                return;
            }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Comparable.class, new ComparableSerializer())
                    .create();
            resp.getWriter().println(gson.toJson(EngineApi.getInstance().getEntities(username),
                    new TypeToken<List<EntityDto>>() {}.getType()));
            resp.setStatus(200);
        }
    }
}
