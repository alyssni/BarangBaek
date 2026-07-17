package com.barangbaek.controller.user;

import com.barangbaek.bean.cartitem;
import com.barangbaek.bean.category;
import com.barangbaek.bean.item;
import com.barangbaek.dao.cartdao;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.itemdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class cartservlet extends HttpServlet {

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

        String action = request.getParameter("action");

        if (action == null || "view".equals(action)) {
            showCart(request, response);
            return;
        }

        response.sendRedirect(
                request.getContextPath() + "/cart?action=view"
        );
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            addToCart(request, response);
        } else if ("update".equals(action)) {
            updateQuantity(request, response);
        } else if ("remove".equals(action)) {
            removeItem(request, response);
        } else if ("clear".equals(action)) {
            clearCart(request, response);
        } else if ("prepareCheckout".equals(action)) {
            prepareCheckout(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath() + "/cart?action=view"
            );
        }
    }

    private void showCart(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            cartdao cartDao = new cartdao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<cartitem> cartItems =
                    cartDao.getCartItems(userID);

            List<category> categories =
                    categoryDao.getAllCategories();

            double cartTotal = 0.0;

            for (cartitem c : cartItems) {
                if (c.isAvailable()
                        && c.getQuantity() <= c.getStock()) {
                    cartTotal += c.getSubtotal();
                }
            }

            updateSessionCartCount(
                    request.getSession(),
                    cartDao,
                    userID
            );

            request.setAttribute("cartItems", cartItems);
            request.setAttribute("cartTotal", cartTotal);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/user/cart.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Your cart could not be loaded."
            );

            request.getRequestDispatcher(
                    "/user/cart.jsp"
            ).forward(request, response);
        }
    }

    private void addToCart(
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

        Integer quantity = parsePositiveInteger(
                request.getParameter("quantity")
        );

        String source = request.getParameter("source");

        if (itemID == null || quantity == null) {
            redirectAfterAdd(
                    request,
                    response,
                    source,
                    itemID,
                    "quantity"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            itemdao itemDao = new itemdao(conn);
            cartdao cartDao = new cartdao(conn);

            item selectedItem = itemDao.getItemById(itemID);

            if (selectedItem == null) {
                redirectAfterAdd(
                        request,
                        response,
                        source,
                        itemID,
                        "missing"
                );
                return;
            }

            if (selectedItem.getSellerID() == userID) {
                redirectAfterAdd(
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

                redirectAfterAdd(
                        request,
                        response,
                        source,
                        itemID,
                        "unavailable"
                );
                return;
            }

            int existingQuantity =
                    cartDao.getCartQuantity(userID, itemID);

            if (existingQuantity + quantity
                    > selectedItem.getStock()) {

                redirectAfterAdd(
                        request,
                        response,
                        source,
                        itemID,
                        "stock"
                );
                return;
            }

            cartDao.addOrUpdateCartItem(
                    userID,
                    itemID,
                    quantity
            );

            updateSessionCartCount(
                    request.getSession(),
                    cartDao,
                    userID
            );

            redirectAfterAdd(
                    request,
                    response,
                    source,
                    itemID,
                    "added"
            );

        } catch (Exception e) {
            e.printStackTrace();

            redirectAfterAdd(
                    request,
                    response,
                    source,
                    itemID,
                    "system"
            );
        }
    }

    private void updateQuantity(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        Integer cartItemID = parsePositiveInteger(
                request.getParameter("cartItemID")
        );

        Integer quantity = parsePositiveInteger(
                request.getParameter("quantity")
        );

        if (cartItemID == null || quantity == null) {
            redirectCart(response, request, "invalidQuantity");
            return;
        }

        try (Connection conn = getConnection()) {
            cartdao dao = new cartdao(conn);

            cartitem selectedCartItem =
                    dao.getCartItemByID(cartItemID, userID);

            if (selectedCartItem == null) {
                redirectCart(response, request, "missing");
                return;
            }

            if (!selectedCartItem.isAvailable()) {
                redirectCart(response, request, "unavailable");
                return;
            }

            if (quantity > selectedCartItem.getStock()) {
                redirectCart(response, request, "stock");
                return;
            }

            dao.updateQuantity(
                    cartItemID,
                    userID,
                    quantity
            );

            updateSessionCartCount(
                    request.getSession(),
                    dao,
                    userID
            );

            redirectCart(response, request, "updated");

        } catch (Exception e) {
            e.printStackTrace();
            redirectCart(response, request, "system");
        }
    }

    private void removeItem(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        Integer cartItemID = parsePositiveInteger(
                request.getParameter("cartItemID")
        );

        if (cartItemID == null) {
            redirectCart(response, request, "missing");
            return;
        }

        try (Connection conn = getConnection()) {
            cartdao dao = new cartdao(conn);

            dao.removeCartItem(cartItemID, userID);

            updateSessionCartCount(
                    request.getSession(),
                    dao,
                    userID
            );

            redirectCart(response, request, "removed");

        } catch (Exception e) {
            e.printStackTrace();
            redirectCart(response, request, "system");
        }
    }

    private void clearCart(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            cartdao dao = new cartdao(conn);

            dao.clearCart(userID);

            request.getSession().setAttribute(
                    "cartCount",
                    0
            );

            redirectCart(response, request, "cleared");

        } catch (Exception e) {
            e.printStackTrace();
            redirectCart(response, request, "system");
        }
    }

    private void prepareCheckout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        String[] selectedValues =
                request.getParameterValues("cartItemID");

        if (selectedValues == null
                || selectedValues.length == 0) {

            redirectCart(response, request, "selectItems");
            return;
        }

        List<Integer> selectedIDs =
                new ArrayList<Integer>();

        try (Connection conn = getConnection()) {
            cartdao dao = new cartdao(conn);

            for (String selectedValue : selectedValues) {
                Integer cartItemID =
                        parsePositiveInteger(selectedValue);

                if (cartItemID == null) {
                    redirectCart(
                            response,
                            request,
                            "invalidSelection"
                    );
                    return;
                }

                cartitem selectedItem =
                        dao.getCartItemByID(cartItemID, userID);

                if (selectedItem == null
                        || !selectedItem.isAvailable()
                        || selectedItem.getQuantity()
                        > selectedItem.getStock()
                        || selectedItem.getSellerID() == userID) {

                    redirectCart(
                            response,
                            request,
                            "checkoutUnavailable"
                    );
                    return;
                }

                selectedIDs.add(cartItemID);
            }

            request.getSession().setAttribute(
                    "checkoutCartItemIDs",
                    selectedIDs
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/payment?action=checkout"
            );

        } catch (Exception e) {
            e.printStackTrace();
            redirectCart(response, request, "system");
        }
    }

    private Integer getLoggedInUserID(
            HttpServletRequest request
    ) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object userID = session.getAttribute("userID");

        if (userID instanceof Integer) {
            return (Integer) userID;
        }

        return null;
    }

    private void updateSessionCartCount(
            HttpSession session,
            cartdao dao,
            int userID
    ) throws Exception {

        session.setAttribute(
                "cartCount",
                dao.getCartItemCount(userID)
        );
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

    private void redirectAfterAdd(
            HttpServletRequest request,
            HttpServletResponse response,
            String source,
            Integer itemID,
            String result
    ) throws IOException {

        if ("details".equals(source)
                && itemID != null) {

            response.sendRedirect(
                    request.getContextPath()
                    + "/item?action=details&id="
                    + itemID
                    + "&cart="
                    + result
            );

            return;
        }

        if ("wishlist".equals(source)) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/wishlist?action=view&cart="
                    + result
            );
            return;
        }

        response.sendRedirect(
                request.getContextPath()
                + "/item?action=dashboard&cart="
                + result
        );
    }

    private void redirectCart(
            HttpServletResponse response,
            HttpServletRequest request,
            String result
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/cart?action=view&result="
                + result
        );
    }

    private void redirectToLogin(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/auth?action=login"
        );
    }
}