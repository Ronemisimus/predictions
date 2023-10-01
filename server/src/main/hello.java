package main;

import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;

@ServerEndpoint(value = "/")
public class hello extends GenericServlet {
    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.getWriter().println("Hello, World!");
    }
}
