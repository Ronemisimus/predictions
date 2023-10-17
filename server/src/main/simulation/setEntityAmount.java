package main.simulation;

import dto.subdto.show.world.EntityDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "setEntityAmount", urlPatterns = {"/setEntityAmount"})
public class setEntityAmount extends HttpServlet {
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
            String name = req.getParameter("name");
            String amount = req.getParameter("amount");
            if (username==null){
                resp.setStatus(401);
                resp.getWriter().println("username missing");
                return;
            }
            if (UserRequestManager.getInstance().getRequestId(username) == null) {
                resp.setStatus(401);
                resp.getWriter().println("request not set for user");
                return;
            }
            if (!UserManager.getInstance().sameUser(username, session)) {
                resp.setStatus(401);
                resp.getWriter().println("unauthorized for claimed user");
                return;
            }
            Integer entityAmount = EngineApi.getInstance().getEntities(username).stream()
                    .filter(e -> e.getName().equals(name))
                    .map(EntityDto::getAmount)
                    .findFirst().orElse(null);
            if (entityAmount == null) {
                resp.setStatus(404);
                resp.getWriter().println("entity not found");
            }
            if (!amount.matches("\\d+"))
            {
                resp.setStatus(400);
                resp.getWriter().println("amount invalid");
                return;
            }
            try {
                EngineApi.getInstance().setEntityAmount(username, name, Integer.parseInt(amount));
                resp.setStatus(200);
            }
            catch (Exception e){
                resp.setStatus(400);
                resp.getWriter().println(e.getMessage());
            }
        }
    }
}
