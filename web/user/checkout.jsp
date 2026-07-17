<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.checkoutgroup"%>
<%@page import="com.barangbaek.bean.cartitem"%>
<%@page import="com.barangbaek.bean.user"%>

<%
    List<checkoutgroup> checkoutGroups =
            (List<checkoutgroup>) request.getAttribute("checkoutGroups");

    Double grandTotalValue =
            (Double) request.getAttribute("grandTotal");

    Integer totalQuantityValue =
            (Integer) request.getAttribute("totalQuantity");

    user buyer = (user) request.getAttribute("buyer");

    double grandTotal =
            grandTotalValue != null ? grandTotalValue : 0.0;

    int totalQuantity =
            totalQuantityValue != null ? totalQuantityValue : 0;

    String deliveryAddress = "";

    if (buyer != null) {
        StringBuilder addressBuilder = new StringBuilder();

        if (buyer.getAddress1() != null
                && !buyer.getAddress1().trim().isEmpty()) {
            addressBuilder.append(buyer.getAddress1().trim());
        }

        if (buyer.getAddress2() != null
                && !buyer.getAddress2().trim().isEmpty()) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(buyer.getAddress2().trim());
        }

        if (buyer.getPostcode() != null) {
            if (addressBuilder.length() > 0) {
                addressBuilder.append(", ");
            }
            addressBuilder.append(buyer.getPostcode());
        }

        if (buyer.getCity() != null) {
            addressBuilder.append(" ").append(buyer.getCity());
        }

        if (buyer.getState() != null) {
            addressBuilder.append(", ").append(buyer.getState());
        }

        deliveryAddress = addressBuilder.toString();
    }

    String selectedPaymentMethod =
            (String) request.getAttribute("selectedPaymentMethod");

    String selectedPaymentProvider =
            (String) request.getAttribute("selectedPaymentProvider");

    String selectedDeliveryType =
            (String) request.getAttribute("selectedDeliveryType");

    String selectedPickupLocation =
            (String) request.getAttribute("selectedPickupLocation");

    if (selectedPaymentMethod == null
            || selectedPaymentMethod.trim().isEmpty()) {
        selectedPaymentMethod = "Online Banking";
    }

    if (selectedPaymentProvider == null
            || selectedPaymentProvider.trim().isEmpty()) {
        selectedPaymentProvider = "Maybank";
    }

    if (selectedDeliveryType == null
            || selectedDeliveryType.trim().isEmpty()) {
        selectedDeliveryType = "Courier Delivery";
    }

    if (selectedPickupLocation == null) {
        selectedPickupLocation = "";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Checkout | BarangBaek</title>

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

    <main class="checkout-page">

        <header class="checkout-heading">
            <div>
                <span class="checkout-eyebrow">Secure checkout</span>
                <h1>Review and Pay</h1>
                <p>
                    Your selected items will be separated into one order
                    for each seller automatically.
                </p>
            </div>

            <a href="${pageContext.request.contextPath}/cart?action=view"
               class="checkout-back-link">
                <i class="fa-solid fa-arrow-left"></i>
                Back to Cart
            </a>
        </header>

        <% if (request.getAttribute("error") != null) { %>
        <div class="checkout-message checkout-message--error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (checkoutGroups == null || checkoutGroups.isEmpty()) { %>

        <section class="checkout-empty">
            <i class="fa-solid fa-cart-shopping"></i>
            <h2>No checkout items found</h2>
            <p>Please return to your cart and select the items again.</p>
            <a href="${pageContext.request.contextPath}/cart?action=view">
                Return to Cart
            </a>
        </section>

        <% } else { %>

        <form action="${pageContext.request.contextPath}/payment"
              method="post"
              class="checkout-layout">

            <input type="hidden" name="action" value="process">

            <section class="checkout-main-column">

                <section class="checkout-card">
                    <div class="checkout-card__heading">
                        <span class="checkout-step">1</span>
                        <div>
                            <h2>Selected Items</h2>
                            <p>
                                <%= totalQuantity %>
                                item<%= totalQuantity == 1 ? "" : "s" %>
                                from <%= checkoutGroups.size() %>
                                seller<%= checkoutGroups.size() == 1 ? "" : "s" %>
                            </p>
                        </div>
                    </div>

                    <% for (checkoutgroup group : checkoutGroups) { %>
                    <div class="seller-order-group">
                        <div class="seller-order-group__heading">
                            <div>
                                <span>Seller</span>
                                <strong><%= group.getSellerName() %></strong>
                            </div>

                            <strong>
                                RM <%= String.format("%.2f", group.getGroupTotal()) %>
                            </strong>
                        </div>

                        <% for (cartitem selectedItem : group.getItems()) { %>
                        <article class="checkout-item-row">
                            <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= selectedItem.getItemPhoto() %>"
                                 alt="<%= selectedItem.getItemName() %>"
                                 onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">

                            <div class="checkout-item-row__info">
                                <h3><%= selectedItem.getItemName() %></h3>
                                <p>
                                    Quantity: <%= selectedItem.getQuantity() %>
                                    &middot;
                                    RM <%= String.format("%.2f", selectedItem.getPrice()) %> each
                                </p>
                            </div>

                            <strong>
                                RM <%= String.format("%.2f", selectedItem.getSubtotal()) %>
                            </strong>
                        </article>
                        <% } %>
                    </div>
                    <% } %>
                </section>

                <section class="checkout-card">
                    <div class="checkout-card__heading">
                        <span class="checkout-step">2</span>
                        <div>
                            <h2>Delivery Method</h2>
                            <p>Select how you want to receive the items.</p>
                        </div>
                    </div>

                    <label class="checkout-choice">
                        <input type="radio"
                               name="deliveryType"
                               value="Courier Delivery"
                               <%= "Courier Delivery".equals(selectedDeliveryType) ? "checked" : "" %>
                               onchange="updateCheckoutDelivery()">

                        <span class="checkout-choice__icon">
                            <i class="fa-solid fa-truck"></i>
                        </span>

                        <span class="checkout-choice__content">
                            <strong>Courier Delivery</strong>
                            <small>
                                Deliver to your registered profile address.
                            </small>
                        </span>
                    </label>

                    <div class="delivery-address-box" id="deliveryAddressBox">
                        <strong>Delivery address</strong>
                        <p>
                            <%= deliveryAddress.isEmpty()
                                    ? "No address is available. Update your profile before checkout."
                                    : deliveryAddress %>
                        </p>
                    </div>

                    <label class="checkout-choice">
                        <input type="radio"
                               name="deliveryType"
                               value="Campus Pickup"
                               <%= "Campus Pickup".equals(selectedDeliveryType) ? "checked" : "" %>
                               onchange="updateCheckoutDelivery()">

                        <span class="checkout-choice__icon">
                            <i class="fa-solid fa-location-dot"></i>
                        </span>

                        <span class="checkout-choice__content">
                            <strong>Campus Pickup</strong>
                            <small>
                                Meet the seller at a selected public campus location.
                            </small>
                        </span>
                    </label>

                    <div class="pickup-location-box" id="pickupLocationBox">
                        <label for="pickupLocation">Pickup location</label>
                        <select id="pickupLocation" name="pickupLocation">
                            <option value="">Select pickup location</option>
                            <option value="Main Library Entrance" <%= "Main Library Entrance".equals(selectedPickupLocation) ? "selected" : "" %>>Main Library Entrance</option>
                            <option value="Student Centre Lobby" <%= "Student Centre Lobby".equals(selectedPickupLocation) ? "selected" : "" %>>Student Centre Lobby</option>
                            <option value="Faculty Main Entrance" <%= "Faculty Main Entrance".equals(selectedPickupLocation) ? "selected" : "" %>>Faculty Main Entrance</option>
                            <option value="Campus Security Post" <%= "Campus Security Post".equals(selectedPickupLocation) ? "selected" : "" %>>Campus Security Post</option>
                        </select>
                    </div>
                </section>

                <section class="checkout-card">
                    <div class="checkout-card__heading">
                        <span class="checkout-step">3</span>
                        <div>
                            <h2>Payment Method</h2>
                            <p>
                                Choose a simulated payment method for this
                                academic project. No real banking or card
                                credentials are collected.
                            </p>
                        </div>
                    </div>

                    <label class="checkout-choice checkout-payment-choice">
                        <input type="radio"
                               name="paymentMethod"
                               value="Online Banking"
                               <%= "Online Banking".equals(selectedPaymentMethod) ? "checked" : "" %>
                               onchange="updateCheckoutPayment()">

                        <span class="checkout-choice__icon">
                            <i class="fa-solid fa-building-columns"></i>
                        </span>

                        <span class="checkout-choice__content">
                            <strong>Online Banking</strong>
                            <small>
                                Simulated instant payment through a selected bank.
                            </small>
                        </span>

                        <span class="payment-status-pill">Paid instantly</span>
                    </label>

                    <label class="checkout-choice checkout-payment-choice">
                        <input type="radio"
                               name="paymentMethod"
                               value="Debit/Credit Card"
                               <%= "Debit/Credit Card".equals(selectedPaymentMethod) ? "checked" : "" %>
                               onchange="updateCheckoutPayment()">

                        <span class="checkout-choice__icon">
                            <i class="fa-solid fa-credit-card"></i>
                        </span>

                        <span class="checkout-choice__content">
                            <strong>Debit/Credit Card</strong>
                            <small>
                                Simulated Visa or Mastercard transaction.
                            </small>
                        </span>

                        <span class="payment-status-pill">Paid instantly</span>
                    </label>

                    <label class="checkout-choice checkout-payment-choice">
                        <input type="radio"
                               name="paymentMethod"
                               value="Cash on Pickup"
                               <%= "Cash on Pickup".equals(selectedPaymentMethod) ? "checked" : "" %>
                               onchange="updateCheckoutPayment()">

                        <span class="checkout-choice__icon">
                            <i class="fa-solid fa-money-bill-wave"></i>
                        </span>

                        <span class="checkout-choice__content">
                            <strong>Cash on Pickup</strong>
                            <small>
                                Pay the seller when the item is collected on campus.
                            </small>
                        </span>

                        <span class="payment-status-pill payment-status-pill--pending">
                            Pending until collected
                        </span>
                    </label>

                    <div class="payment-provider-box"
                         id="paymentProviderBox"
                         data-selected-provider="<%= selectedPaymentProvider %>">

                        <label for="paymentProvider" id="paymentProviderLabel">
                            Select bank
                        </label>

                        <select id="paymentProvider"
                                name="paymentProvider">
                        </select>

                        <p id="paymentProviderHelp">
                            This selection is recorded only as payment-method
                            information for the receipt.
                        </p>
                    </div>
                </section>

            </section>

            <aside class="checkout-summary-card">
                <span class="checkout-summary-eyebrow">Payment summary</span>
                <h2>Order Total</h2>

                <div class="checkout-summary-line">
                    <span>Selected quantity</span>
                    <strong><%= totalQuantity %></strong>
                </div>

                <div class="checkout-summary-line">
                    <span>Seller orders</span>
                    <strong><%= checkoutGroups.size() %></strong>
                </div>

                <div class="checkout-summary-line">
                    <span>Delivery fee</span>
                    <strong>RM 0.00</strong>
                </div>

                <div class="checkout-summary-line checkout-summary-line--total">
                    <span>Total payment</span>
                    <strong>RM <%= String.format("%.2f", grandTotal) %></strong>
                </div>

                <button type="submit" class="pay-now-button">
                    <i class="fa-solid fa-lock"></i>
                    Confirm and Pay
                </button>

                <p class="checkout-summary-note">
                    Stock and item availability are checked again when
                    you confirm payment.
                </p>
            </aside>

        </form>

        <% } %>

    </main>

    <script src="${pageContext.request.contextPath}/assets/js/checkout-payment.js?v=1"></script>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>