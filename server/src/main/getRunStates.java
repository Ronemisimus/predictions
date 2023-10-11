package main;

import com.google.gson.Gson;
import dto.RunHistoryDto;
import dto.subdto.show.instance.RunStateDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "getRunStates", urlPatterns = {"/getRunStates", "/admin/getRunStates"})
public class getRunStates extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            RunHistoryDto runHistoryDto = EngineApi.getInstance().getRunStates();
            Gson gson = new Gson();
            resp.getWriter().print(gson.toJson(runHistoryDto));
        }
    }
}
