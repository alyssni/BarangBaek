<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.barangbaek.bean.item"%>
<%@page import="com.barangbaek.bean.user"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
    item selectedItem = (item) request.getAttribute("item");
    user seller = (user) request.getAttribute("seller");
    Integer loggedInUserID = (Integer) session.getAttribute("userID");
    Boolean wishlistedValue = (Boolean) request.getAttribute("wishlisted");
    boolean wishlisted = wishlistedValue != null && wishlistedValue;

    if (selectedItem == null) {
        response.sendRedirect(request.getContextPath() + "/item?action=dashboard");
        return;
    }

    String itemStatus = selectedItem.getItemStatus();
    if (itemStatus == null || itemStatus.trim().isEmpty()) {
        itemStatus = "Unavailable";
    }

    boolean ownItem = loggedInUserID != null
            && loggedInUserID == selectedItem.getSellerID();

    boolean canBuy = !ownItem
            && "Available".equalsIgnoreCase(itemStatus)
            && selectedItem.getStock() > 0;

    String itemPhoto = selectedItem.getItemPhoto();
    if (itemPhoto == null || itemPhoto.trim().isEmpty()) {
        itemPhoto = "default-item.png";
    }

    String sellerPhoto = "default-user.png";
    String sellerDisplayName = "Unknown seller";
    String sellerUsername = "";
    String sellerUniversity = "University not provided";

    if (seller != null) {
        if (seller.getUserPhoto() != null
                && !seller.getUserPhoto().trim().isEmpty()) {
            sellerPhoto = seller.getUserPhoto();
        }
        if (seller.getFullName() != null
                && !seller.getFullName().trim().isEmpty()) {
            sellerDisplayName = seller.getFullName();
        }
        if (seller.getUsername() != null) {
            sellerUsername = seller.getUsername();
        }
        if (seller.getUniversity() != null
                && !seller.getUniversity().trim().isEmpty()) {
            sellerUniversity = seller.getUniversity();
        }
    }

    String listedDate = "Not available";
    if (selectedItem.getCreatedAt() != null) {
        listedDate = new SimpleDateFormat("dd MMMM yyyy")
                .format(selectedItem.getCreatedAt());
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= selectedItem.getItemName() %> | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/itemdetails.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/wishlist.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="details-page">
        <%
            String cartResult = request.getParameter("cart");
        %>

        <%
            String wishlistResult = request.getParameter("wishlist");
        %>

        <% if ("added".equals(wishlistResult)) { %>
        <div class="details-message details-message--success">
            <i class="fa-solid fa-heart"></i>
            Item saved to your wishlist.
        </div>
        <% } else if ("removed".equals(wishlistResult)) { %>
        <div class="details-message details-message--success">
            <i class="fa-regular fa-heart"></i>
            Item removed from your wishlist.
        </div>
        <% } else if ("unavailable".equals(wishlistResult)) { %>
        <div class="details-message details-message--error">
            Only available items can be saved to your wishlist.
        </div>
        <% } else if ("system".equals(wishlistResult)) { %>
        <div class="details-message details-message--error">
            The wishlist could not be updated.
        </div>
        <% } %>

        <% if ("added".equals(cartResult)) { %>
        <div class="details-message details-message--success">
            <i class="fa-solid fa-circle-check"></i>
            Item added to your cart.
        </div>
        <% } else if ("stock".equals(cartResult)) { %>
        <div class="details-message details-message--error">
            The requested quantity exceeds the available stock.
        </div>
        <% } else if ("unavailable".equals(cartResult)) { %>
        <div class="details-message details-message--error">
            This item is no longer available.
        </div>
        <% } else if ("ownItem".equals(cartResult)) { %>
        <div class="details-message details-message--error">
            You cannot add your own listing to your cart.
        </div>
        <% } else if ("system".equals(cartResult)) { %>
        <div class="details-message details-message--error">
            The item could not be added. Please try again.
        </div>
        <% } %>

        <a class="details-back-link"
           href="${pageContext.request.contextPath}/item?action=dashboard">
            <i class="fa-solid fa-arrow-left"></i>
            Back to marketplace
        </a>

        <section class="details-layout">
            <div class="details-photo-panel">
                <img class="details-main-photo"
                     src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= itemPhoto %>"
                     alt="<%= selectedItem.getItemName() %>"
                     onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">
            </div>

            <div class="details-information-panel">
                <div class="details-title-row">
                    <div class="details-badges">
                    <span class="details-category-badge">
                        <%= selectedItem.getCategoryName() != null
                                ? selectedItem.getCategoryName()
                                : "Uncategorised" %>
                    </span>

                    <span class="details-status-badge details-status--<%= itemStatus.toLowerCase().replace(" ", "-") %>">
                        <%= itemStatus %>
                    </span>
                    </div>

                    <% if (!ownItem) { %>
                    <form action="${pageContext.request.contextPath}/wishlist"
                          method="post">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="itemID"
                               value="<%= selectedItem.getItemID() %>">
                        <input type="hidden" name="source" value="details">
                        <button type="submit"
                                class="details-wishlist-button <%= wishlisted ? "is-saved" : "" %>">
                            <i class="<%= wishlisted ? "fa-solid" : "fa-regular" %> fa-heart"></i>
                            <%= wishlisted ? "Saved" : "Save Item" %>
                        </button>
                    </form>
                    <% } %>
                </div>

                <h1><%= selectedItem.getItemName() %></h1>

                <p class="details-price">
                    RM <%= String.format("%.2f", selectedItem.getPrice()) %>
                </p>

                <div class="details-facts">
                    <div class="details-fact">
                        <i class="fa-solid fa-star-half-stroke"></i>
                        <div>
                            <span>Condition</span>
                            <strong><%= selectedItem.getItemCondition() %></strong>
                        </div>
                    </div>

                    <div class="details-fact">
                        <i class="fa-solid fa-box"></i>
                        <div>
                            <span>Available stock</span>
                            <strong><%= selectedItem.getStock() %></strong>
                        </div>
                    </div>

                    <div class="details-fact">
                        <i class="fa-regular fa-calendar"></i>
                        <div>
                            <span>Listed on</span>
                            <strong><%= listedDate %></strong>
                        </div>
                    </div>
                </div>

                <div class="details-description">
                    <h2>Item description</h2>
                    <p>
                        <%= selectedItem.getItemDesc() != null
                                && !selectedItem.getItemDesc().trim().isEmpty()
                                ? selectedItem.getItemDesc()
                                : "The seller has not added a description for this item." %>
                    </p>
                </div>

                <% if (ownItem) { %>
                <div class="details-owner-actions">
                    <p>This item belongs to you.</p>
                    <a href="${pageContext.request.contextPath}/item?action=edit&id=<%= selectedItem.getItemID() %>">
                        <i class="fa-solid fa-pen-to-square"></i>
                        Edit Item
                    </a>
                    <a href="${pageContext.request.contextPath}/item?action=mylistings">
                        <i class="fa-solid fa-boxes-stacked"></i>
                        My Inventory
                    </a>
                </div>
                <% } else if (canBuy) { %>
                <form class="details-cart-panel"
                      action="${pageContext.request.contextPath}/cart"
                      method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="itemID"
                           value="<%= selectedItem.getItemID() %>">
                    <input type="hidden" name="source" value="details">

                    <div class="details-quantity">
                        <span>Quantity</span>
                        <div class="quantity-control">
                            <button type="button"
                                    onclick="changeQuantity(this, -1, <%= selectedItem.getStock() %>)">−</button>
                            <input type="number"
                                   name="quantity"
                                   value="1"
                                   min="1"
                                   max="<%= selectedItem.getStock() %>"
                                   readonly>
                            <button type="button"
                                    onclick="changeQuantity(this, 1, <%= selectedItem.getStock() %>)">+</button>
                        </div>
                    </div>

                    <button type="submit"
                            class="details-add-cart">
                        <i class="fa-solid fa-cart-plus"></i>
                        Add to Cart
                    </button>
                </form>
                <% } else { %>
                <div class="details-unavailable-message">
                    <i class="fa-solid fa-circle-exclamation"></i>
                    This item is currently unavailable for purchase.
                </div>
                <% } %>
            </div>
        </section>

        <section class="seller-summary-card">
            <div class="seller-summary-profile">
                <img src="${pageContext.request.contextPath}/assets/img/userphoto/<%= sellerPhoto %>"
                     alt="Seller profile photo"
                     onerror="this.src='${pageContext.request.contextPath}/assets/img/userphoto/default-user.png'">

                <div>
                    <span class="seller-summary-label">Sold by</span>
                    <h2><%= sellerDisplayName %></h2>
                    <% if (!sellerUsername.isEmpty()) { %>
                    <p>@<%= sellerUsername %></p>
                    <% } %>
                </div>
            </div>

            <div class="seller-summary-university">
                <i class="fa-solid fa-graduation-cap"></i>
                <div>
                    <span>University</span>
                    <strong><%= sellerUniversity %></strong>
                </div>
            </div>

            <% if (seller != null) { %>
            <a class="seller-profile-link"
               href="${pageContext.request.contextPath}/item?action=sellerProfile&sellerID=<%= seller.getUserID() %>">
                View Seller Profile
                <i class="fa-solid fa-arrow-right"></i>
            </a>
            <% } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>