<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.item"%>
<%@page import="com.barangbaek.bean.user"%>
<%
    user seller = (user) request.getAttribute("seller");
    List<item> sellerItems = (List<item>) request.getAttribute("sellerItems");
    Integer loggedInUserID = (Integer) session.getAttribute("userID");

    if (seller == null) {
        response.sendRedirect(request.getContextPath() + "/item?action=dashboard");
        return;
    }

    String sellerPhoto = seller.getUserPhoto();
    if (sellerPhoto == null || sellerPhoto.trim().isEmpty()) {
        sellerPhoto = "default-user.png";
    }

    String sellerName = seller.getFullName();
    if (sellerName == null || sellerName.trim().isEmpty()) {
        sellerName = seller.getUsername();
    }

    String university = seller.getUniversity();
    if (university == null || university.trim().isEmpty()) {
        university = "University not provided";
    }

    int totalItems = sellerItems != null ? sellerItems.size() : 0;
    int availableItems = 0;
    int soldItems = 0;
    int unavailableItems = 0;

    if (sellerItems != null) {
        for (item sellerItem : sellerItems) {
            String status = sellerItem.getItemStatus();
            if ("Available".equalsIgnoreCase(status)
                    && sellerItem.getStock() > 0) {
                availableItems++;
            } else if ("Sold".equalsIgnoreCase(status)
                    || sellerItem.getStock() <= 0) {
                soldItems++;
            } else {
                unavailableItems++;
            }
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= sellerName %> | BarangBaek Seller</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/sellerprofile.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="seller-page">
        <a class="seller-back-link"
           href="${pageContext.request.contextPath}/item?action=dashboard">
            <i class="fa-solid fa-arrow-left"></i>
            Back to marketplace
        </a>

        <section class="seller-header-card">
            <img class="seller-header-photo"
                 src="${pageContext.request.contextPath}/assets/img/userphoto/<%= sellerPhoto %>"
                 alt="<%= sellerName %> profile photo"
                 onerror="this.src='${pageContext.request.contextPath}/assets/img/userphoto/default-user.png'">

            <div class="seller-header-info">
                <span class="seller-header-label">BarangBaek seller</span>
                <h1><%= sellerName %></h1>
                <p class="seller-header-username">@<%= seller.getUsername() %></p>
                <p class="seller-header-university">
                    <i class="fa-solid fa-graduation-cap"></i>
                    <%= university %>
                </p>
            </div>

            <% if (loggedInUserID != null
                    && loggedInUserID == seller.getUserID()) { %>
            <a class="seller-own-profile-link"
               href="${pageContext.request.contextPath}/profile?action=view">
                <i class="fa-solid fa-user-pen"></i>
                Edit My Profile
            </a>
            <% } %>
        </section>

        <section class="seller-statistics">
            <div class="seller-stat-card">
                <strong><%= totalItems %></strong>
                <span>Total listings</span>
            </div>
            <div class="seller-stat-card seller-stat-card--available">
                <strong><%= availableItems %></strong>
                <span>Available</span>
            </div>
            <div class="seller-stat-card seller-stat-card--sold">
                <strong><%= soldItems %></strong>
                <span>Sold or out of stock</span>
            </div>
            <div class="seller-stat-card seller-stat-card--unavailable">
                <strong><%= unavailableItems %></strong>
                <span>Unavailable</span>
            </div>
        </section>

        <section class="seller-listings-heading">
            <div>
                <span>Seller inventory</span>
                <h2>All Listings</h2>
            </div>
            <p>Available, sold and unavailable items are shown here.</p>
        </section>

        <section class="seller-item-list">
            <% if (sellerItems != null && !sellerItems.isEmpty()) {
                for (item sellerItem : sellerItems) {
                    String photo = sellerItem.getItemPhoto();
                    if (photo == null || photo.trim().isEmpty()) {
                        photo = "default-item.png";
                    }

                    String status = sellerItem.getItemStatus();
                    if (sellerItem.getStock() <= 0
                            && "Available".equalsIgnoreCase(status)) {
                        status = "Out of Stock";
                    }
            %>
            <article class="seller-item-card">
                <div class="seller-item-photo-wrap">
                    <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= photo %>"
                         alt="<%= sellerItem.getItemName() %>"
                         onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">
                    <span class="seller-item-status seller-item-status--<%= status.toLowerCase().replace(" ", "-") %>">
                        <%= status %>
                    </span>
                </div>

                <div class="seller-item-body">
                    <span class="seller-item-category">
                        <%= sellerItem.getCategoryName() != null
                                ? sellerItem.getCategoryName()
                                : "Uncategorised" %>
                    </span>
                    <h3><%= sellerItem.getItemName() %></h3>
                    <p class="seller-item-price">
                        RM <%= String.format("%.2f", sellerItem.getPrice()) %>
                    </p>
                    <p class="seller-item-condition">
                        <i class="fa-solid fa-star-half-stroke"></i>
                        <%= sellerItem.getItemCondition() %>
                    </p>

                    <a href="${pageContext.request.contextPath}/item?action=details&id=<%= sellerItem.getItemID() %>">
                        Item Details
                    </a>
                </div>
            </article>
            <%  }
               } else { %>
            <div class="seller-empty-state">
                <i class="fa-solid fa-box-open"></i>
                <h3>No listings yet</h3>
                <p>This seller has not listed any items.</p>
            </div>
            <% } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>