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

    private String selected(String current, String option) {
        return option.equals(current) ? "selected" : "";
    }
%>

<%
    user profileUser = (user) request.getAttribute("user");

    if (profileUser == null) {
        response.sendRedirect(
                request.getContextPath() + "/profile?action=edit"
        );
        return;
    }

    String profilePhoto = profileUser.getUserPhoto();

    if (profilePhoto == null || profilePhoto.trim().isEmpty()) {
        profilePhoto = "default-user.png";
    }

    String[] universities = {
        "Universiti Malaya (UM)",
        "Universiti Sains Malaysia (USM)",
        "Universiti Kebangsaan Malaysia (UKM)",
        "Universiti Putra Malaysia (UPM)",
        "Universiti Teknologi MARA (UiTM)",
        "Universiti Teknologi Malaysia (UTM)",
        "Universiti Utara Malaysia (UUM)",
        "Universiti Islam Antarabangsa Malaysia (UIAM)",
        "Universiti Malaysia Sarawak (UNIMAS)",
        "Universiti Malaysia Sabah (UMS)",
        "Universiti Pendidikan Sultan Idris (UPSI)",
        "Universiti Sains Islam Malaysia (USIM)",
        "Universiti Malaysia Terengganu (UMT)",
        "Universiti Tun Hussein Onn Malaysia (UTHM)",
        "Universiti Teknikal Malaysia Melaka (UTeM)",
        "Universiti Malaysia Pahang Al-Sultan Abdullah (UMPSA)",
        "Universiti Malaysia Perlis (UniMAP)",
        "Universiti Malaysia Kelantan (UMK)",
        "Universiti Pertahanan Nasional Malaysia (UPNM)",
        "Universiti Sultan Zainal Abidin (UniSZA)"
    };
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Edit Profile | BarangBaek</title>

    <link rel="icon"
          type="image/png"
          href="${pageContext.request.contextPath}/assets/img/logo.png">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/navbar.css">

    <link rel="stylesheet"
          href="${pageContext.request.contextPath}/assets/css/editprofile.css?v=2">
</head>

<body class="has-navbar">

<%@ include file="/WEB-INF/jspf/user-navbar.jspf" %>

