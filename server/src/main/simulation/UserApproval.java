package main.simulation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;

import java.io.IOException;

public class UserApproval {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean approveUser(String username, HttpSession session, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (username == null){
            resp.setStatus(401);
            resp.getWriter().println("username missing");
            return false;
        }
        if (UserRequestManager.getInstance().getRequestId(username)==null) {
            resp.setStatus(401);
            resp.getWriter().println("request not set for user");
            return false;
        }
        if (!UserManager.getInstance().sameUser(username, session)) {
            resp.setStatus(401);
            resp.getWriter().println("unauthorized for claimed user");
            return false;
        }
        return true;
    }
}
