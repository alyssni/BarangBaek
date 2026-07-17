package com.barangbaek.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.barangbaek.bean.user;
import com.barangbaek.dao.userdao;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.servlet.http.Cookie;

public class authfilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);

        // Normal session login
        if (session != null
                && session.getAttribute("userID") != null) {

            chain.doFilter(request, response);
            return;
        }

        // Check remember-me cookie
        String token = null;
        Cookie[] cookies = req.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && !token.trim().isEmpty()) {
            try {
                Class.forName("org.apache.derby.jdbc.ClientDriver");

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:derby://localhost:1527/barangbaek_db",
                        "app",
                        "app"
                )) {
                    userdao dao = new userdao(conn);

                    user rememberedUser
                            = dao.getUserByRememberToken(token);

                    if (rememberedUser != null) {
                        session = req.getSession(true);

                        session.setAttribute(
                                "userID",
                                rememberedUser.getUserID()
                        );

                        session.setAttribute(
                                "username",
                                rememberedUser.getUsername()
                        );

                        session.setAttribute(
                                "fullName",
                                rememberedUser.getFullName()
                        );

                        session.setAttribute(
                                "user",
                                rememberedUser
                        );

                        chain.doFilter(request, response);
                        return;
                    }

                    dao.deleteRememberToken(token);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Delete invalid or expired cookie
            Cookie expiredCookie
                    = new Cookie("rememberToken", "");

            expiredCookie.setHttpOnly(true);
            expiredCookie.setMaxAge(0);

            expiredCookie.setPath(
                    req.getContextPath().isEmpty()
                    ? "/"
                    : req.getContextPath()
            );

            res.addCookie(expiredCookie);
        }

        res.sendRedirect(
                req.getContextPath() + "/auth?action=login"
        );
    }

    @Override
    public void destroy() {
    }
}
