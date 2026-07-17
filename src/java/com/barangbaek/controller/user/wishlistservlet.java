package com.barangbaek.controller.user;

import com.barangbaek.bean.item;
import com.barangbaek.dao.cartdao;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.itemdao;
import com.barangbaek.dao.wishlistdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class wishlistservlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

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
            showWishlist(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/wishlist?action=view"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if ("toggle".equals(action)) {
            toggleWishlist(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/wishlist?action=view"
        );
    }

    private void showWishlist(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            wishlistdao wishlistDao = new wishlistdao(conn);
            cartdao cartDao = new cartdao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<item> items = wishlistDao.getWishlistItems(userID);
            int wishlistCount = wishlistDao.countWishlistItems(userID);

            request.setAttribute("items", items);
            request.setAttribute(
                    "categories",
                    categoryDao.getAllCategories()
            );

            request.getSession().setAttribute(
                    "wishlistCount",
                    wishlistCount
            );

            request.getSession().setAttribute(
                    "cartCount",
                    cartDao.getCartItemCount(userID)
            );

            request.getRequestDispatcher(
                    "/user/wishlist.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(
                    "error",
                    "Your wishlist could not be loaded."
            );
            request.getRequestDispatcher(
                    "/user/wishlist.jsp"
            ).forward(request, response);
        }
    }

    private void toggleWishlist(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        Integer itemID = parsePositiveInteger(
                request.getParameter("itemID")
        );

        String source = clean(request.getParameter("source"));

        if (itemID == null) {
            redirectAfterToggle(
                    request,
                    response,
                    source,
                    itemID,
                    "missing"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            wishlistdao wishlistDao = new wishlistdao(conn);
            itemdao itemDao = new itemdao(conn);

            item selectedItem = itemDao.getItemById(itemID);

            if (selectedItem == null) {
                redirectAfterToggle(
                        request,
                        response,
                        source,
                        itemID,
                        "missing"
                );
                return;
            }

            String result;

            if (wishlistDao.isWishlisted(userID, itemID)) {
                wishlistDao.removeFromWishlist(userID, itemID);
                result = "removed";

            } else {
                if (selectedItem.getSellerID() == userID) {
                    redirectAfterToggle(
                            request,
                            response,
                            source,
                            itemID,
                            "ownItem"
                    );
                    return;
                }

                if (!"Available".equalsIgnoreCase(
                        selectedItem.getItemStatus()
                ) || selectedItem.getStock() <= 0) {

                    redirectAfterToggle(
                            request,
                            response,
                            source,
                            itemID,
                            "unavailable"
                    );
                    return;
                }

                wishlistDao.addToWishlist(userID, itemID);
                result = "added";
            }

            request.getSession().setAttribute(
                    "wishlistCount",
                    wishlistDao.countWishlistItems(userID)
            );

            redirectAfterToggle(
                    request,
                    response,
                    source,
                    itemID,
                    result
            );

        } catch (Exception e) {
            e.printStackTrace();
            redirectAfterToggle(
                    request,
                    response,
                    source,
                    itemID,
                    "system"
            );
        }
    }

    private void redirectAfterToggle(
            HttpServletRequest request,
            HttpServletResponse response,
            String source,
            Integer itemID,
            String result
    ) throws IOException {

        if ("details".equals(source) && itemID != null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=details&id="
                    + itemID
                    + "&wishlist="
                    + result
            );
            return;
        }

        if ("wishlist".equals(source)) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/wishlist?action=view&result="
                    + result
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/item?action=dashboard&wishlist="
                + result
        );
    }

    private Integer getLoggedInUserID(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null
                || !(session.getAttribute("userID") instanceof Integer)) {
            return null;
        }

        return (Integer) session.getAttribute("userID");
    }

    private Integer parsePositiveInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        response.sendRedirect(
                request.getContextPath() + "/auth?action=login"
        );
    }
}