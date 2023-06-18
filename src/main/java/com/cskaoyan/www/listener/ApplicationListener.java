package com.cskaoyan.www.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Poikka
 * @description
 * @date 2023/6/18 16:25
 */
@WebListener
public class ApplicationListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassLoader classLoader = ApplicationListener.class.getClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("auth.txt");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resourceAsStream));
        String line;
        ArrayList<String> uris = new ArrayList<>();
        try {
            while (((line = bufferedReader.readLine()) != null)) {
                uris.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("uris", uris);
    }
}
