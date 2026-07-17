<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.barangbaek.bean.order"%>
<%@page import="com.barangbaek.bean.orderitem"%>
<%@page import="com.barangbaek.bean.payment"%>
<%@page import="com.barangbaek.bean.parcel"%>

<%
    order receiptOrder =
            (order) request.getAttribute("receiptOrder");

    List<orderitem> receiptItems =
            (List<orderitem>) request.getAttribute("receiptItems");

    payment receiptPayment =
            (payment) request.getAttribute("receiptPayment");

    parcel receiptParcel =
            (parcel) request.getAttribute("receiptParcel");

    String viewerType =
            (String) request.getAttribute("receiptViewerType");

    boolean sellerView = "SELLER".equals(viewerType);

    SimpleDateFormat receiptDateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");

    String receiptNumber = receiptOrder != null
            ? String.format("BB-RCP-%06d", receiptOrder.getOrderID())
            : "-";

    String paymentReference = receiptPayment != null
            ? String.format("BB-PAY-%06d", receiptPayment.getPaymentID())
            : "Not recorded";
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Receipt | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/receipt.css">
</head>

<body>

    <div class="receipt-screen-navbar">
        <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>
    </div>

    <main class="receipt-page">

        <% if (request.getAttribute("receiptError") != null
                || receiptOrder == null) { %>

        <section class="receipt-error-card">
            <i class="fa-solid fa-file-circle-xmark"></i>
            <h1>Receipt unavailable</h1>
            <p>
                <%= request.getAttribute("receiptError") != null
                        ? request.getAttribute("receiptError")
                        : "The requested receipt could not be found." %>
            </p>
            <a href="${pageContext.request.contextPath}/order?action=myorders">
                Back to Purchases
            </a>
        </section>

        <% } else { %>

        <div class="receipt-toolbar">
            <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= receiptOrder.getOrderID() %>"
               class="receipt-back-button">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Order
            </a>

            <button type="button"
                    class="receipt-print-button"
                    onclick="window.print()">
                <i class="fa-solid fa-print"></i>
                Print Receipt
            </button>
        </div>

        <article class="receipt-document">
            <header class="receipt-header">
                <div class="receipt-brand">
                    <img src="${pageContext.request.contextPath}/assets/img/logo.png"
                         alt="BarangBaek">
                    <div>
                        <strong>BarangBaek</strong>
                        <span>Campus Buy-and-Sell Marketplace</span>
                    </div>
                </div>

                <div class="receipt-title-block">
                    <span>Official payment record</span>
                    <h1>Receipt</h1>
                    <strong><%= receiptNumber %></strong>
                </div>
            </header>

            <section class="receipt-meta-grid">
                <div>
                    <span>Order number</span>
                    <strong>#<%= receiptOrder.getOrderID() %></strong>
                </div>

                <div>
                    <span>Order date</span>
                    <strong>
                        <%= receiptDateFormat.format(
                                receiptOrder.getOrderDateTime()
                        ) %>
                    </strong>
                </div>

                <div>
                    <span>Payment reference</span>
                    <strong><%= paymentReference %></strong>
                </div>

                <div>
                    <span>Payment recorded</span>
                    <strong>
                        <%= receiptPayment != null
                                && receiptPayment.getPaymentDateTime() != null
                                ? receiptDateFormat.format(
                                        receiptPayment.getPaymentDateTime()
                                )
                                : "Not recorded" %>
                    </strong>
                </div>
            </section>

            <section class="receipt-party-section">
                <div>
                    <span>Buyer</span>
                    <strong><%= receiptOrder.getBuyerName() %></strong>
                </div>

                <div>
                    <span>Seller</span>
                    <strong><%= receiptOrder.getSellerName() %></strong>
                </div>
            </section>

            <section class="receipt-items-section">
                <h2>Purchased Items</h2>

                <div class="receipt-table-wrap">
                    <table class="receipt-table">
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>Unit Price</th>
                                <th>Quantity</th>
                                <th>Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (receiptItems != null) {
                                for (orderitem currentItem : receiptItems) {
                                    double lineTotal =
                                            currentItem.getPriceAtPurchase()
                                            * currentItem.getQuantity(); %>
                            <tr>
                                <td><%= currentItem.getItemName() %></td>
                                <td>
                                    RM <%= String.format(
                                            "%.2f",
                                            currentItem.getPriceAtPurchase()
                                    ) %>
                                </td>
                                <td><%= currentItem.getQuantity() %></td>
                                <td>
                                    RM <%= String.format("%.2f", lineTotal) %>
                                </td>
                            </tr>
                            <%  }
                               } %>
                        </tbody>
                    </table>
                </div>
            </section>

            <section class="receipt-summary-layout">
                <div class="receipt-payment-card">
                    <h2>Payment and Delivery</h2>

                    <div>
                        <span>Payment method</span>
                        <strong>
                            <%= receiptPayment != null
                                    ? receiptPayment.getPaymentMethod()
                                    : "Not recorded" %>
                        </strong>
                    </div>

                    <div>
                        <span>Payment status</span>
                        <strong class="receipt-status">
                            <%= receiptPayment != null
                                    ? receiptPayment.getPaymentStatus()
                                    : "Pending" %>
                        </strong>
                    </div>

                    <div>
                        <span>Delivery method</span>
                        <strong>
                            <%= receiptParcel != null
                                    ? receiptParcel.getDeliveryType()
                                    : "Not recorded" %>
                        </strong>
                    </div>

                    <% if (receiptParcel != null
                            && receiptParcel.getPickupLocation() != null
                            && !receiptParcel.getPickupLocation().trim().isEmpty()) { %>
                    <div>
                        <span>Pickup location</span>
                        <strong><%= receiptParcel.getPickupLocation() %></strong>
                    </div>
                    <% } %>
                </div>

                <div class="receipt-total-card">
                    <span>Grand Total</span>
                    <strong>
                        RM <%= String.format(
                                "%.2f",
                                receiptOrder.getTotalAmount()
                        ) %>
                    </strong>
                    <small>
                        One receipt is generated for each seller order.
                    </small>
                </div>
            </section>

            <footer class="receipt-footer">
                <p>
                    Thank you for using BarangBaek. This receipt is generated
                    electronically and is valid without a signature.
                </p>
                <span>
                    Viewed as <%= sellerView ? "seller" : "buyer" %>
                </span>
            </footer>
        </article>

        <% } %>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>