<main class="edit-profile-page">

    <div class="edit-profile-heading">
        <div>
            <a class="edit-profile-back"
               href="${pageContext.request.contextPath}/profile?action=view">
                <i class="fa-solid fa-arrow-left"></i>
                Back to My Profile
            </a>
            <span class="edit-profile-kicker">Profile settings</span>
            <h1>Edit Profile</h1>
            <p>Update the information used for your BarangBaek account and deliveries.</p>
        </div>
    </div>

    <% if (request.getAttribute("error") != null) { %>
    <div class="edit-profile-alert">
        <i class="fa-solid fa-circle-exclamation"></i>
        <span><%= h(String.valueOf(request.getAttribute("error"))) %></span>
    </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/profile?action=update"
          method="post"
          enctype="multipart/form-data"
          class="edit-profile-layout"
          id="profileForm">

        <aside class="edit-profile-sidebar">
            <section class="edit-photo-card">
                <span class="edit-profile-kicker">Profile photo</span>

                <div class="edit-photo-preview">
                    <img id="profilePhotoPreview"
                         src="${pageContext.request.contextPath}/assets/img/userphoto/<%= h(profilePhoto) %>"
                         alt="Profile photo preview"
                         onerror="this.src='${pageContext.request.contextPath}/assets/img/userphoto/default-user.png'">
                </div>

                <label class="edit-photo-button" for="userPhoto">
                    <i class="fa-solid fa-camera"></i>
                    Choose New Photo
                </label>

                <input type="file"
                       id="userPhoto"
                       name="userPhoto"
                       accept="image/jpeg,image/png,image/webp">

                <p>JPG, PNG or WEBP. Maximum file size: 5 MB.</p>
            </section>

            <section class="fixed-account-card">
                <span class="edit-profile-kicker">Fixed information</span>
                <h2>Account details</h2>

                <div class="fixed-account-row">
                    <span>User ID</span>
                    <strong>#<%= profileUser.getUserID() %></strong>
                </div>
                <div class="fixed-account-row">
                    <span>Email</span>
                    <strong><%= h(profileUser.getEmail()) %></strong>
                </div>
                <div class="fixed-account-row">
                    <span>Birthday</span>
                    <strong><%= h(profileUser.getBirthday()) %></strong>
                </div>
                <div class="fixed-account-row">
                    <span>Gender</span>
                    <strong><%= h(profileUser.getGender()) %></strong>
                </div>

                <div class="fixed-account-note">
                    <i class="fa-solid fa-lock"></i>
                    These details cannot be edited from this page.
                </div>
            </section>
        </aside>

        <section class="edit-profile-form-card">
            <div class="edit-form-section">
                <div class="edit-form-section__heading">
                    <span class="edit-form-section__icon">
                        <i class="fa-solid fa-user"></i>
                    </span>
                    <div>
                        <h2>Basic information</h2>
                        <p>Choose the name and username shown in BarangBaek.</p>
                    </div>
                </div>

                <div class="edit-field-row">
                    <div class="edit-field">
                        <label for="username">Username</label>
                        <input type="text"
                               id="username"
                               name="username"
                               value="<%= h(profileUser.getUsername()) %>"
                               minlength="3"
                               maxlength="30"
                               pattern="[A-Za-z0-9._]{3,30}"
                               autocomplete="username"
                               required>
                        <small>Letters, numbers, dots and underscores only.</small>
                    </div>

                    <div class="edit-field">
                        <label for="fullname">Full name</label>
                        <input type="text"
                               id="fullname"
                               name="fullname"
                               value="<%= h(profileUser.getFullName()) %>"
                               maxlength="100"
                               autocomplete="name"
                               required>
                    </div>
                </div>

                <div class="edit-field">
                    <label for="university">University</label>
                    <select id="university"
                            name="university"
                            required>
                        <option value="">Select university</option>
                        <% for (String university : universities) { %>
                        <option value="<%= h(university) %>"
                                <%= selected(profileUser.getUniversity(), university) %>>
                            <%= h(university) %>
                        </option>
                        <% } %>
                    </select>
                </div>
            </div>

            <div class="edit-form-section">
                <div class="edit-form-section__heading">
                    <span class="edit-form-section__icon">
                        <i class="fa-solid fa-location-dot"></i>
                    </span>
                    <div>
                        <h2>Delivery address</h2>
                        <p>This address is used when you select courier delivery.</p>
                    </div>
                </div>

                <div class="edit-field">
                    <label for="address1">Address line 1</label>
                    <input type="text"
                           id="address1"
                           name="address1"
                           value="<%= h(profileUser.getAddress1()) %>"
                           maxlength="150"
                           autocomplete="address-line1"
                           required>
                </div>

                <div class="edit-field">
                    <label for="address2">Address line 2 <span>(optional)</span></label>
                    <input type="text"
                           id="address2"
                           name="address2"
                           value="<%= h(profileUser.getAddress2()) %>"
                           maxlength="150"
                           autocomplete="address-line2">
                </div>

                <div class="edit-field-row">
                    <div class="edit-field">
                        <label for="city">City</label>
                        <select id="city"
                                name="city"
                                data-current-city="<%= h(profileUser.getCity()) %>"
                                required>
                            <option value="">Select city</option>
                        </select>
                    </div>

                    <div class="edit-field">
                        <label for="state">State</label>
                        <input type="text"
                               id="state"
                               name="state"
                               value="<%= h(profileUser.getState()) %>"
                               readonly
                               required>
                    </div>
                </div>

                <div class="edit-field-row">
                    <div class="edit-field">
                        <label for="postcode">Postcode</label>
                        <input type="text"
                               id="postcode"
                               name="postcode"
                               value="<%= h(profileUser.getPostcode()) %>"
                               maxlength="5"
                               pattern="[0-9]{5}"
                               inputmode="numeric"
                               autocomplete="postal-code"
                               required>
                    </div>

                    <div class="edit-field">
                        <label for="phone">Phone number</label>
                        <input type="tel"
                               id="phone"
                               name="phone"
                               value="<%= h(profileUser.getPhone()) %>"
                               pattern="[0-9+\- ]{9,15}"
                               maxlength="15"
                               autocomplete="tel"
                               required>
                    </div>
                </div>
            </div>

            <div class="edit-profile-actions">
                <a class="edit-cancel-button"
                   href="${pageContext.request.contextPath}/profile?action=view">
                    Cancel
                </a>

                <button type="submit"
                        class="edit-save-button"
                        id="profileSaveButton">
                    <i class="fa-solid fa-floppy-disk"></i>
                    Save Changes
                </button>
            </div>
        </section>
    </form>
</main>

<script src="${pageContext.request.contextPath}/assets/js/main.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/profile.js?v=2"></script>

</body>
</html>