package com.barangbaek.controller.user;

import com.barangbaek.bean.user;
import com.barangbaek.dao.cartdao;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.userdao;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class profileservlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_PHOTO = "default-user.png";

    private Connection getConnection() throws Exception {
        Class.forName("org.apache.derby.jdbc.ClientDriver");

        return DriverManager.getConnection(
                "jdbc:derby://localhost:1527/barangbaek_db",
                "app",
                "app"
        );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if (action == null || "view".equals(action)) {
            viewProfile(request, response);
            return;
        }

        if ("edit".equals(action)) {
            editProfile(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/profile?action=view"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateProfile(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/profile?action=view"
        );
    }

    private void viewProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            response.sendRedirect(
                    request.getContextPath() + "/auth?action=login"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            userdao dao = new userdao(conn);
            user profileUser = dao.getUserByID(userID);

            if (profileUser == null) {
                session.invalidate();
                response.sendRedirect(
                        request.getContextPath() + "/auth?action=login"
                );
                return;
            }

            restorePhotoIfNeeded(profileUser.getUserPhoto());
            updateSessionUser(session, profileUser);
            loadNavbarData(conn, request, session, userID);

            if ("true".equals(request.getParameter("updated"))) {
                request.setAttribute(
                        "success",
                        "Your profile has been updated successfully."
                );
            }

            request.setAttribute("user", profileUser);
            request.getRequestDispatcher(
                    "/user/profile.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(
                    "error",
                    "Unable to load your profile: " + e.getMessage()
            );
            request.getRequestDispatcher(
                    "/user/profile.jsp"
            ).forward(request, response);
        }
    }

    private void editProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            response.sendRedirect(
                    request.getContextPath() + "/auth?action=login"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            userdao dao = new userdao(conn);
            user profileUser = dao.getUserByID(userID);

            if (profileUser == null) {
                session.invalidate();
                response.sendRedirect(
                        request.getContextPath() + "/auth?action=login"
                );
                return;
            }

            restorePhotoIfNeeded(profileUser.getUserPhoto());
            updateSessionUser(session, profileUser);
            loadNavbarData(conn, request, session, userID);

            request.setAttribute("user", profileUser);
            request.getRequestDispatcher(
                    "/user/editprofile.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            forwardReloadedError(
                    request,
                    response,
                    session,
                    userID,
                    "Unable to load the edit profile page: " + e.getMessage()
            );
        }
    }

    private void updateProfile(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            response.sendRedirect(
                    request.getContextPath() + "/auth?action=login"
            );
            return;
        }

        Connection conn = null;
        String newPhotoName = null;
        String previousPhotoName = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            userdao dao = new userdao(conn);
            user existingUser = dao.getUserByID(userID);

            if (existingUser == null) {
                conn.rollback();
                session.invalidate();
                response.sendRedirect(
                        request.getContextPath() + "/auth?action=login"
                );
                return;
            }

            previousPhotoName = existingUser.getUserPhoto();

            String username = trimParameter(
                    request.getParameter("username")
            );
            String fullName = trimParameter(
                    request.getParameter("fullname")
            );
            String address1 = trimParameter(
                    request.getParameter("address1")
            );
            String address2 = trimParameter(
                    request.getParameter("address2")
            );
            String city = trimParameter(
                    request.getParameter("city")
            );
            String state = trimParameter(
                    request.getParameter("state")
            );
            String postcode = trimParameter(
                    request.getParameter("postcode")
            );
            String phone = trimParameter(
                    request.getParameter("phone")
            );
            String university = trimParameter(
                    request.getParameter("university")
            );

            existingUser.setUsername(username);
            existingUser.setFullName(fullName);
            existingUser.setAddress1(address1);
            existingUser.setAddress2(address2);
            existingUser.setCity(city);
            existingUser.setState(state);
            existingUser.setPostcode(postcode);
            existingUser.setPhone(phone);
            existingUser.setUniversity(university);

            String validationError = validateProfile(
                    dao,
                    existingUser,
                    userID
            );

            if (validationError != null) {
                conn.rollback();
                loadNavbarData(conn, request, session, userID);
                forwardEditError(
                        request,
                        response,
                        existingUser,
                        validationError
                );
                return;
            }

            if (!dao.updateProfile(existingUser)) {
                throw new Exception("Profile details could not be updated.");
            }

            Part photoPart = request.getPart("userPhoto");

            if (photoPart != null && photoPart.getSize() > 0) {
                validatePhoto(photoPart);
                newPhotoName = saveUploadedPhoto(photoPart, userID);

                if (!dao.updateUserPhoto(userID, newPhotoName)) {
                    throw new Exception("Profile photo could not be updated.");
                }
            }

            conn.commit();

            if (newPhotoName != null) {
                deleteOldPhoto(previousPhotoName);
            }

            user refreshedUser = dao.getUserByID(userID);
            updateSessionUser(session, refreshedUser);

            response.sendRedirect(
                    request.getContextPath()
                    + "/profile?action=view&updated=true"
            );

        } catch (IllegalStateException e) {
            rollbackQuietly(conn);
            deleteNewPhotoQuietly(newPhotoName);
            forwardReloadedError(
                    request,
                    response,
                    session,
                    userID,
                    "The selected image is too large. Maximum size is 5 MB."
            );

        } catch (Exception e) {
            rollbackQuietly(conn);
            deleteNewPhotoQuietly(newPhotoName);
            e.printStackTrace();
            forwardReloadedError(
                    request,
                    response,
                    session,
                    userID,
                    "Profile update failed: " + e.getMessage()
            );

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    private String validateProfile(
            userdao dao,
            user profileUser,
            int userID
    ) throws Exception {

        String username = profileUser.getUsername();
        String fullName = profileUser.getFullName();
        String address1 = profileUser.getAddress1();
        String address2 = profileUser.getAddress2();
        String city = profileUser.getCity();
        String state = profileUser.getState();
        String postcode = profileUser.getPostcode();
        String phone = profileUser.getPhone();
        String university = profileUser.getUniversity();

        if (username.isEmpty()
                || fullName.isEmpty()
                || address1.isEmpty()
                || city.isEmpty()
                || state.isEmpty()
                || postcode.isEmpty()
                || phone.isEmpty()
                || university.isEmpty()) {
            return "Please complete all required fields.";
        }

        if (!username.matches("^[A-Za-z0-9._]{3,30}$")) {
            return "Username must contain 3–30 letters, numbers, dots or underscores.";
        }

        if (dao.usernameExistsForOtherUser(username, userID)) {
            return "That username is already used by another account.";
        }

        if (fullName.length() > 100) {
            return "Full name must not exceed 100 characters.";
        }

        if (address1.length() > 150 || address2.length() > 150) {
            return "Each address line must not exceed 150 characters.";
        }

        if (city.length() > 50 || state.length() > 100) {
            return "The selected city or state is invalid.";
        }

        if (!postcode.matches("^[0-9]{5}$")) {
            return "Postcode must contain exactly 5 digits.";
        }

        if (!phone.matches("^[0-9+\\-\\s]{9,15}$")) {
            return "Please enter a valid phone number.";
        }

        if (university.length() > 150) {
            return "University name is too long.";
        }

        return null;
    }

    private void validatePhoto(Part photoPart) throws Exception {
        String contentType = photoPart.getContentType();
        String submittedName = photoPart.getSubmittedFileName();
        String extension = getPhotoExtension(contentType, submittedName);

        if (extension == null) {
            throw new Exception(
                    "Profile photo must be a JPG, JPEG, PNG or WEBP image."
            );
        }

        if (photoPart.getSize() > 5L * 1024L * 1024L) {
            throw new Exception(
                    "The selected image is too large. Maximum size is 5 MB."
            );
        }
    }

    private String saveUploadedPhoto(
            Part photoPart,
            int userID
    ) throws Exception {

        String extension = getPhotoExtension(
                photoPart.getContentType(),
                photoPart.getSubmittedFileName()
        );

        String filename = "user_"
                + userID
                + "_"
                + System.currentTimeMillis()
                + "."
                + extension;

        File persistentDirectory = getPersistentPhotoDirectory();

        if (!persistentDirectory.exists()
                && !persistentDirectory.mkdirs()) {
            throw new IOException(
                    "Unable to create the profile-photo directory."
            );
        }

        Path persistentFile = new File(
                persistentDirectory,
                filename
        ).toPath();

        Files.copy(
                photoPart.getInputStream(),
                persistentFile,
                StandardCopyOption.REPLACE_EXISTING
        );

        File webDirectory = getWebPhotoDirectory();

        if (webDirectory != null) {
            if (!webDirectory.exists()) {
                webDirectory.mkdirs();
            }

            Files.copy(
                    persistentFile,
                    new File(webDirectory, filename).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return filename;
    }

    private void restorePhotoIfNeeded(String photoName) {
        if (photoName == null
                || photoName.trim().isEmpty()
                || DEFAULT_PHOTO.equals(photoName)) {
            return;
        }

        try {
            File webDirectory = getWebPhotoDirectory();

            if (webDirectory == null) {
                return;
            }

            File webPhoto = new File(webDirectory, photoName);

            if (webPhoto.exists()) {
                return;
            }

            File persistentPhoto = new File(
                    getPersistentPhotoDirectory(),
                    photoName
            );

            if (!persistentPhoto.exists()) {
                return;
            }

            if (!webDirectory.exists()) {
                webDirectory.mkdirs();
            }

            Files.copy(
                    persistentPhoto.toPath(),
                    webPhoto.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

        } catch (Exception ignored) {
        }
    }

    private void deleteOldPhoto(String photoName) {
        if (photoName == null
                || photoName.trim().isEmpty()
                || DEFAULT_PHOTO.equals(photoName)) {
            return;
        }

        try {
            Files.deleteIfExists(
                    new File(
                            getPersistentPhotoDirectory(),
                            photoName
                    ).toPath()
            );
        } catch (Exception ignored) {
        }

        try {
            File webDirectory = getWebPhotoDirectory();

            if (webDirectory != null) {
                Files.deleteIfExists(
                        new File(webDirectory, photoName).toPath()
                );
            }
        } catch (Exception ignored) {
        }
    }

    private void deleteNewPhotoQuietly(String photoName) {
        if (photoName == null) {
            return;
        }

        deleteOldPhoto(photoName);
    }

    private String getPhotoExtension(
            String contentType,
            String submittedName
    ) {
        String lowerType = contentType == null
                ? ""
                : contentType.toLowerCase(Locale.ENGLISH);

        if ("image/jpeg".equals(lowerType)
                || "image/jpg".equals(lowerType)) {
            return "jpg";
        }

        if ("image/png".equals(lowerType)) {
            return "png";
        }

        if ("image/webp".equals(lowerType)) {
            return "webp";
        }

        if (submittedName != null) {
            String lowerName = submittedName.toLowerCase(Locale.ENGLISH);

            if (lowerName.endsWith(".jpg")
                    || lowerName.endsWith(".jpeg")) {
                return "jpg";
            }

            if (lowerName.endsWith(".png")) {
                return "png";
            }

            if (lowerName.endsWith(".webp")) {
                return "webp";
            }
        }

        return null;
    }

    private File getPersistentPhotoDirectory() {
        return new File(
                System.getProperty("user.home"),
                "BarangBaekUploads"
                + File.separator
                + "userphoto"
        );
    }

    private File getWebPhotoDirectory() {
        String realPath = getServletContext().getRealPath(
                "/assets/img/userphoto"
        );

        if (realPath == null) {
            return null;
        }

        return new File(realPath);
    }

    private void loadNavbarData(
            Connection conn,
            HttpServletRequest request,
            HttpSession session,
            int userID
    ) {
        try {
            categorydao categoryDao = new categorydao(conn);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );
        } catch (Exception ignored) {
        }

        try {
            cartdao cartDao = new cartdao(conn);
            session.setAttribute(
                    "cartCount",
                    cartDao.getCartItemCount(userID)
            );
        } catch (Exception ignored) {
        }
    }

    private void updateSessionUser(
            HttpSession session,
            user profileUser
    ) {
        session.setAttribute("userID", profileUser.getUserID());
        session.setAttribute("username", profileUser.getUsername());
        session.setAttribute("fullName", profileUser.getFullName());
        session.setAttribute("userPhoto", profileUser.getUserPhoto());
        session.setAttribute("user", profileUser);
    }

    private Integer getLoggedInUserID(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userID = session.getAttribute("userID");

        if (userID instanceof Number) {
            return ((Number) userID).intValue();
        }

        Object sessionUser = session.getAttribute("user");

        if (sessionUser instanceof user) {
            return ((user) sessionUser).getUserID();
        }

        return null;
    }

    private String trimParameter(String value) {
        return value == null ? "" : value.trim();
    }

    private void forwardReloadedError(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpSession session,
            int userID,
            String errorMessage
    ) throws ServletException, IOException {

        try (Connection reloadConnection = getConnection()) {
            userdao dao = new userdao(reloadConnection);
            user profileUser = dao.getUserByID(userID);

            if (profileUser == null) {
                response.sendRedirect(
                        request.getContextPath() + "/auth?action=login"
                );
                return;
            }

            restorePhotoIfNeeded(profileUser.getUserPhoto());
            loadNavbarData(
                    reloadConnection,
                    request,
                    session,
                    userID
            );
            forwardEditError(
                    request,
                    response,
                    profileUser,
                    errorMessage
            );

        } catch (Exception secondException) {
            secondException.printStackTrace();
            response.sendRedirect(
                    request.getContextPath() + "/profile?action=view"
            );
        }
    }

    private void forwardEditError(
            HttpServletRequest request,
            HttpServletResponse response,
            user profileUser,
            String errorMessage
    ) throws ServletException, IOException {

        request.setAttribute("user", profileUser);
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher(
                "/user/editprofile.jsp"
        ).forward(request, response);
    }

    private void rollbackQuietly(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            conn.rollback();
        } catch (Exception ignored) {
        }
    }
}