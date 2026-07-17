<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.item"%>

<%
    List<item> items = (List<item>) request.getAttribute("items");

    Integer totalCount = (Integer) request.getAttribute("totalCount");
    Integer availableCount = (Integer) request.getAttribute("availableCount");
    Integer soldCount = (Integer) request.getAttribute("soldCount");
    Integer unavailableCount = (Integer) request.getAttribute("unavailableCount");
    Integer outOfStockCount = (Integer) request.getAttribute("outOfStockCount");

    String selectedStatus =
            (String) request.getAttribute("selectedStatus");

    if (selectedStatus == null) {
        selectedStatus = "All";
    }

    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Inventory | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/inventory.css">
</head>
<body class="has-navbar">

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="inventory-page">
        <header class="inventory-header">
            <div>
                <span class="inventory-eyebrow">Buyer and seller account</span>
                <h1>My Inventory</h1>
                <p>Manage every item you list on BarangBaek.</p>
            </div>

            <a class="inventory-add-button"
               href="${pageContext.request.contextPath}/item?action=add">
                <i class="fa-solid fa-plus"></i>
                Sell an Item
            </a>
        </header>

        <% if ("added".equals(success)) { %>
            <div class="inventory-alert inventory-alert--success">
                Item published successfully.
            </div>
        <% } else if ("updated".equals(success)) { %>
            <div class="inventory-alert inventory-alert--success">
                Item updated successfully.
            </div>
        <% } else if ("statusUpdated".equals(success)) { %>
            <div class="inventory-alert inventory-alert--success">
                Item status updated successfully.
            </div>
        <% } else if ("deleted".equals(success)) { %>
            <div class="inventory-alert inventory-alert--success">
                Item permanently deleted.
            </div>
        <% } else if ("deactivated".equals(success)) { %>
            <div class="inventory-alert inventory-alert--info">
                This item has order history, so it was made Unavailable instead of being deleted.
            </div>
        <% } %>

        <% if (error != null) { %>
            <div class="inventory-alert inventory-alert--error">
                <% if ("statusFailed".equals(error)) { %>
                    An item with zero stock cannot be made Available.
                <% } else if ("notOwner".equals(error)) { %>
                    You are not allowed to manage that item.
                <% } else if ("deleteFailed".equals(error)) { %>
                    The listing could not be removed.
                <% } else { %>
                    The requested inventory action could not be completed.
                <% } %>
            </div>
        <% } %>

        <section class="inventory-stats">
            <a class="inventory-stat <%= "All".equals(selectedStatus)
                    ? "inventory-stat--active" : "" %>"
               href="${pageContext.request.contextPath}/item?action=mylistings">
                <span>All listings</span>
                <strong><%= totalCount == null ? 0 : totalCount %></strong>
            </a>

            <a class="inventory-stat <%= "Available".equals(selectedStatus)
                    ? "inventory-stat--active" : "" %>"
               href="${pageContext.request.contextPath}/item?action=mylistings&status=Available">
                <span>Available</span>
                <strong><%= availableCount == null ? 0 : availableCount %></strong>
            </a>

            <a class="inventory-stat <%= "Sold".equals(selectedStatus)
                    ? "inventory-stat--active" : "" %>"
               href="${pageContext.request.contextPath}/item?action=mylistings&status=Sold">
                <span>Sold</span>
                <strong><%= soldCount == null ? 0 : soldCount %></strong>
            </a>

            <a class="inventory-stat <%= "Unavailable".equals(selectedStatus)
                    ? "inventory-stat--active" : "" %>"
               href="${pageContext.request.contextPath}/item?action=mylistings&status=Unavailable">
                <span>Unavailable</span>
                <strong><%= unavailableCount == null ? 0 : unavailableCount %></strong>
            </a>

            <a class="inventory-stat <%= "OutOfStock".equals(selectedStatus)
                    ? "inventory-stat--active" : "" %>"
               href="${pageContext.request.contextPath}/item?action=mylistings&status=OutOfStock">
                <span>Out of stock</span>
                <strong><%= outOfStockCount == null ? 0 : outOfStockCount %></strong>
            </a>
        </section>

        <section class="inventory-list">
            <% if (items != null && !items.isEmpty()) {
                for (item listedItem : items) {
                    String photo = listedItem.getItemPhoto();

                    if (photo == null || photo.trim().isEmpty()) {
                        photo = "default-item.png";
                    }

                    String statusClass = listedItem.getItemStatus()
                            .toLowerCase()
                            .replace(" ", "-");
            %>
                <article class="inventory-item-card">
                    <div class="inventory-photo-wrap">
                        <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= photo %>"
                             onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'"
                             alt="<%= escapeHtml(listedItem.getItemName()) %>">

                        <span class="inventory-status inventory-status--<%= statusClass %>">
                            <%= listedItem.getItemStatus() %>
                        </span>
                    </div>

                    <div class="inventory-item-main">
                        <div class="inventory-item-heading">
                            <div>
                                <span class="inventory-category">
                                    <%= listedItem.getCategoryName() == null
                                            ? "Uncategorised"
                                            : listedItem.getCategoryName() %>
                                </span>
                                <h2><%= escapeHtml(listedItem.getItemName()) %></h2>
                            </div>

                            <strong class="inventory-price">
                                RM <%= String.format("%.2f", listedItem.getPrice()) %>
                            </strong>
                        </div>

                        <div class="inventory-meta">
                            <span>
                                <i class="fa-solid fa-boxes-stacked"></i>
                                Stock: <%= listedItem.getStock() %>
                            </span>
                            <span>
                                <i class="fa-solid fa-star"></i>
                                <%= listedItem.getItemCondition() %>
                            </span>
                            <span>
                                <i class="fa-regular fa-calendar"></i>
                                <%= listedItem.getCreatedAt() == null
                                    ? "-"
                                    : listedItem.getCreatedAt().toString().substring(0, 10) %>
                            </span>
                        </div>

                        <p class="inventory-description">
                            <%= escapeHtml(listedItem.getItemDesc()) %>
                        </p>

                        <div class="inventory-actions">
                            <a class="inventory-action inventory-action--outline"
                               href="${pageContext.request.contextPath}/item?action=details&id=<%= listedItem.getItemID() %>">
                                View Details
                            </a>

                            <a class="inventory-action inventory-action--edit"
                               href="${pageContext.request.contextPath}/item?action=edit&id=<%= listedItem.getItemID() %>">
                                Edit
                            </a>

                            <% if ("Available".equals(listedItem.getItemStatus())) { %>
                                <form action="${pageContext.request.contextPath}/item"
                                      method="post">
                                    <input type="hidden" name="action" value="status">
                                    <input type="hidden" name="itemID"
                                           value="<%= listedItem.getItemID() %>">
                                    <input type="hidden" name="status" value="Unavailable">
                                    <button class="inventory-action inventory-action--warning"
                                            type="submit">
                                        Make Unavailable
                                    </button>
                                </form>
                            <% } else if (listedItem.getStock() > 0) { %>
                                <form action="${pageContext.request.contextPath}/item"
                                      method="post">
                                    <input type="hidden" name="action" value="status">
                                    <input type="hidden" name="itemID"
                                           value="<%= listedItem.getItemID() %>">
                                    <input type="hidden" name="status" value="Available">
                                    <button class="inventory-action inventory-action--success"
                                            type="submit">
                                        Reactivate
                                    </button>
                                </form>
                            <% } %>

                            <% if (!"Sold".equals(listedItem.getItemStatus())) { %>
                                <form action="${pageContext.request.contextPath}/item"
                                      method="post">
                                    <input type="hidden" name="action" value="status">
                                    <input type="hidden" name="itemID"
                                           value="<%= listedItem.getItemID() %>">
                                    <input type="hidden" name="status" value="Sold">
                                    <button class="inventory-action inventory-action--sold"
                                            type="submit">
                                        Mark Sold
                                    </button>
                                </form>
                            <% } %>

                            <form action="${pageContext.request.contextPath}/item"
                                  method="post"
                                  onsubmit="return confirm('Remove this listing? Listings with order history will be made Unavailable instead of deleted.');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="itemID"
                                       value="<%= listedItem.getItemID() %>">
                                <button class="inventory-action inventory-action--delete"
                                        type="submit">
                                    Remove
                                </button>
                            </form>
                        </div>
                    </div>
                </article>
            <%  }
               } else { %>
                <div class="inventory-empty">
                    <i class="fa-solid fa-box-open"></i>
                    <h2>No listings found</h2>
                    <p>Add a new item or choose a different inventory filter.</p>
                    <a href="${pageContext.request.contextPath}/item?action=add">
                        Sell your first item
                    </a>
                </div>
            <% } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
</body>
</html>

<%!
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>