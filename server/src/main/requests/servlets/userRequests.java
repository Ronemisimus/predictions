package main.requests.servlets;

import com.google.gson.Gson;
import dto.subdto.requests.RequestDetailsDto;
import dto.subdto.requests.RequestEntryDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.requests.request.Request;
import main.requests.requestManager.RequestManager;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@WebServlet(name = "userRequests", urlPatterns = {"/requests"})
public class userRequests extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null){
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            String username = req.getParameter("username");
            if (!UserManager.getInstance().userExists(username)) {
                resp.setStatus(404);
                resp.getWriter().println("user not found");
            }
            else if (UserManager.getInstance().sameUser(username, session)) {
                Collection<RequestDetailsDto> requests = RequestManager.getInstance().getRequestsByUser(username);
                Gson gson = new Gson();
                resp.getWriter().print(gson.toJson(requests));
            }
            else {
                resp.setStatus(401);
                resp.getWriter().println("unauthorized");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null){
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            Gson gson = new Gson();
            RequestEntryDto entry;
            if (req.getContentLength() > 0) {
                entry = gson.fromJson(req.getReader().lines().collect(Collectors.joining()), RequestEntryDto.class);
                Request request = RequestManager.getInstance().addRequest(entry);
                if (request.isValid()) {
                    resp.getWriter().print("success");
                    resp.setStatus(200);
                }
                else{
                    resp.setStatus(422);
                    if (request.requestingUser() == null){
                        resp.getWriter().println("missing requesting user");
                    }
                    else if (request.getTerminationTypes().isEmpty()){
                        resp.getWriter().println("missing termination types");
                    }
                    else if (request.getRunAllocation() <=0){
                        resp.getWriter().println("missing/invalid run allocation");
                    }
                    else if (request.getWorldName() == null){
                        resp.getWriter().println("missing/non-registered world name");
                    }
                    else{
                        resp.getWriter().println("invalid request");
                    }
                }
            }
            else{
                resp.setStatus(422);
                resp.getWriter().println("missing request content");
            }
        }
    }
}
