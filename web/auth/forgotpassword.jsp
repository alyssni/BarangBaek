<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Forgot Password | BarangBaek</title>
        
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/forgotpassword.css">
</head>

<body>
<div class="reset-box">

    <h1>Forgot Password</h1>

    <p class="description"> Enter the same information used when you registered your account. </p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error-message"> <%= request.getAttribute("error") %> </div>
    <% } %>

    <form action="${pageContext.request.contextPath}/auth?action=verifyReset" method="post">

        <div class="input-group">
            <label for="email">REGISTERED EMAIL</label>
            <input type="email" id="email" name="email" placeholder="Enter your registered email" required>
        </div>

        <div class="input-group">
            <label for="phone">REGISTERED PHONE NUMBER</label>
            <input type="text" id="phone" name="phone" placeholder="Example: 0123456789" required>
        </div>

        <div class="input-group">
            <label for="birthday">DATE OF BIRTH</label>
            <input type="date" id="birthday" name="birthday" required>
        </div>

        <button type="submit" class="reset-btn"> Verify Account </button>

        <a class="back-link" href="${pageContext.request.contextPath}/auth?action=login"> Back to login </a>
    </form>
</div>
</body>
</html>