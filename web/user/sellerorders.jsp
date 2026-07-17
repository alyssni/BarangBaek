<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.barangbaek.bean.order"%>

<%
    List<order> orders =
            (List<order>) request.getAttribute("orders");

    SimpleDateFormat orderDateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Sales Orders | BarangBaek</title>

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
                <span class="orders-eyebrow">Seller workspace</span>
                <h1>Sales Orders</h1>
                <p>Orders received from users who purchased your listings.</p>
            </div>

            <a href="${pageContext.request.contextPath}/item?action=mylistings">
                <i class="fa-solid fa-boxes-stacked"></i>
                My Inventory
            </a>
        </header>

        <% if (request.getAttribute("error") != null) { %>
        <div class="orders-message orders-message--error">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (orders == null || orders.isEmpty()) { %>
        <section class="orders-empty">
            <i class="fa-solid fa-receipt"></i>
            <h2>No sales orders yet</h2>
            <p>Orders will appear after another user purchases your item.</p>
            <a href="${pageContext.request.contextPath}/item?action=mylistings">
                View Inventory
            </a>
        </section>
        <% } else { %>

        <section class="orders-list">
            <% for (order currentOrder : orders) { %>
            <article class="order-card">
                <div class="order-card__top">
                    <div>
                        <span>Order #<%= currentOrder.getOrderID() %></span>
                        <strong>Buyer: <%= currentOrder.getBuyerName() %></strong>
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
                        <span>Sales total</span>
                        <strong>
                            RM <%= String.format("%.2f", currentOrder.getTotalAmount()) %>
                        </strong>
                    </div>
                </div>

                <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= currentOrder.getOrderID() %>"
                   class="view-order-button">
                    View Sales Details
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