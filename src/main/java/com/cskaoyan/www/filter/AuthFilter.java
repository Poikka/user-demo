package com.cskaoyan.www.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Poikka
 * @description
 * @date 2023/6/18 14:49
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        ServletContext servletContext = request.getServletContext();
        ArrayList<String> uris = (ArrayList<String>) servletContext.getAttribute("uris");

        String requestURI = request.getRequestURI();

        if(uris.contains(requestURI)){
            HttpSession session = request.getSession();
            Object attribute = session.getAttribute("email");
            if(attribute == null){
                response.sendRedirect("/index.html");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
