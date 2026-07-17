package com.barangbaek.controller.user;

import com.barangbaek.bean.category;
import com.barangbaek.bean.order;
import com.barangbaek.bean.orderitem;
import com.barangbaek.bean.parcel;
import com.barangbaek.bean.payment;
import com.barangbaek.dao.categorydao;
import com.barangbaek.dao.orderdao;
import com.barangbaek.dao.parceldao;
import com.barangbaek.dao.paymentdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class orderservlet extends HttpServlet {

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

        if (action == null || "myorders".equals(action)) {
            showMyPurchases(request, response);
        } else if ("sellerOrders".equals(action)) {
            showSalesOrders(request, response);
        } else if ("details".equals(action)) {
            showOrderDetails(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/order?action=myorders"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/order?action=myorders"
        );
    }

    private void showMyPurchases(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            orderdao orderDao = new orderdao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<order> orders =
                    orderDao.getOrdersByBuyer(userID);

            List<category> categories =
                    categoryDao.getAllCategories();

            request.setAttribute("orders", orders);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/user/myorders.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Your purchases could not be loaded."
            );

            request.getRequestDispatcher(
                    "/user/myorders.jsp"
            ).forward(request, response);
        }
    }

    private void showSalesOrders(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            orderdao orderDao = new orderdao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<order> orders =
                    orderDao.getOrdersBySeller(userID);

            List<category> categories =
                    categoryDao.getAllCategories();

            request.setAttribute("orders", orders);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/user/sellerorders.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Your sales orders could not be loaded."
            );

            request.getRequestDispatcher(
                    "/user/sellerorders.jsp"
            ).forward(request, response);
        }
    }

    private void showOrderDetails(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);
        Integer orderID = parsePositiveInteger(
                request.getParameter("orderId")
        );

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (orderID == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/order?action=myorders"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            orderdao orderDao = new orderdao(conn);
            paymentdao paymentDao = new paymentdao(conn);
            parceldao parcelDao = new parceldao(conn);
            categorydao categoryDao = new categorydao(conn);

            order selectedOrder =
                    orderDao.getOrderById(orderID);

            if (selectedOrder == null
                    || (selectedOrder.getBuyerID() != userID
                    && selectedOrder.getSellerID() != userID)) {

                response.sendRedirect(
                        request.getContextPath()
                        + "/order?action=myorders"
                        + "&error=unauthorized"
                );
                return;
            }

            List<orderitem> orderItems =
                    orderDao.getOrderItemsByOrder(orderID);

            payment orderPayment =
                    paymentDao.getPaymentByOrder(orderID);

            parcel orderParcel =
                    parcelDao.getParcelByOrder(orderID);

            List<category> categories =
                    categoryDao.getAllCategories();

            String viewerType =
                    selectedOrder.getBuyerID() == userID
                    ? "BUYER"
                    : "SELLER";

            request.setAttribute("order", selectedOrder);
            request.setAttribute("orderItems", orderItems);
            request.setAttribute("payment", orderPayment);
            request.setAttribute("parcel", orderParcel);
            request.setAttribute("viewerType", viewerType);
            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/user/orderdetails.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            response.sendRedirect(
                    request.getContextPath()
                    + "/order?action=myorders"
            );
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