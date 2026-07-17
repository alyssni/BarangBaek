package com.barangbaek.controller.user;

import com.barangbaek.bean.category;
import com.barangbaek.bean.parcel;
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

public class parcelservlet extends HttpServlet {

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

        if (action == null || "track".equals(action)
                || "show".equals(action)) {
            showTrackingPage(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/parcel?action=track"
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateDelivery(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/parcel?action=track"
            );
        }
    }

    private void showTrackingPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        Integer userID = getLoggedInUserID(request);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        try (Connection conn = getConnection()) {
            parceldao parcelDao = new parceldao(conn);
            categorydao categoryDao = new categorydao(conn);

            List<parcel> buyerParcels =
                    parcelDao.getParcelsByBuyer(userID);

            List<parcel> sellerParcels =
                    parcelDao.getParcelsBySeller(userID);

            List<category> categories =
                    categoryDao.getAllCategories();

            request.setAttribute("buyerParcels", buyerParcels);
            request.setAttribute("sellerParcels", sellerParcels);
            request.setAttribute("categories", categories);

            if ("true".equals(request.getParameter("updated"))) {
                request.setAttribute(
                        "success",
                        "Delivery information updated successfully."
                );
            }

            String errorCode = request.getParameter("error");

            if ("invalid".equals(errorCode)) {
                request.setAttribute(
                        "error",
                        "The selected delivery update is invalid."
                );
            } else if ("trackingRequired".equals(errorCode)) {
                request.setAttribute(
                        "error",
                        "Enter a tracking number before marking "
                        + "a courier parcel as shipped."
                );
            } else if ("unauthorized".equals(errorCode)) {
                request.setAttribute(
                        "error",
                        "You may update only your own sales deliveries."
                );
            }

            request.getRequestDispatcher(
                    "/user/parcel.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Parcel information could not be loaded: "
                    + e.getMessage()
            );

            request.getRequestDispatcher(
                    "/user/parcel.jsp"
            ).forward(request, response);
        }
    }

    private void updateDelivery(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {

        Integer sellerID = getLoggedInUserID(request);
        Integer parcelID = parsePositiveInteger(
                request.getParameter("parcelID")
        );

        String deliveryStatus = trimParameter(
                request.getParameter("deliveryStatus")
        );

        String trackingNumber = trimParameter(
                request.getParameter("trackingNumber")
        );

        if (sellerID == null) {
            redirectToLogin(request, response);
            return;
        }

        if (parcelID == null || deliveryStatus.isEmpty()) {
            redirectWithError(request, response, "invalid");
            return;
        }

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            parceldao parcelDao = new parceldao(conn);
            orderdao orderDao = new orderdao(conn);
            paymentdao paymentDao = new paymentdao(conn);

            parcel selectedParcel =
                    parcelDao.getParcelByID(parcelID);

            if (selectedParcel == null
                    || selectedParcel.getSellerID() != sellerID) {
                conn.rollback();
                redirectWithError(
                        request,
                        response,
                        "unauthorized"
                );
                return;
            }

            if (!isValidStatus(
                    selectedParcel.getDeliveryType(),
                    deliveryStatus
            )) {
                conn.rollback();
                redirectWithError(request, response, "invalid");
                return;
            }

            boolean courierDelivery =
                    "Courier Delivery".equals(
                            selectedParcel.getDeliveryType()
                    );

            boolean trackingRequired =
                    courierDelivery
                    && ("Shipped".equals(deliveryStatus)
                    || "Out for Delivery".equals(deliveryStatus)
                    || "Delivered".equals(deliveryStatus));

            if (trackingNumber.isEmpty()) {
                trackingNumber = selectedParcel.getTrackingNumber();
            }

            if (trackingRequired
                    && (trackingNumber == null
                    || trackingNumber.trim().isEmpty())) {
                conn.rollback();
                redirectWithError(
                        request,
                        response,
                        "trackingRequired"
                );
                return;
            }

            if (!courierDelivery) {
                trackingNumber = null;
            }

            boolean updated =
                    parcelDao.updateParcelBySeller(
                            parcelID,
                            sellerID,
                            trackingNumber,
                            deliveryStatus
                    );

            if (!updated) {
                throw new Exception(
                        "Delivery record could not be updated."
                );
            }

            String orderStatus =
                    convertToOrderStatus(deliveryStatus);

            orderDao.updateOrderStatus(
                    selectedParcel.getOrderID(),
                    orderStatus
            );

            if ("Collected".equals(deliveryStatus)) {
                paymentDao.markCashOnPickupAsPaid(
                        selectedParcel.getOrderID()
                );
            }

            conn.commit();

            response.sendRedirect(
                    request.getContextPath()
                    + "/parcel?action=track&updated=true"
            );

        } catch (Exception e) {
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
            }

            redirectWithError(request, response, "invalid");

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

    private boolean isValidStatus(
            String deliveryType,
            String deliveryStatus
    ) {

        if ("Courier Delivery".equals(deliveryType)) {
            return "Preparing".equals(deliveryStatus)
                    || "Packed".equals(deliveryStatus)
                    || "Shipped".equals(deliveryStatus)
                    || "Out for Delivery".equals(deliveryStatus)
                    || "Delivered".equals(deliveryStatus);
        }

        if ("Campus Pickup".equals(deliveryType)) {
            return "Preparing for Pickup".equals(deliveryStatus)
                    || "Ready for Pickup".equals(deliveryStatus)
                    || "Collected".equals(deliveryStatus);
        }

        return false;
    }

    private String convertToOrderStatus(String deliveryStatus) {
        if ("Delivered".equals(deliveryStatus)
                || "Collected".equals(deliveryStatus)) {
            return "Completed";
        }

        if ("Shipped".equals(deliveryStatus)
                || "Out for Delivery".equals(deliveryStatus)
                || "Ready for Pickup".equals(deliveryStatus)) {
            return "In Progress";
        }

        return "Processing";
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

    private String trimParameter(String value) {
        return value == null ? "" : value.trim();
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

    private void redirectWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String errorCode
    ) throws IOException {

        response.sendRedirect(
                request.getContextPath()
                + "/parcel?action=track&error="
                + errorCode
        );
    }
}