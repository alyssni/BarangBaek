<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="com.barangbaek.bean.user"%>

<%!
    private String h(String value) {
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

    private String showValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Not provided";
        }

        return h(value);
    }
%>

<%
    user profileUser = (user) request.getAttribute("user");

    if (profileUser == null) {
        response.sendRedirect(
                request.getContextPath() + "/profile?action=view"
        );
        return;
    }

    String profilePhoto = profileUser.getUserPhoto();

    if (profilePhoto == null || profilePhoto.trim().isEmpty()) {
        profilePhoto = "default-user.png";
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>My Profile | BarangBaek</title>

    <link rel="icon"
          type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">


    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/profile.css?v=2">
</head>

<body class="has-navbar">

<%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

<main class="profile-page">

    <% if (request.getAttribute("success") != null) { %>
    <div class="profile-alert profile-alert--success">
        <i class="fa-solid fa-circle-check"></i>
        <span><%= h(String.valueOf(request.getAttribute("success"))) %></span>
    </div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
    <div class="profile-alert profile-alert--error">
        <i class="fa-solid fa-circle-exclamation"></i>
        <span><%= h(String.valueOf(request.getAttribute("error"))) %></span>
    </div>
    <% } %>

    <section class="profile-banner">
        <div class="profile-banner__photo">
            <img src="${pageContext.request.contextPath}/assets/img/userphoto/<%= h(profilePhoto) %>"
                 alt="<%= h(profileUser.getFullName()) %> profile photo"
                 onerror="this.src='${pageContext.request.contextPath}/assets/img/userphoto/default-user.png'">
        </div>

        <div class="profile-banner__identity">
            <span class="profile-kicker">My profile</span>
            <h1><%= showValue(profileUser.getFullName()) %></h1>
            <p class="profile-username">@<%= showValue(profileUser.getUsername()) %></p>

            <div class="profile-banner__meta">
                <span>
                    <i class="fa-solid fa-graduation-cap"></i>
                    <%= showValue(profileUser.getUniversity()) %>
                </span>
                <span>
                    <i class="fa-solid fa-location-dot"></i>
                    <%= showValue(profileUser.getCity()) %>,
                    <%= showValue(profileUser.getState()) %>
                </span>
            </div>
        </div>

        <a class="profile-edit-button"
           href="${pageContext.request.contextPath}/profile?action=edit">
            <i class="fa-solid fa-user-pen"></i>
            Edit Profile
        </a>
    </section>

    <section class="profile-section-heading">
        <div>
            <span class="profile-kicker">Private account</span>
            <h2>Your information</h2>
            <p>Only you can view the personal information shown on this page.</p>
        </div>
    </section>

    <div class="profile-card-row">
        <section class="profile-card profile-card--personal">
            <div class="profile-card__title">
                <span class="profile-card__icon">
                    <i class="fa-regular fa-address-card"></i>
                </span>
                <div>
                    <h3>Personal details</h3>
                    <p>Your main account information</p>
                </div>
            </div>

            <dl class="profile-detail-list">
                <div>
                    <dt>User ID</dt>
                    <dd>#<%= profileUser.getUserID() %></dd>
                </div>
                <div>
                    <dt>Full name</dt>
                    <dd><%= showValue(profileUser.getFullName()) %></dd>
                </div>
                <div>
                    <dt>Username</dt>
                    <dd>@<%= showValue(profileUser.getUsername()) %></dd>
                </div>
                <div>
                    <dt>Birthday</dt>
                    <dd><%= showValue(profileUser.getBirthday()) %></dd>
                </div>
                <div>
                    <dt>Gender</dt>
                    <dd><%= showValue(profileUser.getGender()) %></dd>
                </div>
            </dl>
        </section>

        <section class="profile-card profile-card--contact">
            <div class="profile-card__title">
                <span class="profile-card__icon">
                    <i class="fa-solid fa-envelope-open-text"></i>
                </span>
                <div>
                    <h3>Contact and campus</h3>
                    <p>How your account is identified</p>
                </div>
            </div>

            <dl class="profile-detail-list">
                <div>
                    <dt>Education email</dt>
                    <dd><%= showValue(profileUser.getEmail()) %></dd>
                </div>
                <div>
                    <dt>Phone number</dt>
                    <dd><%= showValue(profileUser.getPhone()) %></dd>
                </div>
                <div>
                    <dt>University</dt>
                    <dd><%= showValue(profileUser.getUniversity()) %></dd>
                </div>
            </dl>

            <div class="profile-readonly-note">
                <i class="fa-solid fa-lock"></i>
                Email, birthday and gender are fixed account details.
            </div>
        </section>
    </div>

    <section class="profile-address-card">
        <div class="profile-address-card__heading">
            <span class="profile-card__icon">
                <i class="fa-solid fa-house"></i>
            </span>
            <div>
                <h3>Saved delivery address</h3>
                <p>This address is used during courier checkout.</p>
            </div>
        </div>

        <div class="profile-address-card__body">
            <strong><%= showValue(profileUser.getFullName()) %></strong>
            <p><%= showValue(profileUser.getAddress1()) %></p>

            <% if (profileUser.getAddress2() != null
                    && !profileUser.getAddress2().trim().isEmpty()) { %>
            <p><%= h(profileUser.getAddress2()) %></p>
            <% } %>

            <p>
                <%= showValue(profileUser.getPostcode()) %>
                <%= showValue(profileUser.getCity()) %>
            </p>
            <p><%= showValue(profileUser.getState()) %></p>

            <span class="profile-address-card__phone">
                <i class="fa-solid fa-phone"></i>
                <%= showValue(profileUser.getPhone()) %>
            </span>
        </div>
    </section>
</main>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>

</body>
</html>