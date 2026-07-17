<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Set"%>
<%@page import="com.barangbaek.bean.item"%>
<%@page import="com.barangbaek.bean.category"%>

<%!
    private String html(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
    List<item> items = (List<item>) request.getAttribute("items");
    List<category> categories = (List<category>) request.getAttribute("categories");
    Set<Integer> wishlistItemIDs = (Set<Integer>) request.getAttribute("wishlistItemIDs");
    String keyword = (String) request.getAttribute("keyword");
    Integer selectedCategoryID = (Integer) request.getAttribute("selectedCategoryID");
    String selectedCondition = (String) request.getAttribute("selectedCondition");
    Double minimumPrice = (Double) request.getAttribute("minimumPrice");
    Double maximumPrice = (Double) request.getAttribute("maximumPrice");
    String selectedSort = (String) request.getAttribute("selectedSort");
    Integer resultCount = (Integer) request.getAttribute("resultCount");
    Integer loggedInUserID = (Integer) session.getAttribute("userID");

    if (keyword == null) keyword = "";
    if (selectedCondition == null) selectedCondition = "";
    if (selectedSort == null) selectedSort = "newest";
    if (resultCount == null) resultCount = items != null ? items.size() : 0;

    boolean filtersActive = !keyword.isEmpty()
            || selectedCategoryID != null
            || !selectedCondition.isEmpty()
            || minimumPrice != null
            || maximumPrice != null
            || !"newest".equals(selectedSort);
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard | BarangBaek</title>

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/marketplace.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-enhancements.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wishlist.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="market-page">
        <section class="market-hero market-hero--user">
            <div>
                <span class="market-eyebrow">Welcome back</span>
                <h1>Buy what you need. Sell what you no longer use.</h1>
                <p>Discover affordable campus essentials and save your favourites for later.</p>
            </div>

            <a class="market-primary-link" href="${pageContext.request.contextPath}/item?action=add">
                <i class="fa-solid fa-circle-plus"></i>
                Sell an Item
            </a>
        </section>

        <%
            String cartResult = request.getParameter("cart");
            String wishlistResult = request.getParameter("wishlist");
        %>

        <% if ("added".equals(cartResult)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-solid fa-circle-check"></i>
            Item added to your cart.
        </div>
        
        <% } else if ("quantity".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            Select at least one item before adding it to your cart.
        </div>
        
        <% } else if ("stock".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            The requested quantity exceeds the available stock.
        </div>
        
        <% } else if ("ownItem".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            You cannot add your own listing to your cart.
        </div>
        <% } else if ("unavailable".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            That item is no longer available.
        </div>
        
        <% } else if ("system".equals(cartResult)) { %>
        <div class="market-alert market-alert--error">
            The item could not be added. Please try again.
        </div>
        <% } %>

        
        <% if ("added".equals(wishlistResult)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-solid fa-heart"></i>
            Item saved to your wishlist.
        </div>
        
        <% } else if ("removed".equals(wishlistResult)) { %>
        <div class="market-alert market-alert--success">
            <i class="fa-regular fa-heart"></i>
            Item removed from your wishlist.
        </div>
        
        <% } else if ("ownItem".equals(wishlistResult)) { %>
        <div class="market-alert market-alert--error">
            Your own listing cannot be added to your wishlist.
        </div>
        
        <% } else if ("unavailable".equals(wishlistResult)) { %>
        <div class="market-alert market-alert--error">
            Only available items can be added to your wishlist.
        </div>
        
        <% } else if ("system".equals(wishlistResult)) { %>
        <div class="market-alert market-alert--error">
            The wishlist could not be updated. Please try again.
        </div>
        <% } %>

        
        <% if (request.getAttribute("error") != null) { %>
        <div class="market-alert market-alert--error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <section class="discovery-panel" aria-labelledby="discoveryTitle">
            <div class="discovery-panel__heading">
                <div>
                    <span class="market-eyebrow">Smart discovery</span>
                    <h2 id="discoveryTitle">Find Your Best Match</h2>
                    <p>Filter by budget, condition and category, then choose how results are sorted.</p>
                </div>

                <div class="quick-filter-chips" aria-label="Quick filters">
                    <a href="${pageContext.request.contextPath}/item?action=dashboard&maxPrice=50&sort=priceLow">
                        <i class="fa-solid fa-tag"></i> Under RM50
                    </a>
                    <a href="${pageContext.request.contextPath}/item?action=dashboard&condition=Like+New">
                        <i class="fa-solid fa-sparkles"></i> Like New
                    </a>
                    <a href="${pageContext.request.contextPath}/item?action=dashboard&condition=New">
                        <i class="fa-solid fa-box-open"></i> New Items
                    </a>
                    <a href="${pageContext.request.contextPath}/item?action=dashboard&sort=priceLow">
                        <i class="fa-solid fa-arrow-down-wide-short"></i> Lowest Price
                    </a>
                </div>
            </div>

            <form class="advanced-filter-form" action="${pageContext.request.contextPath}/item" method="get">
                <input type="hidden" name="action" value="dashboard">
                <input type="hidden" name="keyword" value="<%= html(keyword) %>">

                <div class="filter-field">
                    <label for="filterCategory">Category</label>
                    <select id="filterCategory" name="categoryID">
                        <option value="">All Categories</option>
                        <% if (categories != null) {
                            for (category c : categories) { %>
                        <option value="<%= c.getCategoryID() %>"
                                title="<%= html(c.getCategoryDesc()) %>"
                                <%= selectedCategoryID != null
                                        && selectedCategoryID == c.getCategoryID()
                                        ? "selected" : "" %>>
                            <%= html(c.getCategoryName()) %>
                        </option>
                        <%  }
                           } %>
                    </select>
                </div>

                <div class="filter-field">
                    <label for="filterCondition">Condition</label>
                    <select id="filterCondition" name="condition">
                        <option value="">All Conditions</option>
                        <% String[] conditions = {"New", "Like New", "Good", "Fair", "Poor"};
                           for (String condition : conditions) { %>
                        <option value="<%= condition %>"
                                <%= condition.equals(selectedCondition) ? "selected" : "" %>>
                            <%= condition %>
                        </option>
                        <% } %>
                    </select>
                </div>

                <div class="filter-field">
                    <label for="minimumPrice">Minimum Price</label>
                    <div class="price-input-wrap">
                        <span>RM</span>
                        <input id="minimumPrice" type="number" name="minPrice" min="0" step="0.01" 
                               value="<%= minimumPrice != null ? String.format("%.2f", minimumPrice) : "" %>" placeholder="0.00">
                    </div>
                </div>

                <div class="filter-field">
                    <label for="maximumPrice">Maximum Price</label>
                    <div class="price-input-wrap">
                        <span>RM</span>
                        <input id="maximumPrice" type="number" name="maxPrice" min="0" step="0.01"
                        value="<%= maximumPrice != null ? String.format("%.2f", maximumPrice) : "" %>" placeholder="500.00">
                    </div>
                </div>

                <div class="filter-field">
                    <label for="sortItems">Sort By</label>
                    <select id="sortItems" name="sort">
                        <option value="newest" <%= "newest".equals(selectedSort) ? "selected" : "" %>>Newest First</option>
                        <option value="oldest" <%= "oldest".equals(selectedSort) ? "selected" : "" %>>Oldest First</option>
                        <option value="priceLow" <%= "priceLow".equals(selectedSort) ? "selected" : "" %>>Price: Low to High</option>
                        <option value="priceHigh" <%= "priceHigh".equals(selectedSort) ? "selected" : "" %>>Price: High to Low</option>
                        <option value="nameAZ" <%= "nameAZ".equals(selectedSort) ? "selected" : "" %>>Name: A to Z</option>
                    </select>
                </div>

                <div class="filter-actions">
                    <button type="submit" class="filter-apply-button">
                        <i class="fa-solid fa-sliders"></i>
                        Apply Filters
                    </button>
                    <a class="filter-clear-button" href="${pageContext.request.contextPath}/item?action=dashboard"> Clear </a>
                </div>
            </form>
        </section>

        <section class="market-section-heading">
            <div>
                <span class="market-eyebrow">Available now</span>
                <h2>Browse Items</h2>
                <p class="market-result-count">
                    <strong><%= resultCount %></strong>
                    <%= resultCount == 1 ? "item" : "items" %> found
                </p>
            </div>

            <% if (filtersActive) { %>
            <a class="market-reset-link"
               href="${pageContext.request.contextPath}/item?action=dashboard">
                Clear all filters
            </a>
            <% } %>
        </section>

        <% if (!keyword.isEmpty()) { %>
        <p class="market-results-note">
            Showing results for <strong>“<%= html(keyword) %>”</strong>
        </p>
        <% } %>

        <section class="item-grid" aria-label="Available marketplace items">
            <% if (items != null && !items.isEmpty()) {
                for (item i : items) {
                    String photo = i.getItemPhoto();
                    if (photo == null || photo.trim().isEmpty()) {
                        photo = "default-item.png";
                    }
                    boolean ownItem = loggedInUserID != null
                            && loggedInUserID == i.getSellerID();
                    boolean wishlisted = wishlistItemIDs != null
                            && wishlistItemIDs.contains(i.getItemID());
            %>
            <article class="item-card">
                <div class="item-card__image-wrap">
                    <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= html(photo) %>"
                         alt="<%= html(i.getItemName()) %>"
                         onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">

                    <% if (!ownItem) { %>
                    <form class="wishlist-heart-form" action="${pageContext.request.contextPath}/wishlist" method="post">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="itemID" value="<%= i.getItemID() %>">
                        <input type="hidden" name="source" value="dashboard">
                        <button type="submit" class="wishlist-heart-button <%= wishlisted ? "is-saved" : "" %>"
                        title="<%= wishlisted ? "Remove from wishlist" : "Save to wishlist" %>"
                        aria-label="<%= wishlisted ? "Remove from wishlist" : "Save to wishlist" %>">
                            <i class="<%= wishlisted ? "fa-solid" : "fa-regular" %> fa-heart"></i>
                        </button>
                    </form>
                    <% } %>

                    <span class="item-card__condition"><%= html(i.getItemCondition()) %></span>
                </div>

                <div class="item-card__body">
                    <span class="item-card__category">
                        <%= i.getCategoryName() != null ? html(i.getCategoryName()) : "Uncategorised" %>
                    </span>
                    <h3><%= html(i.getItemName()) %></h3>
                    <p class="item-card__price">
                        RM <%= String.format("%.2f", i.getPrice()) %>
                    </p>
                    <p class="item-card__stock">
                        <i class="fa-solid fa-box"></i>
                        <%= i.getStock() %> available
                    </p>

                    <div class="item-card__actions">
                        <a class="item-details-button" href="${pageContext.request.contextPath}/item?action=details&id=<%= i.getItemID() %>">
                            Item Details
                        </a>

                        <% if (ownItem) { %>
                        <a class="item-manage-button" href="${pageContext.request.contextPath}/item?action=edit&id=<%= i.getItemID() %>">
                            Manage Item
                        </a>
                            
                        <% } else { %>
                        <form class="item-cart-row" action="${pageContext.request.contextPath}/cart" method="post">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="itemID" value="<%= i.getItemID() %>">
                            <input type="hidden" name="source" value="dashboard">

                            <div class="quantity-control" aria-label="Quantity selector">
                                <button type="button" onclick="changeQuantity(this, -1, <%= i.getStock() %>)">−</button>
                                <input type="number" name="quantity" value="0" min="0" max="<%= i.getStock() %>" readonly>
                                <button type="button" onclick="changeQuantity(this, 1, <%= i.getStock() %>)">+</button>
                            </div>

                            <button type="submit" class="add-cart-button" disabled>
                                <i class="fa-solid fa-cart-plus"></i>
                                Add
                            </button>
                        </form>
                        <% } %>
                    </div>
                </div>
            </article>
            <%  }
               } else { %>
            <div class="market-empty-state">
                <i class="fa-solid fa-box-open"></i>
                <h3>No available items found</h3>
                <p>Try adjusting the category, condition, budget or sort options.</p>
            </div>
            <% } %>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js?v=5"></script>
</body>
</html>