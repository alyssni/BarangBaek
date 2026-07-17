<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.cartitem"%>

<%
    List<cartitem> cartItems = (List<cartitem>) request.getAttribute("cartItems");
    Double cartTotalValue = (Double) request.getAttribute("cartTotal");
    double cartTotal = cartTotalValue != null ? cartTotalValue : 0.0;
    String result = request.getParameter("result");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Cart | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/cart.css">
</head>

<body>

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="cart-page">

        <header class="cart-heading">
            <div>
                <span class="cart-eyebrow">Buyer workspace</span>
                <h1>My Cart</h1>
                <p>Select the items that you want to purchase.</p>
            </div>

            <a class="continue-shopping"
               href="${pageContext.request.contextPath}/item?action=dashboard">
                <i class="fa-solid fa-arrow-left"></i>
                Continue Shopping
            </a>
        </header>

        <% if ("updated".equals(result)) { %>
        <div class="cart-message cart-message--success">
            Cart quantity updated successfully.
        </div>
        <% } else if ("removed".equals(result)) { %>
        <div class="cart-message cart-message--success">
            Item removed from your cart.
        </div>
        <% } else if ("cleared".equals(result)) { %>
        <div class="cart-message cart-message--success">
            Your cart has been cleared.
        </div>
        <% } else if ("stock".equals(result)) { %>
        <div class="cart-message cart-message--error">
            The selected quantity exceeds the available stock.
        </div>
        <% } else if ("unavailable".equals(result)) { %>
        <div class="cart-message cart-message--error">
            This item is no longer available. Remove it or choose another item.
        </div>
        <% } else if ("selectItems".equals(result)) { %>
        <div class="cart-message cart-message--error">
            Select at least one item before continuing to checkout.
        </div>
        <% } else if ("checkoutUnavailable".equals(result)) { %>
        <div class="cart-message cart-message--error">
            One of the selected items is unavailable or exceeds its current stock.
        </div>
        <% } else if ("checkoutReady".equals(result)) { %>
        <div class="cart-message cart-message--success">
            Your selected items are ready. The payment connection is the next module.
        </div>
        <% } else if ("invalidQuantity".equals(result)
                || "invalidSelection".equals(result)) { %>
        <div class="cart-message cart-message--error">
            Please enter a valid quantity and selection.
        </div>
        <% } else if ("system".equals(result)) { %>
        <div class="cart-message cart-message--error">
            The cart action could not be completed. Please try again.
        </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="cart-message cart-message--error">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <% if (cartItems == null || cartItems.isEmpty()) { %>

        <section class="cart-empty">
            <i class="fa-solid fa-cart-shopping"></i>
            <h2>Your cart is empty</h2>
            <p>Explore the marketplace and add items that you want to buy.</p>
            <a href="${pageContext.request.contextPath}/item?action=dashboard">
                Browse Items
            </a>
        </section>

        <% } else { %>

        <div class="cart-layout">

            <section class="cart-list-card">

                <div class="cart-list-toolbar">
                    <label class="select-all-option">
                        <input type="checkbox"
                               id="selectAllCartItems"
                               onchange="toggleAllCartItems(this)">
                        Select all available items
                    </label>

                    <form action="${pageContext.request.contextPath}/cart"
                          method="post"
                          onsubmit="return confirm('Clear every item from your cart?');">
                        <input type="hidden" name="action" value="clear">
                        <button type="submit" class="clear-cart-button">
                            <i class="fa-solid fa-trash-can"></i>
                            Clear Cart
                        </button>
                    </form>
                </div>

                <form id="checkoutForm"
                      action="${pageContext.request.contextPath}/cart"
                      method="post">
                    <input type="hidden" name="action" value="prepareCheckout">
                </form>

                <% for (cartitem c : cartItems) {
                    boolean available = c.isAvailable()
                            && c.getQuantity() <= c.getStock();
                %>

                <article class="cart-row <%= available ? "" : "cart-row--unavailable" %>">

                    <div class="cart-row__selector">
                        <input type="checkbox"
                               class="cart-select-box"
                               name="cartItemID"
                               value="<%= c.getCartItemID() %>"
                               form="checkoutForm"
                               data-price="<%= c.getPrice() %>"
                               data-quantity="<%= c.getQuantity() %>"
                               onchange="updateCartSelectionSummary()"
                               <%= available ? "" : "disabled" %>>
                    </div>

                    <a class="cart-row__image"
                       href="${pageContext.request.contextPath}/item?action=details&id=<%= c.getItemID() %>">
                        <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= c.getItemPhoto() %>"
                             alt="<%= c.getItemName() %>"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">
                    </a>

                    <div class="cart-row__information">
                        <span class="cart-row__condition">
                            <%= c.getItemCondition() %>
                        </span>

                        <h2>
                            <a href="${pageContext.request.contextPath}/item?action=details&id=<%= c.getItemID() %>">
                                <%= c.getItemName() %>
                            </a>
                        </h2>

                        <p>
                            Sold by
                            <a href="${pageContext.request.contextPath}/item?action=sellerProfile&sellerID=<%= c.getSellerID() %>">
                                <%= c.getSellerName() %>
                            </a>
                        </p>

                        <strong class="cart-row__price">
                            RM <%= String.format("%.2f", c.getPrice()) %>
                        </strong>

                        <% if (!available) { %>
                        <div class="cart-row__warning">
                            <i class="fa-solid fa-circle-exclamation"></i>
                            This listing is unavailable or its stock has changed.
                        </div>
                        <% } %>
                    </div>

                    <div class="cart-row__controls">

                        <form action="${pageContext.request.contextPath}/cart"
                              method="post"
                              class="cart-update-form">

                            <input type="hidden" name="action" value="update">
                            <input type="hidden" name="cartItemID"
                                   value="<%= c.getCartItemID() %>">

                            <label for="quantity-<%= c.getCartItemID() %>">
                                Quantity
                            </label>

                            <input type="number"
                                   id="quantity-<%= c.getCartItemID() %>"
                                   name="quantity"
                                   value="<%= c.getQuantity() %>"
                                   min="1"
                                   max="<%= Math.max(c.getStock(), 1) %>"
                                   <%= available ? "required" : "disabled" %>>

                            <button type="submit"
                                    <%= available ? "" : "disabled" %>>
                                Update
                            </button>
                        </form>

                        <div class="cart-row__subtotal">
                            <span>Subtotal</span>
                            <strong>
                                RM <%= String.format("%.2f", c.getSubtotal()) %>
                            </strong>
                        </div>

                        <form action="${pageContext.request.contextPath}/cart"
                              method="post">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cartItemID"
                                   value="<%= c.getCartItemID() %>">

                            <button type="submit"
                                    class="remove-cart-button"
                                    onclick="return confirm('Remove this item from your cart?');">
                                <i class="fa-solid fa-trash"></i>
                                Remove
                            </button>
                        </form>
                    </div>
                </article>
                <% } %>
            </section>

            <aside class="cart-summary-card">
                <span class="cart-summary-eyebrow">Order summary</span>
                <h2>Selected Items</h2>

                <div class="cart-summary-line">
                    <span>Selected quantity</span>
                    <strong id="selectedItemCount">0</strong>
                </div>

                <div class="cart-summary-line cart-summary-line--total">
                    <span>Selected total</span>
                    <strong id="selectedCartTotal">RM 0.00</strong>
                </div>

                <button type="submit"
                        form="checkoutForm"
                        class="checkout-button">
                    Proceed to Checkout
                    <i class="fa-solid fa-arrow-right"></i>
                </button>

                <p class="cart-summary-note">
                    Current full-cart value: RM <%= String.format("%.2f", cartTotal) %>
                </p>
            </aside>
        </div>
        <% } %>
    </main>
    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>