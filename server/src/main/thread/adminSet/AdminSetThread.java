package main.thread.adminSet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "adminSetThread", urlPatterns = "/admin/setThreadCount")
public class AdminSetThread extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session==null ||
                getServletContext().getAttribute("adminSession")==null ||
                !getServletContext().getAttribute("adminSession").equals(session)) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            if (req.getParameter("threadCount") == null){
                resp.setStatus(422);
                resp.getWriter().println("missing thread count");
                return;
            }
            String param = req.getParameter("threadCount");
            // check if thread count is an integer
            if (!param.matches("\\d+")) {
                resp.setStatus(422);
                resp.getWriter().println("invalid thread count");
                return;
            }
            int threadCount = Integer.parseInt(param);
            if (threadCount <= 0) {
                resp.setStatus(422);
                resp.getWriter().println("invalid thread count");
                return;
            }
            EngineApi.getInstance().setThreadCount(threadCount);
            resp.setStatus(200);
            resp.getWriter().println("success");
        }
    }
}
