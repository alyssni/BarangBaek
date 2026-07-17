<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Login | BarangBaek</title>

        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/login.css">
    </head>

    <body>
        <div class="login-box">
            <div class="container">

                <div class="title">
                    <!-- Logo redirect to guest dashboard -->
                    <a class="auth-brand" href="${pageContext.request.contextPath}/public?action=dashboard" aria-label="Go to BarangBaek public dashboard">
                        <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="BarangBaek logo">
                    </a>
                    
                    <!-- Tag line -->
                    <div class="tagline"> ✨ Where Campus Buys and Sells ✨ </div>
                </div>
                    
                <div class="welcome">
                    <div class="emoji">🎓</div>
                    <h2>Welcome Back</h2>
                    <p>Login to your account</p>
                </div>

                <!-- Error Message -->
                <% if (request.getAttribute("error") != null) {%>
                <div class="error-message"> <%= request.getAttribute("error")%> </div>
                <% } %>

                <!-- Success Message -->
                <% if (request.getAttribute("success") != null) {%>
                <div class="success-message"> <%= request.getAttribute("success")%> </div>
                <% }%>

                <!-- Login Form: submits to AuthServlet -->
                <form id="loginForm" action="${pageContext.request.contextPath}/auth?action=login" method="post" autocomplete="on">

                    <!-- Username -->
                    <div class="input-group">
                        <label for="username"> USERNAME </label>
                        <input type="text" id="username" name="username" placeholder="Enter your username" minlength="3" maxlength="30" autocomplete="username" required>
                    </div>

                    <!-- Password -->        
                    <div class="input-group">
                        <label for="password">PASSWORD</label>
                        <div class="password-box">
                            <input type="password" id="password" name="password" placeholder="Enter your password" autocomplete="current-password" required>
                            <button type="button" id="togglePassword" aria-label="Show password" title="Show password">
                                <i class="fa-solid fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Remember password -->
                    <div class="options">
                        <label class="remember-option">
                            <input type="checkbox" id="remember" name="remember" value="true"> Remember me
                        </label>

                        <a class="forgot-btn" href="${pageContext.request.contextPath}/auth?action=forgotPasswordForm"> Forgot password?</a>
                    </div>

                    <!-- Login Button -->
                    <button type="submit" class="login-btn">
                        Login
                    </button>

                    <!-- Register Link -->
                    <div class="register">
                        No account yet?
                        <a href="${pageContext.request.contextPath}/auth?action=registerForm">Create account</a>
                    </div>

                    <!-- View as guest -->
                    <div class="register"> 
                        <a href="${pageContext.request.contextPath}/public?action=dashboard">View as Guest</a>
                    </div>
                </form>
            </div>
        </div>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                const toggleButton = document.getElementById("togglePassword");
                const passwordInput = document.getElementById("password");

                if (!toggleButton || !passwordInput) {
                    return;
                }

                const icon = toggleButton.querySelector("i");

                toggleButton.addEventListener("click", function () {
                    const passwordIsHidden = passwordInput.type === "password";
                    passwordInput.type = passwordIsHidden ? "text" : "password";

                    if (icon) {
                        icon.classList.toggle("fa-eye", !passwordIsHidden);
                        icon.classList.toggle("fa-eye-slash", passwordIsHidden);
                    }

                    const buttonText = passwordIsHidden ? "Hide password": "Show password";
                    toggleButton.setAttribute("aria-label", buttonText);
                    toggleButton.setAttribute( "title", buttonText );
                });
            });
        </script>
    </body>
</html>