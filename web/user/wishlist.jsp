<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.item"%>
<%!
    private String html(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<%
    List<item> items = (List<item>) request.getAttribute("items");
    Integer loggedInUserID = (Integer) session.getAttribute("userID");
    int totalItems = items != null ? items.size() : 0;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Wishlist | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/marketplace.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/wishlist.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="wishlist-page">
        <section class="wishlist-hero">
            <div>
                <span class="market-eyebrow">Saved for later</span>
                <h1>My Wishlist</h1>
                <p>Keep track of campus finds you may want to buy later.</p>
            </div>

            <div class="wishlist-hero__count">
                <i class="fa-solid fa-heart"></i>
                <strong><%= totalItems %></strong>
                <span><%= totalItems == 1 ? "saved item" : "saved items" %></span>
            </div>
        </section>

        <%
            String result = request.getParameter("result");
            String cartResult = request.getParameter("cart");
        %>

        <% if ("removed".equals(result)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-regular fa-heart"></i>
            Item removed from your wishlist.
        </div>
        <% } else if ("added".equals(result)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-solid fa-heart"></i>
            Item saved to your wishlist.
        </div>
        <% } else if ("system".equals(result)) { %>
        <div class="market-alert market-alert--error">
            The wishlist could not be updated. Please try again.
        </div>
        <% } %>

        <% if ("added".equals(cartResult)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-solid fa-cart-plus"></i>
            Item added to your cart.
        </div>
        <% } else if ("stock".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            The requested quantity exceeds the available stock.
        </div>
        <% } else if ("unavailable".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            This item is no longer available.
        </div>
        <% } else if ("system".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            The item could not be added to your cart.
        </div>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
        <div class="market-alert market-alert--error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <div class="wishlist-toolbar">
            <div>
                <span class="market-eyebrow">Your collection</span>
                <h2>Saved Marketplace Items</h2>
            </div>

            <a href="${pageContext.request.contextPath}/item?action=dashboard">
                <i class="fa-solid fa-arrow-left"></i>
                Continue Browsing
            </a>
        </div>

        <section class="wishlist-grid" aria-label="Wishlist items">
            <% if (items != null && !items.isEmpty()) {
                for (item i : items) {
                    String photo = i.getItemPhoto();
                    if (photo == null || photo.trim().isEmpty()) {
                        photo = "default-item.png";
                    }

                    boolean ownItem = loggedInUserID != null
                            && loggedInUserID == i.getSellerID();
                    String status = i.getItemStatus();
                    if (status == null || status.trim().isEmpty()) {
                        status = "Unavailable";
                    }
                    boolean available = "Available".equalsIgnoreCase(status)
                            && i.getStock() > 0;
            %>
            <article class="wishlist-card <%= available ? "" : "is-unavailable" %>">
                <div class="wishlist-card__photo">
                    <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= html(photo) %>"
                         alt="<%= html(i.getItemName()) %>"
                         onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">

                    <form action="${pageContext.request.contextPath}/wishlist"
                          method="post">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="itemID" value="<%= i.getItemID() %>">
                        <input type="hidden" name="source" value="wishlist">
                        <button type="submit"
                                class="wishlist-remove-button"
                                title="Remove from wishlist"
                                aria-label="Remove from wishlist">
                            <i class="fa-solid fa-heart"></i>
                        </button>
                    </form>

                    <span class="wishlist-status-badge status-<%= html(status.toLowerCase().replace(" ", "-")) %>">
                        <%= html(status) %>
                    </span>
                </div>

                <div class="wishlist-card__body">
                    <span class="wishlist-card__category">
                        <%= i.getCategoryName() != null
                                ? html(i.getCategoryName())
                                : "Uncategorised" %>
                    </span>
                    <h3><%= html(i.getItemName()) %></h3>
                    <p class="wishlist-card__seller">
                        Sold by <%= i.getSellerName() != null
                                ? html(i.getSellerName())
                                : "Unknown seller" %>
                    </p>
                    <p class="wishlist-card__price">
                        RM <%= String.format("%.2f", i.getPrice()) %>
                    </p>

                    <div class="wishlist-card__facts">
                        <span><i class="fa-solid fa-star-half-stroke"></i> <%= html(i.getItemCondition()) %></span>
                        <span><i class="fa-solid fa-box"></i> <%= i.getStock() %> available</span>
                    </div>

                    <a class="wishlist-details-link"
                       href="${pageContext.request.contextPath}/item?action=details&id=<%= i.getItemID() %>">
                        Item Details
                    </a>

                    <% if (ownItem) { %>
                    <a class="wishlist-manage-link"
                       href="${pageContext.request.contextPath}/item?action=edit&id=<%= i.getItemID() %>">
                        Manage Your Item
                    </a>
                    <% } else if (available) { %>
                    <form class="wishlist-cart-form"
                          action="${pageContext.request.contextPath}/cart"
                          method="post">
                        <input type="hidden" name="action" value="add">
                        <input type="hidden" name="itemID" value="<%= i.getItemID() %>">
                        <input type="hidden" name="source" value="wishlist">

                        <div class="quantity-control">
                            <button type="button"
                                    onclick="changeQuantity(this, -1, <%= i.getStock() %>)">−</button>
                            <input type="number"
                                   name="quantity"
                                   value="1"
                                   min="1"
                                   max="<%= i.getStock() %>"
                                   readonly>
                            <button type="button"
                                    onclick="changeQuantity(this, 1, <%= i.getStock() %>)">+</button>
                        </div>

                        <button type="submit" class="wishlist-add-cart-button">
                            <i class="fa-solid fa-cart-plus"></i>
                            Add to Cart
                        </button>
                    </form>
                    <% } else { %>
                    <div class="wishlist-unavailable-note">
                        <i class="fa-solid fa-circle-exclamation"></i>
                        This saved item cannot currently be purchased.
                    </div>
                    <% } %>
                </div>
            </article>
            <%  }
               } else { %>
            <div class="wishlist-empty-state">
                <i class="fa-regular fa-heart"></i>
                <h2>Your wishlist is empty</h2>
                <p>Tap the heart button on an item to save it here.</p>
                <a href="${pageContext.request.contextPath}/item?action=dashboard">
                    Browse Marketplace
                </a>
            </div>
            <% } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js?v=5"></script>
</body>
</html>
