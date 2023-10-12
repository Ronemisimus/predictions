package main.requests.servlets;

import com.google.gson.Gson;
import dto.subdto.requests.RequestDetailsDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.requests.request.Request;
import main.requests.request.RequestStatus;
import main.requests.requestManager.RequestManager;

import java.io.IOException;
import java.util.Collection;

@WebServlet(name = "adminRequests", urlPatterns = {"/admin/requests"})
public class adminRequests extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null ||
                getServletContext().getAttribute("adminSession") == null ||
                !getServletContext().getAttribute("adminSession").equals(session)) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            Collection<RequestDetailsDto> requests = RequestManager.getInstance().getRequests();
            Gson gson = new Gson();
            resp.getWriter().print(gson.toJson(requests));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null ||
                getServletContext().getAttribute("adminSession") == null ||
                !getServletContext().getAttribute("adminSession").equals(session)) {
            // send unauthorized message with correct status code
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else {
            String requestId = req.getParameter("requestId");
            String status = req.getParameter("status");
            String message = "";
            boolean valid = true;
            if (requestId == null){
                valid = false;
                message = "missing requestId";
            }
            else if (status == null){
                valid = false;
                message = "missing status";
            }
            else if (!requestId.matches("\\d+")){
                valid = false;
                message = "invalid requestId";
            }
            else if (!status.matches("approve|reject"))
            {
                valid = false;
                message = "invalid status";
            }
            if (!valid){
                resp.setStatus(422);
                resp.getWriter().println(message);
            }
            else{
                Request request = RequestManager.getInstance().getRequest(Integer.parseInt(requestId));
                if (request == null){
                    resp.setStatus(404);
                    resp.getWriter().println("request not found");
                }
                else {
                    RequestStatus result;
                    if (status.equals("approve")){
                        result = request.approve();
                    }
                    else{
                        result = request.reject();
                    }
                    if (result == RequestStatus.WAITING)
                    {
                        resp.setStatus(200);
                        resp.getWriter().println("success");
                    }
                    else{
                        resp.setStatus(422);
                        resp.getWriter().println("cannot approve when request is " + result.name());
                    }
                }
            }
        }
    }
}
