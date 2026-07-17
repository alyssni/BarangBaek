<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.List"%>
<%@page import="com.barangbaek.bean.category"%>
<%@page import="com.barangbaek.bean.item"%>

<%
    List<category> categories =
            (List<category>) request.getAttribute("categories");

    item formItem = (item) request.getAttribute("item");

    if (formItem == null) {
        response.sendRedirect(
                request.getContextPath()
                + "/item?action=mylistings"
        );
        return;
    }

    String photoName = formItem.getItemPhoto();

    if (photoName == null || photoName.trim().isEmpty()) {
        photoName = "default-item.png";
    }

    String errorCode = request.getParameter("error");
    String error = (String) request.getAttribute("error");

    if (error == null && "photo".equals(errorCode)) {
        error = "The selected item photo is invalid.";
    } else if (error == null && "failed".equals(errorCode)) {
        error = "The listing could not be updated.";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Edit Item | BarangBaek</title>

    <link rel="icon" type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/itemform.css">
</head>
<body class="has-navbar">

    <%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

    <main class="item-form-page">
        <div class="item-form-heading">
            <div>
                <span class="item-form-eyebrow">Inventory management</span>
                <h1>Edit listing</h1>
                <p>Update the item information, stock and marketplace status.</p>
            </div>

            <a class="item-form-back"
               href="${pageContext.request.contextPath}/item?action=mylistings">
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

        <form id="itemForm"
              class="item-form-layout"
              action="${pageContext.request.contextPath}/item"
              method="post"
              enctype="multipart/form-data">

            <input type="hidden" name="action" value="edit">
            <input type="hidden" name="itemID"
                   value="<%= formItem.getItemID() %>">

            <section class="item-form-card item-photo-card">
                <h2>Item photo</h2>
                <p>Leave the upload empty to keep the current photo.</p>

                <div class="item-photo-preview-wrap">
                    <img id="itemPhotoPreview"
                         class="item-photo-preview"
                         src="${pageContext.request.contextPath}/assets/img/itemphoto/<%= photoName %>"
                         onerror="this.src='${pageContext.request.contextPath}/assets/img/itemphoto/default-item.png'"
                         alt="Item preview">
                </div>

                <label class="item-photo-picker" for="itemPhoto">
                    <i class="fa-solid fa-camera"></i>
                    Replace photo
                </label>

                <input type="file"
                       id="itemPhoto"
                       name="itemPhoto"
                       accept="image/jpeg,image/png,image/webp"
                       onchange="previewItemPhoto(this)">

                <small>JPG, PNG or WEBP. Maximum 5 MB.</small>
            </section>

            <section class="item-form-card item-fields-card">
                <div class="item-fields-row">
                    <div class="item-field item-field--wide">
                        <label for="itemName">Item name *</label>
                        <input type="text"
                               id="itemName"
                               name="itemName"
                               maxlength="100"
                               value="<%= escapeHtml(formItem.getItemName()) %>"
                               required>
                    </div>

                    <div class="item-field">
                        <label for="categoryID">Category *</label>
                        <select id="categoryID" name="categoryID" required>
                            <option value="">Choose category</option>
                            <% if (categories != null) {
                                for (category c : categories) { %>
                                <option value="<%= c.getCategoryID() %>"
                                        data-description="<%= escapeHtml(c.getCategoryDesc()) %>"
                                    <%= formItem.getCategoryID() == c.getCategoryID()
                                            ? "selected" : "" %>>
                                    <%= c.getCategoryName() %>
                                </option>
                            <%  }
                               } %>
                        </select>
                        <small id="categoryDescription"
                               class="category-description">
                            Choose a category to see which items belong in it.
                        </small>
                    </div>
                </div>

                <div class="item-field">
                    <div class="item-label-row">
                        <label for="itemDesc">Description *</label>
                        <span id="descriptionCount">0 / 1000</span>
                    </div>
                    <textarea id="itemDesc"
                              name="itemDesc"
                              maxlength="1000"
                              rows="7"
                              required><%= escapeHtml(formItem.getItemDesc()) %></textarea>
                </div>

                <div class="item-fields-row item-fields-row--three">
                    <div class="item-field">
                        <label for="price">Price (RM) *</label>
                        <input type="number"
                               id="price"
                               name="price"
                               min="0.01"
                               max="99999999.99"
                               step="0.01"
                               value="<%= String.format("%.2f", formItem.getPrice()) %>"
                               required>
                    </div>

                    <div class="item-field">
                        <label for="stock">Stock *</label>
                        <input type="number"
                               id="stock"
                               name="stock"
                               min="0"
                               max="9999"
                               value="<%= formItem.getStock() %>"
                               required>
                        <small>Stock 0 will mark the item as Sold.</small>
                    </div>

                    <div class="item-field">
                        <label for="itemStatus">Status *</label>
                        <select id="itemStatus"
                                name="itemStatus"
                                required>
                            <option value="Available"
                                <%= "Available".equals(formItem.getItemStatus())
                                    ? "selected" : "" %>>
                                Available
                            </option>
                            <option value="Unavailable"
                                <%= "Unavailable".equals(formItem.getItemStatus())
                                    ? "selected" : "" %>>
                                Unavailable
                            </option>
                            <option value="Sold"
                                <%= "Sold".equals(formItem.getItemStatus())
                                    ? "selected" : "" %>>
                                Sold
                            </option>
                        </select>
                    </div>
                </div>

                <fieldset class="item-condition-fieldset">
                    <legend>Condition *</legend>

                    <div class="item-condition-options">
                        <% String[] conditions = {
                            "New", "Like New", "Good", "Fair", "Poor"
                        };

                        for (String condition : conditions) { %>
                            <label class="item-condition-option">
                                <input type="radio"
                                       name="itemCondition"
                                       value="<%= condition %>"
                                       <%= condition.equals(formItem.getItemCondition())
                                            ? "checked" : "" %>
                                       required>
                                <span><%= condition %></span>
                            </label>
                        <% } %>
                    </div>
                </fieldset>

                <div class="item-form-note">
                    <i class="fa-solid fa-circle-info"></i>
                    Unavailable and Sold items are removed from customer carts.
                </div>

                <div class="item-form-actions">
                    <a class="item-secondary-button"
                       href="${pageContext.request.contextPath}/item?action=mylistings">
                        Cancel
                    </a>

                    <button id="itemSubmitButton"
                            class="item-primary-button"
                            type="submit">
                        <i class="fa-solid fa-floppy-disk"></i>
                        Save changes
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

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>