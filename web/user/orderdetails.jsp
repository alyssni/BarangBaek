<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.barangbaek.bean.order"%>
<%@page import="com.barangbaek.bean.orderitem"%>
<%@page import="com.barangbaek.bean.payment"%>
<%@page import="com.barangbaek.bean.parcel"%>

<%
    order selectedOrder =
            (order) request.getAttribute("order");

    List<orderitem> orderItems =
            (List<orderitem>) request.getAttribute("orderItems");

    payment orderPayment =
            (payment) request.getAttribute("payment");

    parcel orderParcel =
            (parcel) request.getAttribute("parcel");

    String viewerType =
            (String) request.getAttribute("viewerType");

    boolean sellerView = "SELLER".equals(viewerType);

    SimpleDateFormat detailDateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Order Details | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/orders.css">
</head>

<body>

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="orders-page">

        <% if (selectedOrder == null) { %>
        <section class="orders-empty">
            <h2>Order not found</h2>
            <a href="${pageContext.request.contextPath}/order?action=myorders">
                Back to Purchases
            </a>
        </section>
        <% } else { %>

        <header class="orders-heading">
            <div>
                <span class="orders-eyebrow">
                    <%= sellerView ? "Sales order" : "Purchase details" %>
                </span>
                <h1>Order #<%= selectedOrder.getOrderID() %></h1>
                <p>
                    Placed on
                    <%= detailDateFormat.format(selectedOrder.getOrderDateTime()) %>
                </p>
            </div>

            <div class="order-heading-actions">
                <a href="${pageContext.request.contextPath}/payment?action=receipt&orderID=<%= selectedOrder.getOrderID() %>"
                   class="order-receipt-button">
                    <i class="fa-solid fa-receipt"></i>
                    View Receipt
                </a>

                <a href="${pageContext.request.contextPath}/order?action=<%= sellerView ? "sellerOrders" : "myorders" %>">
                    <i class="fa-solid fa-arrow-left"></i>
                    Back to Orders
                </a>
            </div>
        </header>

        <div class="order-detail-layout">
            <section class="order-detail-main">
                <section class="order-detail-card">
                    <div class="order-detail-card__heading">
                        <h2>Items</h2>
                        <span class="order-status">
                            <%= selectedOrder.getOrderStatus() %>
                        </span>
                    </div>

                    <% if (orderItems != null) {
                        for (orderitem currentItem : orderItems) { %>
                    <article class="order-detail-item">
                        <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= currentItem.getItemPhoto() %>"
                             alt="<%= currentItem.getItemName() %>"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">

                        <div>
                            <h3><%= currentItem.getItemName() %></h3>
                            <p>
                                Quantity: <%= currentItem.getQuantity() %>
                                &middot;
                                RM <%= String.format("%.2f", currentItem.getPriceAtPurchase()) %> each
                            </p>
                        </div>

                        <strong>
                            RM <%= String.format(
                                    "%.2f",
                                    currentItem.getQuantity()
                                    * currentItem.getPriceAtPurchase()
                            ) %>
                        </strong>
                    </article>
                    <%  }
                       } %>
                </section>
            </section>

            <aside class="order-detail-sidebar">
                <section class="order-detail-card">
                    <h2>Order Summary</h2>

                    <div class="order-info-line">
                        <span><%= sellerView ? "Buyer" : "Seller" %></span>
                        <strong>
                            <%= sellerView
                                    ? selectedOrder.getBuyerName()
                                    : selectedOrder.getSellerName() %>
                        </strong>
                    </div>

                    <div class="order-info-line order-info-line--total">
                        <span>Total</span>
                        <strong>
                            RM <%= String.format("%.2f", selectedOrder.getTotalAmount()) %>
                        </strong>
                    </div>
                </section>

                <section class="order-detail-card">
                    <h2>Payment</h2>

                    <div class="order-info-line">
                        <span>Method</span>
                        <strong>
                            <%= orderPayment != null
                                    ? orderPayment.getPaymentMethod()
                                    : "Not recorded" %>
                        </strong>
                    </div>

                    <div class="order-info-line">
                        <span>Status</span>
                        <strong>
                            <%= orderPayment != null
                                    ? orderPayment.getPaymentStatus()
                                    : "Pending" %>
                        </strong>
                    </div>

                    <div class="order-info-line">
                        <span>Payment reference</span>
                        <strong>
                            <%= orderPayment != null
                                    ? String.format(
                                            "BB-PAY-%06d",
                                            orderPayment.getPaymentID()
                                    )
                                    : "Not recorded" %>
                        </strong>
                    </div>

                    <div class="order-info-line">
                        <span>Recorded on</span>
                        <strong>
                            <%= orderPayment != null
                                    && orderPayment.getPaymentDateTime() != null
                                    ? detailDateFormat.format(
                                            orderPayment.getPaymentDateTime()
                                    )
                                    : "Not recorded" %>
                        </strong>
                    </div>
                </section>

                <section class="order-detail-card">
                    <h2>Delivery</h2>

                    <div class="order-info-line">
                        <span>Method</span>
                        <strong>
                            <%= orderParcel != null
                                    ? orderParcel.getDeliveryType()
                                    : "Not recorded" %>
                        </strong>
                    </div>

                    <% if (orderParcel != null
                            && orderParcel.getPickupLocation() != null
                            && !orderParcel.getPickupLocation().trim().isEmpty()) { %>
                    <div class="order-info-line">
                        <span>Pickup location</span>
                        <strong><%= orderParcel.getPickupLocation() %></strong>
                    </div>
                    <% } %>

                    <div class="order-info-line">
                        <span>Delivery status</span>
                        <strong>
                            <%= orderParcel != null
                                    ? orderParcel.getDeliveryStatus()
                                    : "Pending" %>
                        </strong>
                    </div>

                    <div class="order-info-line">
                        <span>Tracking number</span>
                        <strong>
                            <%= orderParcel != null
                                    && orderParcel.getTrackingNumber() != null
                                    ? orderParcel.getTrackingNumber()
                                    : "Not assigned yet" %>
                        </strong>
                    </div>
                </section>
            </aside>
        </div>

        <% } %>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>