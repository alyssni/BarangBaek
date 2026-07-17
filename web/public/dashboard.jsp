<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.item"%>
<%@page import="com.barangbaek.bean.category"%>
<%!
    private String html(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>
<%
    List<item> items = (List<item>) request.getAttribute("items");
    List<category> categories = (List<category>) request.getAttribute("categories");
    String keyword = (String) request.getAttribute("keyword");
    Integer selectedCategoryID = (Integer) request.getAttribute("selectedCategoryID");
    String selectedCondition = (String) request.getAttribute("selectedCondition");
    Double minimumPrice = (Double) request.getAttribute("minimumPrice");
    Double maximumPrice = (Double) request.getAttribute("maximumPrice");
    String selectedSort = (String) request.getAttribute("selectedSort");
    Integer resultCount = (Integer) request.getAttribute("resultCount");

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
    
    <title>Marketplace | BarangBaek</title>

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/marketplace.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard-enhancements.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/wishlist.css">
</head>
<body>
    <%@ include file="/WEB-INF/jspf/guest-navbar.jspf" %>

    <main class="market-page">
        <section class="market-hero">
            <div>
                <span class="market-eyebrow">Campus marketplace</span>
                <h1>Find useful items from students around you.</h1>
                <p>Search and compare listings before creating your BarangBaek account.</p>
            </div>

            <a class="market-primary-link" href="${pageContext.request.contextPath}/auth?action=registerForm">
                Create Account
                <i class="fa-solid fa-arrow-right"></i>
            </a>
        </section>

        <% if (request.getAttribute("error") != null) { %>
        <div class="market-alert market-alert--error">
            <i class="fa-solid fa-circle-exclamation"></i>
            <%= request.getAttribute("error") %>
        </div>
        <% } %>

        <section class="discovery-panel" aria-labelledby="guestDiscoveryTitle">
            <div class="discovery-panel__heading">
                <div>
                    <span class="market-eyebrow">Smart discovery</span>
                    <h2 id="guestDiscoveryTitle">Find Items Within Your Budget</h2>
                    <p>Guests can search, filter and compare all currently available listings.</p>
                </div>

                <div class="quick-filter-chips" aria-label="Quick filters">
                    <a href="${pageContext.request.contextPath}/public?action=dashboard&maxPrice=50&sort=priceLow">
                        <i class="fa-solid fa-tag"></i> Under RM50
                    </a>
                    <a href="${pageContext.request.contextPath}/public?action=dashboard&condition=Like+New">
                        <i class="fa-solid fa-sparkles"></i> Like New
                    </a>
                    <a href="${pageContext.request.contextPath}/public?action=dashboard&condition=New">
                        <i class="fa-solid fa-box-open"></i> New Items
                    </a>
                    <a href="${pageContext.request.contextPath}/public?action=dashboard&sort=priceLow">
                        <i class="fa-solid fa-arrow-down-wide-short"></i> Lowest Price
                    </a>
                </div>
            </div>

            <form class="advanced-filter-form"
                  action="${pageContext.request.contextPath}/public"
                  method="get">
                <input type="hidden" name="action" value="dashboard">
                <input type="hidden" name="keyword" value="<%= html(keyword) %>">

                <div class="filter-field">
                    <label for="guestFilterCategory">Category</label>
                    <select id="guestFilterCategory" name="categoryID">
                        <option value="">All Categories</option>
                        <% if (categories != null) {
                            for (category c : categories) { %>
                        <option value="<%= c.getCategoryID() %>" title="<%= html(c.getCategoryDesc()) %>"
                            <%= selectedCategoryID != null && selectedCategoryID == c.getCategoryID() ? "selected" : "" %>>
                            <%= html(c.getCategoryName()) %>
                        </option>
                        <%  }
                           } %>
                    </select>
                </div>

                <div class="filter-field">
                    <label for="guestFilterCondition">Condition</label>
                    <select id="guestFilterCondition" name="condition">
                        <option value="">All Conditions</option>
                        <% String[] conditions = {"New", "Like New", "Good", "Fair", "Poor"};
                           for (String condition : conditions) { %>
                        <option value="<%= condition %>"
                                <%= condition.equals(selectedCondition)
                                        ? "selected" : "" %>>
                            <%= condition %>
                        </option>
                        <% } %>
                    </select>
                </div>

                <div class="filter-field">
                    <label for="guestMinimumPrice">Minimum Price</label>
                    <div class="price-input-wrap">
                        <span>RM </span>
                        <input id="guestMinimumPrice" type="number" name="minPrice" min="0" step="0.01" 
                        value="<%= minimumPrice != null ? String.format("%.2f", minimumPrice) : "" %>" placeholder="0.00">
                    </div>
                </div>

                <div class="filter-field">
                    <label for="guestMaximumPrice">Maximum Price</label>
                    <div class="price-input-wrap">
                        <span>RM</span>
                        <input id="guestMaximumPrice" type="number" name="maxPrice" min="0" step="0.01"
                        value="<%= maximumPrice != null ? String.format("%.2f", maximumPrice) : "" %>" placeholder="500.00">
                    </div>
                </div>

                <div class="filter-field">
                    <label for="guestSortItems">Sort By</label>
                    <select id="guestSortItems" name="sort">
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
                    <a class="filter-clear-button" href="${pageContext.request.contextPath}/public?action=dashboard">
                        Clear
                    </a>
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
            <a class="market-reset-link" href="${pageContext.request.contextPath}/public?action=dashboard">
                Clear all filters
            </a>
            <% } %>
        </section>

        <% if (!keyword.isEmpty()) { %>
        <p class="market-results-note"> Showing results for <strong>“<%= html(keyword) %>”</strong> </p>
        <% } %>

        <section class="item-grid" aria-label="Available marketplace items">
            <% if (items != null && !items.isEmpty()) {
                for (item i : items) {
                    String photo = i.getItemPhoto();
                    if (photo == null || photo.trim().isEmpty()) {
                        photo = "default-item.png";
                    }
            %>
            
            <article class="item-card">
                <div class="item-card__image-wrap">
                    <a class="item-card__image-link" href="${pageContext.request.contextPath}/auth?action=login" aria-label="Sign in to view <%= html(i.getItemName()) %>">
                        <img src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= html(photo) %>" alt="<%= html(i.getItemName()) %>"
                        onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'">
                    </a>

                    <a class="wishlist-heart-button guest-wishlist-heart" href="${pageContext.request.contextPath}/auth?action=login" title="Sign in to save this item" aria-label="Sign in to save this item">
                        <i class="fa-regular fa-heart"></i>
                    </a>

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

                    <a class="item-card__guest-action" href="${pageContext.request.contextPath}/auth?action=login"> Sign in to view item </a>
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