package com.cskaoyan.www.servlet.UserServlet;

import com.cskaoyan.www.bean.User;
import com.cskaoyan.www.mapper.UserMapper;
import com.cskaoyan.www.utils.MybatisLoad;
import org.apache.ibatis.session.SqlSession;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/user/*")
public class UserServlet extends HttpServlet {

    static UserMapper mapper;
    static SqlSession sqlSession;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.equals("/login")) {
            login(req, resp);
        } else if (pathInfo.equals("/register")) {
            register(req, resp);
        } else if (pathInfo.equals("/reset-password")) {
            resetPassword(req, resp);
        } else if (pathInfo.equals("/logout")) {
            logout(req, resp);
        } else if (pathInfo.equals("/center")) {
            center(req, resp);
        }
    }

    private void center(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");

        PrintWriter out = resp.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>User Center</title>");
        out.println("<style>");
        out.println("* {");
        out.println("box-sizing: border-box;");
        out.println("margin: 0;");
        out.println("padding: 0;");
        out.println("}");

        out.println("body {");
        out.println("font-family: Arial, sans-serif;");
        out.println("background-repeat: no-repeat;");
        out.println("background-size: cover;");
        out.println("background-image: url(\"bg3.jpg\");");
        out.println("background-attachment: fixed;");
        out.println("}");

        out.println(".container {");
        out.println("width: 400px;");
        out.println("margin: 80px auto;");
        out.println("padding: 40px;");
        out.println("border-radius: 10px;");
        out.println("box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);");
        out.println("background-color: rgba(255, 255, 255, 0.3);");
        out.println("backdrop-filter: blur(10px);");
        out.println("}");

        out.println("h1 {");
        out.println("text-align: center;");
        out.println("margin-bottom: 20px;");
        out.println("color: #333;");
        out.println("}");

        out.println(".user-email {");
        out.println("font-size: 16px;");
        out.println("color: #333;");
        out.println("margin-bottom: 20px;");
        out.println("}");

        out.println(".btn {");
        out.println("width: 100%;");
        out.println("padding: 12px;");
        out.println("border: none;");
        out.println("border-radius: 4px;");
        out.println("font-size: 16px;");
        out.println("font-weight: bold;");
        out.println("cursor: pointer;");
        out.println("}");

        out.println(".btn-change-password {");
        out.println("background-color: #4285f4;");
        out.println("color: #fff;");
        out.println("}");

        out.println(".btn-logout {");
        out.println("margin-top: 10px;");
        out.println("background-color: #f44336;");
        out.println("color: #fff;");
        out.println("}");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class=\"container\">");
        out.println("<h1>User Center</h1>");
        out.println("<p class=\"user-email\">Email: <span id=\"user-email\">" + email + "</span></p>");
        out.println("<button class=\"btn btn-change-password\" onclick=\"location.href='/reset-password.html'\">Change Password</button>");
        out.println("<button class=\"btn btn-logout\" onclick=\"location.href='/user/logout'\">Logout</button>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

        out.close();
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo.equals("/login")) {
            login(req, resp);
        } else if (pathInfo.equals("/register")) {
            register(req, resp);
        } else if (pathInfo.equals("/reset-password")) {
            resetPassword(req, resp);
        } else if (pathInfo.equals("/logout")) {
            logout(req, resp);
        } else if (pathInfo.equals("/center")) {
            center(req, resp);
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        session.invalidate();
        resp.sendRedirect("/index.html");
    }

    private void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String email = (String) session.getAttribute("email");
        String oldPassword = req.getParameter("old-password");
        String newPassword = req.getParameter("new-password");
        String newPasswordCheck = req.getParameter("new-password-check");

        // 旧密码不正确：2，      新密码两次输入不匹配：1，    修改成功：0
        int resetStatus = resetPasswordCheck(email, oldPassword, newPassword, newPasswordCheck);

        switch (resetStatus) {
            case 2:
                resp.sendRedirect("/wrong-old-password.html");
                break;
            case 1:
                resp.sendRedirect("/reset-password-not-match.html");
                break;
            case 0:
                session.invalidate();
                resp.sendRedirect("/reset-password-success.html");
                break;
        }

    }

    private int resetPasswordCheck(String email, String oldPassword, String newPassword, String newPasswordCheck) {
        sqlSession = MybatisLoad.getSqlSession();
        mapper = sqlSession.getMapper(UserMapper.class);
        User[] usersArray = mapper.selectUser();
        ArrayList<User> users = new ArrayList<>(Arrays.asList(usersArray));

        // 旧密码错误
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                if (!user.getPassword().equals(oldPassword)) {
                    return 2;
                }
                break;
            }
        }

        // 两次输入密码不对
        if (!newPassword.equals(newPasswordCheck)) {
            return 1;
        }

        // 修改成功
        mapper.resetPassword(email, newPassword);
        return 0;
    }

    private void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String passwordCheck = req.getParameter("password-check");

        // 用户名已存在：2， 密码两次输入不一致：1，注册成功：0
        int registerStatus = registerCheck(email, password, passwordCheck);

        switch (registerStatus) {
            case 2:
                resp.sendRedirect("/user-already-exist.html");
                break;
            case 1:
                resp.sendRedirect("/password-not-match.html");
                break;
            case 0:
                resp.sendRedirect("/register-success.html");
                break;
        }
    }

    private int registerCheck(String email, String password, String passwordCheck) {
        sqlSession = MybatisLoad.getSqlSession();
        mapper = sqlSession.getMapper(UserMapper.class);
        User[] usersArray = mapper.selectUser();
        ArrayList<User> users = new ArrayList<>(Arrays.asList(usersArray));

        // 检查用户是否已存在
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return 2;
            }
        }

        // 两次密码输入不一致
        if (!password.equals(passwordCheck)) {
            return 1;
        }

        mapper.insertUser(email, password);
        return 0;
    }


    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        // 登录情况  用户名不存在 : 2 , 密码错误 : 1 , 登录成功 0
        int loginStatus = loginCheck(email, password);

        switch (loginStatus) {
            case 0:
                HttpSession session = req.getSession();
                session.setAttribute("email", email);
                resp.sendRedirect("/user/center");
                break;
            case 1:
                resp.sendRedirect("/password-not-match.html");
                break;
            case 2:
                resp.sendRedirect("/user-not-exist.html");
                break;
        }
    }

    private int loginCheck(String email, String password) {
        sqlSession = MybatisLoad.getSqlSession();
        mapper = sqlSession.getMapper(UserMapper.class);
        User[] usersArray = mapper.selectUser();
        ArrayList<User> users = new ArrayList<>(Arrays.asList(usersArray));

        int longinStatus = 2;

        for (User u : users) {
            if (u.getEmail().equals(email)) {
                if (u.getPassword().equals(password)) {
                    return 0;
                }
                longinStatus = 1;
            }
        }
        if (longinStatus == 1) {
            return 1;
        }
        return 2;
    }
}
