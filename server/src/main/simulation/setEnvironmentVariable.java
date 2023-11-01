package main.simulation;

import dto.EnvDto;
import dto.subdto.show.world.PropertyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import main.login.UserManager;
import main.util.EngineApi;

import java.io.IOException;

@WebServlet(name = "setEnvironmentVariable", urlPatterns = {"/setEnvironmentVariable"})
public class setEnvironmentVariable extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.setStatus(401);
            resp.getWriter().println("unauthorized");
        }
        else{
            String username = req.getParameter("username");
            String name = req.getParameter("name");
            String value = req.getParameter("value");
            if (username==null){
                resp.setStatus(401);
                resp.getWriter().println("username missing");
                return;
            }
            if (!UserManager.getInstance().userExists(username)){
                resp.setStatus(404);
                resp.getWriter().println("user not found");
                return;
            }
            if (!UserManager.getInstance().sameUser(username, session)){
                resp.setStatus(401);
                resp.getWriter().println("unauthorized for claimed user");
                return;
            }
            if (UserRequestManager.getInstance().getRequestId(username) == null)
            {
                resp.setStatus(401);
                resp.getWriter().println("request not set for user");
                return;
            }
            try {
                EnvDto env = EngineApi.getInstance().getEnv(username);
                PropertyDto res = env.getEnvironment().stream()
                        .filter(e->e.getName().equals(name))
                        .findFirst().orElse(null);

                String envType = res==null? null : res.getType();

                if (envType == null){
                    throw new RuntimeException("environment variable not found");
                }

                switch (envType.toLowerCase()) {
                    case "string":
                        EngineApi.getInstance().setEnvironmentVariable(username, name, value);
                        break;
                    case "decimal":
                        Integer intVal = Integer.parseInt(value);
                        Integer from = (Integer)res.getFrom();
                        Integer to = (Integer)res.getTo();
                        if (intVal > from && intVal < to){
                            EngineApi.getInstance().setEnvironmentVariable(username, name, intVal);
                        }
                        else {
                            throw new RuntimeException("value out of range");
                        }
                        break;
                    case "boolean":
                        Boolean boolVal = value.equalsIgnoreCase("true") ? Boolean.TRUE :
                                value.equalsIgnoreCase("false") ? Boolean.FALSE : null;
                        if (boolVal!=null){
                            EngineApi.getInstance().setEnvironmentVariable(username, name, boolVal);
                        }
                        else {
                            throw new RuntimeException("value isn't valid boolean");
                        }
                        break;
                    case "float":
                        Double floatVal = Double.parseDouble(value);
                        Double fromFloat = (Double)res.getFrom();
                        Double toFloat = (Double)res.getTo();
                        if (floatVal > fromFloat && floatVal < toFloat){
                            EngineApi.getInstance().setEnvironmentVariable(username, name, floatVal);
                        }
                        else {
                            throw new RuntimeException("value out of range");
                        }
                        break;
                    default:
                        throw new RuntimeException("environment variable type not supported");
                }
                resp.setStatus(200);
            }catch (Exception e){
                resp.setStatus(400);
                resp.getWriter().println(e.getMessage());
            }
        }
    }
}