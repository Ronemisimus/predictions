package main;

import com.google.gson.Gson;
import dto.ReadFileDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name="readFile", urlPatterns={"/admin/readFile"})
public class ReadFile extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null ||
                getServletContext().getAttribute("adminSession") == null ||
                !getServletContext().getAttribute("adminSession").equals(session)) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            int contentLength = req.getContentLength();
            char[] content = new char[contentLength];
            //noinspection ResultOfMethodCallIgnored
            req.getReader().read(content);
            ReadFileDto answer = EngineApi.getInstance().readFile(content);
            Gson gson = new Gson();
            resp.getWriter().print(gson.toJson(answer));
        }
    }
}
