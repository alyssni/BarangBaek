<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.barangbaek.bean.order"%>

<%
    List<order> orders =
            (List<order>) request.getAttribute("orders");

    SimpleDateFormat orderDateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Purchases | BarangBaek</title>

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
        <header class="orders-heading">
            <div>
                <span class="orders-eyebrow">Buyer workspace</span>
                <h1>My Purchases</h1>
                <p>View every order that you placed with marketplace sellers.</p>
            </div>

            <a href="${pageContext.request.contextPath}/item?action=dashboard">
                <i class="fa-solid fa-bag-shopping"></i>
                Continue Shopping
            </a>
        </header>

        <% if ("unauthorized".equals(error)) { %>
        <div class="orders-message orders-message--error">
            You are not authorised to view that order.
        </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="orders-message orders-message--error">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (orders == null || orders.isEmpty()) { %>
        <section class="orders-empty">
            <i class="fa-solid fa-bag-shopping"></i>
            <h2>No purchases yet</h2>
            <p>Your completed checkouts will appear here.</p>
            <a href="${pageContext.request.contextPath}/item?action=dashboard">
                Browse Marketplace
            </a>
        </section>
        <% } else { %>

        <section class="orders-list">
            <% for (order currentOrder : orders) { %>
            <article class="order-card">
                <div class="order-card__top">
                    <div>
                        <span>Order #<%= currentOrder.getOrderID() %></span>
                        <strong><%= currentOrder.getSellerName() %></strong>
                    </div>

                    <span class="order-status">
                        <%= currentOrder.getOrderStatus() %>
                    </span>
                </div>

                <div class="order-card__details">
                    <div>
                        <span>Order date</span>
                        <strong>
                            <%= orderDateFormat.format(currentOrder.getOrderDateTime()) %>
                        </strong>
                    </div>

                    <div>
                        <span>Total</span>
                        <strong>
                            RM <%= String.format("%.2f", currentOrder.getTotalAmount()) %>
                        </strong>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= currentOrder.getOrderID() %>"
                   class="view-order-button">
                    View Order Details
                    <i class="fa-solid fa-arrow-right"></i>
                </a>
            </article>
            <% } %>
        </section>

        <% } %>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>