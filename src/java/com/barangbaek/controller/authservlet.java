package com.barangbaek.controller;

import com.barangbaek.bean.user;
import com.barangbaek.dao.userdao;

import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class authservlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int REMEMBER_SECONDS
            = 30 * 24 * 60 * 60;

    private static final String REMEMBER_COOKIE
            = "rememberToken";

    // Database connection
    private Connection getConnection() throws Exception {

        Class.forName("org.apache.derby.jdbc.ClientDriver");

        return DriverManager.getConnection(
                "jdbc:derby://localhost:1527/barangbaek_db",
                "app",
                "app"
        );
    }

    // Remember me helper
    private String generateRememberToken() {

        byte[] bytes = new byte[32];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String getCookieValue(
            HttpServletRequest request,
            String cookieName
    ) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {

            for (Cookie cookie : cookies) {

                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private String getCookiePath(
            HttpServletRequest request
    ) {

        String contextPath = request.getContextPath();

        if (contextPath == null || contextPath.isEmpty()) {
            return "/";
        }

        return contextPath;
    }

    private void addRememberCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            String token
    ) {

        Cookie cookie = new Cookie(
                REMEMBER_COOKIE,
                token
        );

        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setMaxAge(REMEMBER_SECONDS);
        cookie.setPath(getCookiePath(request));

        response.addCookie(cookie);
    }

    private void clearRememberCookie(
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        Cookie cookie = new Cookie(
                REMEMBER_COOKIE,
                ""
        );

        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));

        response.addCookie(cookie);
    }

    // Get request
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {
            action = "login";
        }

        if ("logout".equals(action)) {

            handleLogout(request, response);
            return;
        }

        if ("registerForm".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/register.jsp"
            ).forward(request, response);

            return;
        }

        if ("forgotPasswordForm".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/forgotpassword.jsp"
            ).forward(request, response);

            return;
        }

        if ("login".equals(action)
                || "loginForm".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/login.jsp"
            ).forward(request, response);

            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/auth?action=login"
        );
    }

    // Post request
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/auth?action=login"
            );

            return;
        }

        try (
                Connection conn = getConnection()) {

            userdao dao = new userdao(conn);

            if ("login".equals(action)) {

                handleLogin(
                        request,
                        response,
                        dao
                );

                return;
            }

            if ("verifyReset".equals(action)) {

                handleVerifyReset(
                        request,
                        response,
                        dao
                );

                return;
            }

            if ("resetPassword".equals(action)) {

                handleResetPassword(
                        request,
                        response,
                        dao
                );

                return;
            }

            if ("register".equals(action)) {

                handleRegistration(
                        request,
                        response,
                        dao
                );

                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/auth?action=login"
            );

        } catch (Exception e) {

            e.printStackTrace();

            if (!response.isCommitted()) {

                request.setAttribute(
                        "error",
                        "System error: " + e.getMessage()
                );

                forwardToRelatedErrorPage(
                        request,
                        response,
                        action
                );
            }
        }
    }

    // Login
    private void handleLogin(
            HttpServletRequest request,
            HttpServletResponse response,
            userdao dao
    ) throws Exception {

        String username
                = request.getParameter("username");

        String password
                = request.getParameter("password");

        if (username != null) {
            username = username.trim();
        }

        if (username == null
                || username.isEmpty()
                || password == null
                || password.isEmpty()) {

            request.setAttribute(
                    "error",
                    "Please enter your username and password."
            );

            request.getRequestDispatcher(
                    "/auth/login.jsp"
            ).forward(request, response);

            return;
        }

        user loggedInUser = dao.login(
                username,
                password
        );

        if (loggedInUser == null) {

            request.setAttribute(
                    "error",
                    "Invalid username or password."
            );

            request.getRequestDispatcher(
                    "/auth/login.jsp"
            ).forward(request, response);

            return;
        }

        String existingToken = getCookieValue(
                request,
                REMEMBER_COOKIE
        );

        if (existingToken != null
                && !existingToken.trim().isEmpty()) {

            dao.deleteRememberToken(existingToken);
        }

        HttpSession oldSession
                = request.getSession(false);

        if (oldSession != null) {
            oldSession.invalidate();
        }

        HttpSession newSession
                = request.getSession(true);

        newSession.setAttribute(
                "userID",
                loggedInUser.getUserID()
        );

        newSession.setAttribute(
                "username",
                loggedInUser.getUsername()
        );

        newSession.setAttribute(
                "fullName",
                loggedInUser.getFullName()
        );

        newSession.setAttribute(
                "user",
                loggedInUser
        );

        boolean remember
                = request.getParameter("remember") != null;

        if (remember) {
            dao.deleteRememberTokensByUser(
                    loggedInUser.getUserID()
            );

            String newToken
                    = generateRememberToken();

            long expiryTime
                    = System.currentTimeMillis()
                    + (REMEMBER_SECONDS * 1000L);

            Timestamp expiryDate
                    = new Timestamp(expiryTime);

            dao.saveRememberToken(
                    loggedInUser.getUserID(),
                    newToken,
                    expiryDate
            );

            addRememberCookie(
                    request,
                    response,
                    newToken
            );

        } else {

            clearRememberCookie(
                    request,
                    response
            );
        }

        response.sendRedirect(
                request.getContextPath()
                + "/item?action=dashboard"
        );
    }

    // Logout
    private void handleLogout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        String rememberToken = getCookieValue(
                request,
                REMEMBER_COOKIE
        );

        if (rememberToken != null
                && !rememberToken.trim().isEmpty()) {

            try (
                    Connection conn = getConnection()) {

                userdao dao = new userdao(conn);

                dao.deleteRememberToken(
                        rememberToken
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        clearRememberCookie(
                request,
                response
        );

        HttpSession session
                = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        response.sendRedirect(
                request.getContextPath()
                + "/auth?action=login"
        );
    }

    // forgot password verification
    private void handleVerifyReset(
            HttpServletRequest request,
            HttpServletResponse response,
            userdao dao
    ) throws Exception {

        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String birthday
                = request.getParameter("birthday");

        if (email != null) {
            email = email.trim();
        }

        if (phone != null) {
            phone = phone.trim();
        }

        if (email == null
                || email.isEmpty()
                || phone == null
                || phone.isEmpty()
                || birthday == null
                || birthday.isEmpty()) {

            request.setAttribute(
                    "error",
                    "Please complete all required information."
            );

            request.getRequestDispatcher(
                    "/auth/forgotpassword.jsp"
            ).forward(request, response);

            return;
        }

        user resetUser
                = dao.findUserForPasswordReset(
                        email,
                        phone,
                        birthday
                );

        if (resetUser == null) {

            request.setAttribute(
                    "error",
                    "The information does not match "
                    + "any registered account."
            );

            request.getRequestDispatcher(
                    "/auth/forgotpassword.jsp"
            ).forward(request, response);

            return;
        }

        HttpSession session
                = request.getSession(true);

        session.setAttribute(
                "passwordResetUserID",
                resetUser.getUserID()
        );

        session.setAttribute(
                "passwordResetEmail",
                resetUser.getEmail()
        );

        request.getRequestDispatcher(
                "/auth/resetpassword.jsp"
        ).forward(request, response);
    }

    // reset password
    private void handleResetPassword(
            HttpServletRequest request,
            HttpServletResponse response,
            userdao dao
    ) throws Exception {

        HttpSession session
                = request.getSession(false);

        if (session == null
                || session.getAttribute(
                        "passwordResetUserID"
                ) == null) {

            request.setAttribute(
                    "error",
                    "Your password-reset session has expired."
            );

            request.getRequestDispatcher(
                    "/auth/forgotpassword.jsp"
            ).forward(request, response);

            return;
        }

        String password
                = request.getParameter("password");

        String confirmPassword
                = request.getParameter(
                        "confirmPassword"
                );

        if (password == null
                || password.length() < 8) {

            request.setAttribute(
                    "error",
                    "Password must contain at least "
                    + "8 characters."
            );

            request.getRequestDispatcher(
                    "/auth/resetpassword.jsp"
            ).forward(request, response);

            return;
        }

        if (!password.equals(confirmPassword)) {

            request.setAttribute(
                    "error",
                    "Passwords do not match."
            );

            request.getRequestDispatcher(
                    "/auth/resetpassword.jsp"
            ).forward(request, response);

            return;
        }

        int resetUserID
                = (Integer) session.getAttribute(
                        "passwordResetUserID"
                );

        boolean updated = dao.updatePassword(
                resetUserID,
                password
        );

        if (!updated) {

            request.setAttribute(
                    "error",
                    "Password could not be updated."
            );

            request.getRequestDispatcher(
                    "/auth/resetpassword.jsp"
            ).forward(request, response);

            return;
        }

        /*
         * Remove all remembered logins after
         * a password change.
         */
        dao.deleteRememberTokensByUser(
                resetUserID
        );

        clearRememberCookie(
                request,
                response
        );

        session.invalidate();

        request.setAttribute(
                "success",
                "Password reset successful. "
                + "Please log in with your new password."
        );

        request.getRequestDispatcher(
                "/auth/login.jsp"
        ).forward(request, response);
    }

    // Registration
    private void handleRegistration(
            HttpServletRequest request,
            HttpServletResponse response,
            userdao dao
    ) throws Exception {

        String username
                = trimParameter(
                        request.getParameter("username")
                );

        String fullName
                = trimParameter(
                        request.getParameter("fullname")
                );

        String gender
                = trimParameter(
                        request.getParameter("gender")
                );

        String birthday
                = trimParameter(
                        request.getParameter("birthday")
                );

        String university
                = trimParameter(
                        request.getParameter("university")
                );

        String email
                = trimParameter(
                        request.getParameter("email")
                ).toLowerCase();

        String address1
                = trimParameter(
                        request.getParameter("address1")
                );

        String address2
                = trimParameter(
                        request.getParameter("address2")
                );

        String city
                = trimParameter(
                        request.getParameter("city")
                );

        String state
                = trimParameter(
                        request.getParameter("state")
                );

        String postcode
                = trimParameter(
                        request.getParameter("postcode")
                );

        String phone
                = trimParameter(
                        request.getParameter("phone")
                );

        String password
                = request.getParameter("password");

        String confirmPassword
                = request.getParameter("confirmPassword");

        /*
     * Required fields
         */
        if (username.isEmpty()
                || fullName.isEmpty()
                || gender.isEmpty()
                || birthday.isEmpty()
                || university.isEmpty()
                || email.isEmpty()
                || address1.isEmpty()
                || city.isEmpty()
                || state.isEmpty()
                || postcode.isEmpty()
                || phone.isEmpty()
                || password == null
                || confirmPassword == null) {

            forwardRegistrationError(
                    request,
                    response,
                    "Please complete all required fields."
            );

            return;
        }

        /*
     * Username validation
         */
        if (!username.matches(
                "^[A-Za-z0-9._]{3,30}$"
        )) {

            forwardRegistrationError(
                    request,
                    response,
                    "Username must contain 3–30 letters, "
                    + "numbers, dots or underscores."
            );

            return;
        }

        if (dao.usernameExists(username)) {

            forwardRegistrationError(
                    request,
                    response,
                    "That username is already taken."
            );

            return;
        }

        /*
     * Full-name validation
         */
        if (fullName.length() > 100) {

            forwardRegistrationError(
                    request,
                    response,
                    "Full name is too long."
            );

            return;
        }

        /*
     * Gender validation
         */
        if (!"Male".equals(gender)
                && !"Female".equals(gender)) {

            forwardRegistrationError(
                    request,
                    response,
                    "Please select a valid gender."
            );

            return;
        }

        /*
     * Age validation
         */
        try {

            LocalDate birthDate
                    = LocalDate.parse(birthday);

            LocalDate today
                    = LocalDate.now();

            if (birthDate.isAfter(today)) {

                forwardRegistrationError(
                        request,
                        response,
                        "Date of birth cannot be in the future."
                );

                return;
            }

            int age
                    = Period.between(
                            birthDate,
                            today
                    ).getYears();

            if (age < 18) {

                forwardRegistrationError(
                        request,
                        response,
                        "You must be at least 18 years old."
                );

                return;
            }

        } catch (DateTimeParseException e) {

            forwardRegistrationError(
                    request,
                    response,
                    "Please enter a valid date of birth."
            );

            return;
        }

        /*
     * Education email validation
         */
        String educationEmailPattern
                = "(?i)^[A-Z0-9._%+-]+@"
                + "[A-Z0-9.-]+\\.edu\\.my$";

        if (!email.matches(educationEmailPattern)) {

            forwardRegistrationError(
                    request,
                    response,
                    "Please use a valid education email "
                    + "ending with .edu.my."
            );

            return;
        }

        if (dao.emailExists(email)) {

            forwardRegistrationError(
                    request,
                    response,
                    "That email address is already registered."
            );

            return;
        }

        /*
     * Postcode validation
         */
        if (!postcode.matches("^[0-9]{5}$")) {

            forwardRegistrationError(
                    request,
                    response,
                    "Postcode must contain exactly 5 digits."
            );

            return;
        }

        /*
     * Phone validation
         */
        if (!phone.matches("^[0-9+\\-\\s]{9,15}$")) {

            forwardRegistrationError(
                    request,
                    response,
                    "Please enter a valid phone number."
            );

            return;
        }

        /*
     * Password validation
         */
        boolean hasMinimumLength
                = password.length() >= 8;

        boolean hasUppercase
                = password.matches(".*[A-Z].*");

        boolean hasNumber
                = password.matches(".*\\d.*");

        boolean hasSymbol
                = password.matches(
                        ".*[!@#$%^&*()_\\-+=<>?].*"
                );

        if (!hasMinimumLength
                || !hasUppercase
                || !hasNumber
                || !hasSymbol) {

            forwardRegistrationError(
                    request,
                    response,
                    "Password must contain at least "
                    + "8 characters, one uppercase letter, "
                    + "one number and one symbol."
            );

            return;
        }

        if (!password.equals(confirmPassword)) {

            forwardRegistrationError(
                    request,
                    response,
                    "Passwords do not match."
            );

            return;
        }

        /*
     * Create user object
         */
        user newUser = new user();

        newUser.setUsername(username);
        newUser.setFullName(fullName);
        newUser.setBirthday(birthday);
        newUser.setGender(gender);
        newUser.setUniversity(university);
        newUser.setEmail(email);
        newUser.setAddress1(address1);
        newUser.setAddress2(address2);
        newUser.setCity(city);
        newUser.setState(state);
        newUser.setPostcode(postcode);
        newUser.setPhone(phone);
        newUser.setPassword(password);
        newUser.setUserPhoto("default-user.png");

        boolean registrationSuccessful
                = dao.registerUser(newUser);

        if (!registrationSuccessful) {

            forwardRegistrationError(
                    request,
                    response,
                    "Registration failed. Please try again."
            );

            return;
        }

        request.setAttribute(
                "success",
                "Registration successful. "
                + "Please log in."
        );

        request.getRequestDispatcher(
                "/auth/login.jsp"
        ).forward(request, response);
    }

    //error handling
    private void forwardToRelatedErrorPage(
            HttpServletRequest request,
            HttpServletResponse response,
            String action
    ) throws ServletException, IOException {

        if ("register".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/register.jsp"
            ).forward(request, response);

            return;
        }

        if ("verifyReset".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/forgotpassword.jsp"
            ).forward(request, response);

            return;
        }

        if ("resetPassword".equals(action)) {

            request.getRequestDispatcher(
                    "/auth/resetpassword.jsp"
            ).forward(request, response);

            return;
        }

        request.getRequestDispatcher(
                "/auth/login.jsp"
        ).forward(request, response);
    }


    // general helper
    private String trimParameter(String value) {

        if (value == null) {
            return "";
        }

        return value.trim();
    }

    private void forwardRegistrationError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage
    ) throws ServletException, IOException {

        request.setAttribute(
                "error",
                errorMessage
        );

        request.getRequestDispatcher(
                "/auth/register.jsp"
        ).forward(request, response);
    }

}
