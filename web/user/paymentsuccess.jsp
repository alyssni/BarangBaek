<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>

<%
    List<Integer> orderIDs =
            (List<Integer>) session.getAttribute("lastOrderIDs");

    Double checkoutTotalValue =
            (Double) session.getAttribute("lastCheckoutTotal");

    Integer checkoutQuantityValue =
            (Integer) session.getAttribute("lastCheckoutQuantity");

    String paymentMethod =
            (String) session.getAttribute("lastPaymentMethod");

    String deliveryType =
            (String) session.getAttribute("lastDeliveryType");

    String paymentStatus =
            (String) session.getAttribute("lastPaymentStatus");

    double checkoutTotal =
            checkoutTotalValue != null ? checkoutTotalValue : 0.0;

    int checkoutQuantity =
            checkoutQuantityValue != null ? checkoutQuantityValue : 0;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Payment Successful | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/checkout.css">
</head>

<body>

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="payment-success-page">
        <section class="payment-success-card">
            <div class="payment-success-icon">
                <i class="fa-solid fa-check"></i>
            </div>

            <span class="checkout-eyebrow">Order confirmed</span>
            <h1>Your order has been placed!</h1>

            <p class="payment-success-intro">
                Your selected items have been converted into seller orders
                and removed from the cart.
            </p>

            <div class="payment-success-summary">
                <div>
                    <span>Order total</span>
                    <strong>RM <%= String.format("%.2f", checkoutTotal) %></strong>
                </div>

                <div>
                    <span>Item quantity</span>
                    <strong><%= checkoutQuantity %></strong>
                </div>

                <div>
                    <span>Payment</span>
                    <strong><%= paymentMethod != null ? paymentMethod : "-" %></strong>
                </div>

                <div>
                    <span>Payment status</span>
                    <strong><%= paymentStatus != null ? paymentStatus : "-" %></strong>
                </div>

                <div>
                    <span>Delivery</span>
                    <strong><%= deliveryType != null ? deliveryType : "-" %></strong>
                </div>
            </div>

            <div class="created-order-list">
                <h2>Created Orders</h2>

                <% if (orderIDs != null && !orderIDs.isEmpty()) {
                    for (Integer orderID : orderIDs) { %>
                <div class="created-order-row">
                    <div>
                        <span>Order #<%= orderID %></span>
                        <small>Receipt available for printing or PDF saving</small>
                    </div>

                    <div class="created-order-actions">
                        <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= orderID %>">
                            View Order
                        </a>

                        <a href="${pageContext.request.contextPath}/payment?action=receipt&orderID=<%= orderID %>"
                           class="created-order-receipt">
                            <i class="fa-solid fa-receipt"></i>
                            Receipt
                        </a>
                    </div>
                </div>
                <%  }
                   } %>
            </div>

            <div class="payment-success-actions">
                <a href="${pageContext.request.contextPath}/order?action=myorders"
                   class="payment-success-primary">
                    View My Purchases
                </a>

                <a href="${pageContext.request.contextPath}/item?action=dashboard"
                   class="payment-success-secondary">
                    Continue Shopping
                </a>
            </div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>