package com.barangbaek.controller.user;

import com.barangbaek.bean.cartitem;
import com.barangbaek.bean.checkoutgroup;
import com.barangbaek.bean.order;
import com.barangbaek.bean.orderitem;
import com.barangbaek.bean.parcel;
import com.barangbaek.bean.payment;
import com.barangbaek.bean.user;
import com.barangbaek.dao.cartdao;
import com.barangbaek.dao.itemdao;
import com.barangbaek.dao.orderdao;
import com.barangbaek.dao.parceldao;
import com.barangbaek.dao.paymentdao;
import com.barangbaek.dao.userdao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class paymentservlet extends HttpServlet {

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

        if (action == null || "checkout".equals(action)) {
            showCheckout(request, response);
        } else if ("success".equals(action)) {
            showPaymentSuccess(request, response);
        } else if ("receipt".equals(action)) {
            showReceipt(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cart?action=view"
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

        if ("process".equals(action)) {
            processCheckout(request, response);
        } else {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cart?action=view"
            );
        }
    }

    private void showCheckout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        List<Integer> selectedIDs = getCheckoutIDs(session);

        if (selectedIDs == null || selectedIDs.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cart?action=view&result=selectItems"
            );
            return;
        }

        try (Connection conn = getConnection()) {
            cartdao cartDao = new cartdao(conn);
            userdao userDao = new userdao(conn);

            List<cartitem> selectedItems =
                    cartDao.getCartItemsByIDs(userID, selectedIDs);

            if (!isValidCheckoutSelection(
                    selectedItems,
                    selectedIDs,
                    userID
            )) {

                session.removeAttribute("checkoutCartItemIDs");

                response.sendRedirect(
                        request.getContextPath()
                        + "/cart?action=view"
                        + "&result=checkoutUnavailable"
                );
                return;
            }

            List<checkoutgroup> checkoutGroups =
                    createCheckoutGroups(selectedItems);

            double grandTotal = calculateGrandTotal(selectedItems);
            int totalQuantity = calculateTotalQuantity(selectedItems);

            user buyer = userDao.getUserByID(userID);

            request.setAttribute("checkoutGroups", checkoutGroups);
            request.setAttribute("grandTotal", grandTotal);
            request.setAttribute("totalQuantity", totalQuantity);
            request.setAttribute("buyer", buyer);

            request.getRequestDispatcher(
                    "/user/checkout.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "error",
                    "Checkout could not be loaded."
            );

            request.getRequestDispatcher(
                    "/user/checkout.jsp"
            ).forward(request, response);
        }
    }

    private void processCheckout(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        List<Integer> selectedIDs = getCheckoutIDs(session);

        if (selectedIDs == null || selectedIDs.isEmpty()) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cart?action=view&result=selectItems"
            );
            return;
        }

        String paymentMethod = trim(
                request.getParameter("paymentMethod")
        );

        String paymentProvider = trim(
                request.getParameter("paymentProvider")
        );

        String deliveryType = trim(
                request.getParameter("deliveryType")
        );

        String pickupLocation = trim(
                request.getParameter("pickupLocation")
        );

        request.setAttribute(
                "selectedPaymentMethod",
                paymentMethod
        );
        request.setAttribute(
                "selectedPaymentProvider",
                paymentProvider
        );
        request.setAttribute(
                "selectedDeliveryType",
                deliveryType
        );
        request.setAttribute(
                "selectedPickupLocation",
                pickupLocation
        );

        String validationError = validateCheckoutOptions(
                paymentMethod,
                paymentProvider,
                deliveryType,
                pickupLocation
        );

        if (validationError != null) {
            request.setAttribute("error", validationError);
            showCheckout(request, response);
            return;
        }

        String paymentDescription = buildPaymentDescription(
                paymentMethod,
                paymentProvider
        );

        Connection conn = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            cartdao cartDao = new cartdao(conn);
            itemdao itemDao = new itemdao(conn);
            orderdao orderDao = new orderdao(conn);
            paymentdao paymentDao = new paymentdao(conn);
            parceldao parcelDao = new parceldao(conn);

            List<cartitem> selectedItems =
                    cartDao.getCartItemsByIDs(userID, selectedIDs);

            if (!isValidCheckoutSelection(
                    selectedItems,
                    selectedIDs,
                    userID
            )) {

                conn.rollback();
                session.removeAttribute("checkoutCartItemIDs");

                response.sendRedirect(
                        request.getContextPath()
                        + "/cart?action=view"
                        + "&result=checkoutUnavailable"
                );
                return;
            }

            List<checkoutgroup> groups =
                    createCheckoutGroups(selectedItems);

            List<Integer> createdOrderIDs =
                    new ArrayList<Integer>();

            for (checkoutgroup group : groups) {
                boolean paidImmediately =
                        !"Cash on Pickup".equals(paymentMethod);

                order newOrder = new order();
                newOrder.setBuyerID(userID);
                newOrder.setSellerID(group.getSellerID());
                newOrder.setTotalAmount(group.getGroupTotal());
                newOrder.setOrderStatus(
                        paidImmediately ? "Paid" : "Confirmed"
                );

                int orderID = orderDao.createOrder(newOrder);

                if (orderID <= 0) {
                    throw new Exception("Order could not be created.");
                }

                for (cartitem selectedItem : group.getItems()) {
                    boolean stockReduced =
                            itemDao.reduceStockForPurchase(
                                    selectedItem.getItemID(),
                                    selectedItem.getQuantity()
                            );

                    if (!stockReduced) {
                        throw new Exception(
                                "The stock for "
                                + selectedItem.getItemName()
                                + " changed during checkout."
                        );
                    }

                    orderitem newOrderItem = new orderitem();
                    newOrderItem.setOrderID(orderID);
                    newOrderItem.setItemID(
                            selectedItem.getItemID()
                    );
                    newOrderItem.setQuantity(
                            selectedItem.getQuantity()
                    );
                    newOrderItem.setPriceAtPurchase(
                            selectedItem.getPrice()
                    );

                    if (!orderDao.addOrderItem(newOrderItem)) {
                        throw new Exception(
                                "An order item could not be saved."
                        );
                    }
                }

                payment newPayment = new payment();
                newPayment.setOrderID(orderID);
                newPayment.setPaymentMethod(paymentDescription);
                newPayment.setPaymentStatus(
                        paidImmediately ? "Paid" : "Pending"
                );

                if (paymentDao.createPayment(newPayment) <= 0) {
                    throw new Exception(
                            "Payment could not be recorded."
                    );
                }

                parcel newParcel = new parcel();
                newParcel.setOrderID(orderID);
                newParcel.setDeliveryType(deliveryType);
                newParcel.setPickupLocation(
                        "Campus Pickup".equals(deliveryType)
                        ? pickupLocation
                        : null
                );
                newParcel.setTrackingNumber(null);
                newParcel.setDeliveryStatus(
                        "Campus Pickup".equals(deliveryType)
                        ? "Preparing for Pickup"
                        : "Preparing"
                );

                if (parcelDao.createParcel(newParcel) <= 0) {
                    throw new Exception(
                            "Delivery record could not be created."
                    );
                }

                createdOrderIDs.add(orderID);
            }

            cartDao.removeCartItems(userID, selectedIDs);

            conn.commit();

            session.removeAttribute("checkoutCartItemIDs");
            session.setAttribute(
                    "cartCount",
                    cartDao.getCartItemCount(userID)
            );
            session.setAttribute(
                    "lastOrderIDs",
                    createdOrderIDs
            );
            session.setAttribute(
                    "lastCheckoutTotal",
                    calculateGrandTotal(selectedItems)
            );
            session.setAttribute(
                    "lastCheckoutQuantity",
                    calculateTotalQuantity(selectedItems)
            );
            session.setAttribute(
                    "lastPaymentMethod",
                    paymentDescription
            );
            session.setAttribute(
                    "lastPaymentStatus",
                    "Cash on Pickup".equals(paymentMethod)
                    ? "Pending"
                    : "Paid"
            );
            session.setAttribute(
                    "lastDeliveryType",
                    deliveryType
            );

            response.sendRedirect(
                    request.getContextPath()
                    + "/payment?action=success"
            );

        } catch (Exception e) {
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception ignored) {
                }
            }

            request.setAttribute(
                    "error",
                    e.getMessage() != null
                    ? e.getMessage()
                    : "Payment could not be completed."
            );

            showCheckout(request, response);

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

    private void showPaymentSuccess(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        Object orderIDs = session.getAttribute("lastOrderIDs");

        if (orderIDs == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/order?action=myorders"
            );
            return;
        }

        request.getRequestDispatcher(
                "/user/paymentsuccess.jsp"
        ).forward(request, response);
    }

    private void showReceipt(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        Integer userID = getLoggedInUserID(session);

        if (userID == null) {
            redirectToLogin(request, response);
            return;
        }

        int orderID = parsePositiveInt(
                request.getParameter("orderID")
        );

        if (orderID <= 0) {
            orderID = parsePositiveInt(
                    request.getParameter("orderId")
            );
        }

        if (orderID <= 0) {
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

            order receiptOrder = orderDao.getOrderById(orderID);

            if (receiptOrder == null
                    || (receiptOrder.getBuyerID() != userID
                    && receiptOrder.getSellerID() != userID)) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not authorised to view this receipt."
                );
                return;
            }

            List<orderitem> receiptItems =
                    orderDao.getOrderItemsByOrder(orderID);

            payment receiptPayment =
                    paymentDao.getPaymentByOrder(orderID);

            parcel receiptParcel =
                    parcelDao.getParcelByOrder(orderID);

            request.setAttribute("receiptOrder", receiptOrder);
            request.setAttribute("receiptItems", receiptItems);
            request.setAttribute("receiptPayment", receiptPayment);
            request.setAttribute("receiptParcel", receiptParcel);
            request.setAttribute(
                    "receiptViewerType",
                    receiptOrder.getSellerID() == userID
                    ? "SELLER"
                    : "BUYER"
            );

            request.getRequestDispatcher(
                    "/user/receipt.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            request.setAttribute(
                    "receiptError",
                    "The receipt could not be loaded."
            );

            request.getRequestDispatcher(
                    "/user/receipt.jsp"
            ).forward(request, response);
        }
    }

    private List<checkoutgroup> createCheckoutGroups(
            List<cartitem> selectedItems
    ) {

        Map<Integer, checkoutgroup> grouped =
                new LinkedHashMap<Integer, checkoutgroup>();

        for (cartitem selectedItem : selectedItems) {
            checkoutgroup group = grouped.get(
                    selectedItem.getSellerID()
            );

            if (group == null) {
                group = new checkoutgroup();
                group.setSellerID(selectedItem.getSellerID());
                group.setSellerName(selectedItem.getSellerName());
                grouped.put(selectedItem.getSellerID(), group);
            }

            group.addItem(selectedItem);
        }

        return new ArrayList<checkoutgroup>(grouped.values());
    }

    private boolean isValidCheckoutSelection(
            List<cartitem> selectedItems,
            List<Integer> selectedIDs,
            int userID
    ) {

        if (selectedItems == null
                || selectedIDs == null
                || selectedItems.size() != selectedIDs.size()) {
            return false;
        }

        for (cartitem selectedItem : selectedItems) {
            if (selectedItem == null
                    || !selectedItem.isAvailable()
                    || selectedItem.getQuantity() <= 0
                    || selectedItem.getQuantity()
                    > selectedItem.getStock()
                    || selectedItem.getSellerID() == userID) {
                return false;
            }
        }

        return true;
    }

    private String validateCheckoutOptions(
            String paymentMethod,
            String paymentProvider,
            String deliveryType,
            String pickupLocation
    ) {

        if (!"Online Banking".equals(paymentMethod)
                && !"Debit/Credit Card".equals(paymentMethod)
                && !"Cash on Pickup".equals(paymentMethod)) {

            return "Please select a valid payment method.";
        }

        if (("Online Banking".equals(paymentMethod)
                || "Debit/Credit Card".equals(paymentMethod))
                && paymentProvider.isEmpty()) {

            return "Please select a bank or card type.";
        }

        if (!"Courier Delivery".equals(deliveryType)
                && !"Campus Pickup".equals(deliveryType)) {

            return "Please select a delivery method.";
        }

        if ("Cash on Pickup".equals(paymentMethod)
                && !"Campus Pickup".equals(deliveryType)) {

            return "Cash on Pickup is only available for Campus Pickup.";
        }

        if ("Campus Pickup".equals(deliveryType)
                && pickupLocation.isEmpty()) {

            return "Please select a campus pickup location.";
        }

        return null;
    }

    private String buildPaymentDescription(
            String paymentMethod,
            String paymentProvider
    ) {

        if (paymentProvider == null
                || paymentProvider.trim().isEmpty()) {
            return paymentMethod;
        }

        return paymentMethod + " - " + paymentProvider.trim();
    }

    private int parsePositiveInt(String value) {
        try {
            int number = Integer.parseInt(value);
            return number > 0 ? number : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    private double calculateGrandTotal(
            List<cartitem> selectedItems
    ) {

        double total = 0.0;

        for (cartitem selectedItem : selectedItems) {
            total += selectedItem.getSubtotal();
        }

        return total;
    }

    private int calculateTotalQuantity(
            List<cartitem> selectedItems
    ) {

        int quantity = 0;

        for (cartitem selectedItem : selectedItems) {
            quantity += selectedItem.getQuantity();
        }

        return quantity;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> getCheckoutIDs(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(
                "checkoutCartItemIDs"
        );

        if (value instanceof List<?>) {
            return (List<Integer>) value;
        }

        return null;
    }

    private Integer getLoggedInUserID(HttpSession session) {
        if (session == null) {
            return null;
        }

        Object userID = session.getAttribute("userID");

        if (userID instanceof Integer) {
            return (Integer) userID;
        }

        return null;
    }

    private String trim(String value) {
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
}