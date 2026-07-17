<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.category"%>
<%@page import="com.barangbaek.bean.item"%>

<%
    List<category> categories = (List<category>) request.getAttribute("categories");
    item formItem = (item) request.getAttribute("item");

    if (formItem == null) {
        formItem = new item();
        formItem.setStock(1);
        formItem.setItemCondition("Good");
    }

    String error = (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Sell an Item | BarangBaek</title>

    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/itemform.css">
</head>

<body class="has-navbar">
    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="item-form-page">
        <div class="item-form-heading">
            <div>
                <span class="item-form-eyebrow">Seller tools</span>
                <h1>Sell an item</h1>
                <p>Create a clear listing so other campus users can find it.</p>
            </div>

            <a class="item-form-back" href="${pageContext.request.contextPath}/item?action=mylistings">
                <i class="fa-solid fa-arrow-left"></i>
                My Inventory
            </a>
        </div>

        <% if (error != null) { %>
            <div class="item-form-alert item-form-alert--error">
                <i class="fa-solid fa-circle-exclamation"></i>
                <%= error %>
            </div>
        <% } %>

        <form id="itemForm" class="item-form-layout" action="${pageContext.request.contextPath}/item" method="post" enctype="multipart/form-data">
            <input type="hidden" name="action" value="add">

            <section class="item-form-card item-photo-card">
                <h2>Item photo</h2>
                <p>Upload one clear image. Maximum size: 5 MB.</p>

                <div class="item-photo-preview-wrap">
                    <img id="itemPhotoPreview" class="item-photo-preview" src="${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png" alt="Item preview">
                </div>

                <label class="item-photo-picker" for="itemPhoto">
                    <i class="fa-solid fa-camera"></i>
                    Choose photo
                </label>

                <input type="file" id="itemPhoto" name="itemPhoto" accept="image/jpeg,image/png,image/webp" onchange="previewItemPhoto(this)">
                <small>JPG, PNG or WEBP.</small>
            </section>

            <section class="item-form-card item-fields-card">
                <div class="item-fields-row">
                    <div class="item-field item-field--wide">
                        <label for="itemName">Item name *</label>
                        <input type="text" id="itemName" name="itemName" maxlength="100"
                        value="<%= escapeHtml(formItem.getItemName()) %>" placeholder="Example: Casio scientific calculator" required>
                    </div>

                    <div class="item-field">
                        <label for="categoryID">Category *</label>
                        <select id="categoryID" name="categoryID" required>
                            <option value="">Choose category</option>
                            <% if (categories != null) {
                                for (category c : categories) { %>
                                <option value="<%= c.getCategoryID() %>" data-description="<%= escapeHtml(c.getCategoryDesc()) %>"
                                    <%= formItem.getCategoryID() == c.getCategoryID() ? "selected" : "" %>>
                                    <%= c.getCategoryName() %>
                                </option>
                            <%  }
                               } %>
                        </select>
                        <small id="categoryDescription" class="category-description">
                            Choose a category to see which items belong in it.
                        </small>
                    </div>
                </div>

                <div class="item-field">
                    <div class="item-label-row">
                        <label for="itemDesc">Description *</label>
                        <span id="descriptionCount">0 / 1000</span>
                    </div>
                    <textarea id="itemDesc" name="itemDesc" maxlength="1000" rows="7" 
                        placeholder="Describe the item, its condition and anything the buyer should know."
                        required><%= escapeHtml(formItem.getItemDesc()) %></textarea>
                </div>

                <div class="item-fields-row">
                    <div class="item-field">
                        <label for="price">Price (RM) *</label>
                        <input type="number" id="price" name="price" min="0.01" max="99999999.99" step="0.01"
                               value="<%= formItem.getPrice() > 0 ? String.format("%.2f", formItem.getPrice()) : "" %>"
                               placeholder="0.00" required>
                    </div>

                    <div class="item-field">
                        <label for="stock">Stock *</label>
                        <input type="number" id="stock" name="stock" min="1" max="9999" 
                        value="<%= formItem.getStock() > 0 ? formItem.getStock() : 1 %>" required>
                    </div>
                </div>

                <fieldset class="item-condition-fieldset">
                    <legend>Condition *</legend>

                    <div class="item-condition-options">
                        <% String[] conditions = { "New", "Like New", "Good", "Fair", "Poor" };

                        for (String condition : conditions) { %>
                            <label class="item-condition-option">
                                <input type="radio" name="itemCondition" value="<%= condition %>"
                                    <%= condition.equals(formItem.getItemCondition()) ? "checked" : "" %> required>
                                <span><%= condition %></span>
                            </label>
                        <% } %>
                    </div>
                </fieldset>

                <div class="item-form-note">
                    <i class="fa-solid fa-circle-info"></i>
                    New listings are automatically published as Available.
                </div>

                <div class="item-form-actions">
                    <a class="item-secondary-button" href="${pageContext.request.contextPath}/item?action=mylistings"> Cancel </a>

                    <button id="itemSubmitButton" class="item-primary-button" type="submit">
                        <i class="fa-solid fa-plus"></i>
                        Publish listing
                    </button>
                </div>
            </section>
        </form>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/itemform.js?v=3"></script>
</body>
</html>

<%!
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&#39;");
    }
%>