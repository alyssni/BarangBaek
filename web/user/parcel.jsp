<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.barangbaek.bean.parcel"%>

<%!
    private int courierStep(String status) {
        if ("Delivered".equals(status)) return 5;
        if ("Out for Delivery".equals(status)) return 4;
        if ("Shipped".equals(status)) return 3;
        if ("Packed".equals(status)) return 2;
        return 1;
    }

    private int pickupStep(String status) {
        if ("Collected".equals(status)) return 3;
        if ("Ready for Pickup".equals(status)) return 2;
        return 1;
    }

    private String stepClass(int step, int currentStep) {
        if (step < currentStep) return "completed";
        if (step == currentStep) return "active";
        return "pending";
    }
%>

<%
    List<parcel> buyerParcels =
            (List<parcel>) request.getAttribute("buyerParcels");

    List<parcel> sellerParcels =
            (List<parcel>) request.getAttribute("sellerParcels");

    SimpleDateFormat parcelDateFormat =
            new SimpleDateFormat("dd MMM yyyy, hh:mm a");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Track Parcel | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/parcel.css">
</head>
<body>

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="parcel-page">
        <header class="parcel-heading">
            <div>
                <span class="parcel-eyebrow">Delivery centre</span>
                <h1>Track Parcels</h1>
                <p>
                    Follow your purchases and manage deliveries for items
                    sold through BarangBaek.
                </p>
            </div>

            <a href="${pageContext.request.contextPath}/item?action=dashboard">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Dashboard
            </a>
        </header>

        <% if (request.getAttribute("success") != null) { %>
        <div class="parcel-alert parcel-alert--success">
            <i class="fa-solid fa-circle-check"></i>
            <span><%= request.getAttribute("success") %></span>
        </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="parcel-alert parcel-alert--error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <span><%= request.getAttribute("error") %></span>
        </div>
        <% } %>

        <section class="parcel-section">
            <div class="parcel-section__heading">
                <div>
                    <span class="parcel-eyebrow">Buyer view</span>
                    <h2>My Deliveries</h2>
                    <p>Orders purchased from other BarangBaek sellers.</p>
                </div>

                <span class="parcel-count">
                    <%= buyerParcels != null ? buyerParcels.size() : 0 %>
                </span>
            </div>

            <% if (buyerParcels == null || buyerParcels.isEmpty()) { %>
            <div class="parcel-empty">
                <i class="fa-solid fa-box-open"></i>
                <h3>No purchase deliveries yet</h3>
                <p>Purchased items will appear here after checkout.</p>
                <a href="${pageContext.request.contextPath}/item?action=dashboard">
                    Browse Items
                </a>
            </div>
            <% } else {
                for (parcel currentParcel : buyerParcels) {
                    boolean courier =
                            "Courier Delivery".equals(
                                    currentParcel.getDeliveryType()
                            );

                    int currentStep = courier
                            ? courierStep(currentParcel.getDeliveryStatus())
                            : pickupStep(currentParcel.getDeliveryStatus());
            %>
            <article class="parcel-card">
                <div class="parcel-card__top">
                    <div>
                        <span>Order #<%= currentParcel.getOrderID() %></span>
                        <h3>
                            <%= courier
                                    ? "Courier Delivery"
                                    : "Campus Pickup" %>
                        </h3>
                    </div>

                    <span class="parcel-status">
                        <%= currentParcel.getDeliveryStatus() %>
                    </span>
                </div>

                <div class="parcel-summary">
                    <div>
                        <span>Seller</span>
                        <strong><%= currentParcel.getSellerName() %></strong>
                    </div>
                    <div>
                        <span>Order total</span>
                        <strong>
                            RM <%= String.format(
                                    "%.2f",
                                    currentParcel.getTotalAmount()
                            ) %>
                        </strong>
                    </div>
                    <div>
                        <span>Ordered</span>
                        <strong>
                            <%= parcelDateFormat.format(
                                    currentParcel.getOrderDateTime()
                            ) %>
                        </strong>
                    </div>
                    <div>
                        <span>Last updated</span>
                        <strong>
                            <%= currentParcel.getUpdatedAt() != null
                                    ? parcelDateFormat.format(
                                            currentParcel.getUpdatedAt()
                                    )
                                    : "Not updated" %>
                        </strong>
                    </div>
                </div>

                <div class="parcel-delivery-info">
                    <% if (courier) { %>
                    <div>
                        <span>Tracking number</span>
                        <strong>
                            <%= currentParcel.getTrackingNumber() != null
                                    && !currentParcel.getTrackingNumber().trim().isEmpty()
                                    ? currentParcel.getTrackingNumber()
                                    : "Not assigned yet" %>
                        </strong>
                    </div>
                    <% } else { %>
                    <div>
                        <span>Pickup location</span>
                        <strong>
                            <%= currentParcel.getPickupLocation() != null
                                    ? currentParcel.getPickupLocation()
                                    : "Not assigned" %>
                        </strong>
                    </div>
                    <% } %>
                </div>

                <div class="parcel-timeline">
                    <% if (courier) { %>
                    <div class="parcel-step <%= stepClass(1, currentStep) %>">
                        <span></span><strong>Preparing</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(2, currentStep) %>">
                        <span></span><strong>Packed</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(3, currentStep) %>">
                        <span></span><strong>Shipped</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(4, currentStep) %>">
                        <span></span><strong>Out for Delivery</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(5, currentStep) %>">
                        <span></span><strong>Delivered</strong>
                    </div>
                    <% } else { %>
                    <div class="parcel-step <%= stepClass(1, currentStep) %>">
                        <span></span><strong>Preparing for Pickup</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(2, currentStep) %>">
                        <span></span><strong>Ready for Pickup</strong>
                    </div>
                    <div class="parcel-step <%= stepClass(3, currentStep) %>">
                        <span></span><strong>Collected</strong>
                    </div>
                    <% } %>
                </div>

                <div class="parcel-card__actions">
                    <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= currentParcel.getOrderID() %>">
                        View Order Details
                    </a>
                </div>
            </article>
            <%  }
               } %>
        </section>

        <section class="parcel-section">
            <div class="parcel-section__heading">
                <div>
                    <span class="parcel-eyebrow">Seller view</span>
                    <h2>Customer Deliveries</h2>
                    <p>Update deliveries for items purchased from you.</p>
                </div>

                <span class="parcel-count">
                    <%= sellerParcels != null ? sellerParcels.size() : 0 %>
                </span>
            </div>

            <% if (sellerParcels == null || sellerParcels.isEmpty()) { %>
            <div class="parcel-empty">
                <i class="fa-solid fa-truck-ramp-box"></i>
                <h3>No customer deliveries yet</h3>
                <p>New sales orders will appear here after checkout.</p>
                <a href="${pageContext.request.contextPath}/item?action=mylistings">
                    View My Inventory
                </a>
            </div>
            <% } else {
                for (parcel currentParcel : sellerParcels) {
                    boolean courier =
                            "Courier Delivery".equals(
                                    currentParcel.getDeliveryType()
                            );
            %>
            <article class="parcel-card parcel-card--seller">
                <div class="parcel-card__top">
                    <div>
                        <span>Order #<%= currentParcel.getOrderID() %></span>
                        <h3>Buyer: <%= currentParcel.getBuyerName() %></h3>
                    </div>

                    <span class="parcel-status">
                        <%= currentParcel.getDeliveryStatus() %>
                    </span>
                </div>

                <div class="parcel-summary">
                    <div>
                        <span>Delivery type</span>
                        <strong><%= currentParcel.getDeliveryType() %></strong>
                    </div>
                    <div>
                        <span>Order total</span>
                        <strong>
                            RM <%= String.format(
                                    "%.2f",
                                    currentParcel.getTotalAmount()
                            ) %>
                        </strong>
                    </div>
                    <div>
                        <span>Order status</span>
                        <strong><%= currentParcel.getOrderStatus() %></strong>
                    </div>
                    <div>
                        <span>Last updated</span>
                        <strong>
                            <%= currentParcel.getUpdatedAt() != null
                                    ? parcelDateFormat.format(
                                            currentParcel.getUpdatedAt()
                                    )
                                    : "Not updated" %>
                        </strong>
                    </div>
                </div>

                <% if (!courier) { %>
                <div class="parcel-delivery-info">
                    <div>
                        <span>Pickup location</span>
                        <strong><%= currentParcel.getPickupLocation() %></strong>
                    </div>
                </div>
                <% } %>

                <form class="parcel-update-form"
                      action="${pageContext.request.contextPath}/parcel?action=update"
                      method="post">

                    <input type="hidden"
                           name="parcelID"
                           value="<%= currentParcel.getParcelID() %>">

                    <% if (courier) { %>
                    <div class="parcel-field">
                        <label for="tracking-<%= currentParcel.getParcelID() %>">
                            Tracking Number
                        </label>
                        <input id="tracking-<%= currentParcel.getParcelID() %>"
                               type="text"
                               name="trackingNumber"
                               maxlength="100"
                               value="<%= currentParcel.getTrackingNumber() != null
                                       ? currentParcel.getTrackingNumber()
                                       : "" %>"
                               placeholder="Example: JT123456789MY">
                    </div>
                    <% } %>

                    <div class="parcel-field">
                        <label for="status-<%= currentParcel.getParcelID() %>">
                            Delivery Status
                        </label>

                        <select id="status-<%= currentParcel.getParcelID() %>"
                                name="deliveryStatus"
                                required>
                            <% if (courier) { %>
                            <option value="Preparing"
                                    <%= "Preparing".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Preparing
                            </option>
                            <option value="Packed"
                                    <%= "Packed".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Packed
                            </option>
                            <option value="Shipped"
                                    <%= "Shipped".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Shipped
                            </option>
                            <option value="Out for Delivery"
                                    <%= "Out for Delivery".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Out for Delivery
                            </option>
                            <option value="Delivered"
                                    <%= "Delivered".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Delivered
                            </option>
                            <% } else { %>
                            <option value="Preparing for Pickup"
                                    <%= "Preparing for Pickup".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Preparing for Pickup
                            </option>
                            <option value="Ready for Pickup"
                                    <%= "Ready for Pickup".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Ready for Pickup
                            </option>
                            <option value="Collected"
                                    <%= "Collected".equals(currentParcel.getDeliveryStatus())
                                            ? "selected" : "" %>>
                                Collected
                            </option>
                            <% } %>
                        </select>
                    </div>

                    <div class="parcel-update-form__actions">
                        <a href="${pageContext.request.contextPath}/order?action=details&orderId=<%= currentParcel.getOrderID() %>">
                            View Order
                        </a>

                        <button type="submit">
                            <i class="fa-solid fa-floppy-disk"></i>
                            Update Delivery
                        </button>
                    </div>
                </form>
            </article>
            <%  }
               } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>