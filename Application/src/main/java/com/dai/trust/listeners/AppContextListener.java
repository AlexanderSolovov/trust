package com.dai.trust.listeners;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        System.setProperty("trust.path", context.getRealPath("/"));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }
}
