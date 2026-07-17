<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    if (session.getAttribute("passwordResetUserID") == null) {
        response.sendRedirect(request.getContextPath() + "/auth?action=forgotPasswordForm");
        return;
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Reset Password | BarangBaek</title>

        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/resetpassword.css">
</head>

<body>
        <div class="reset-box">

            <h1>Create New Password</h1><br><br>

            <% if (request.getAttribute("error") != null) {%>
            <div class="error-message">  
                <i class="fa-solid fa-circle-exclamation"></i>
                <%= request.getAttribute("error")%>
            </div>
            <% }%>
 
            <div id="clientError" class="client-error" role="alert"></div>
            
            <form id="resetForm" action="${pageContext.request.contextPath}/auth?action=resetPassword" method="post">
                <div class="form-group">
                    <label for="password">NEW PASSWORD</label>
                    <div class="password-box">
                        <input type="password" id="password" name="password" placeholder="Enter your password" minlength="8" maxlength="100" autocomplete="new-password" required>
                        <button type="button" id="togglePassword" aria-label="Show password" title="Show password">
                            <i class="fa-solid fa-eye"></i>
                        </button>    
                    </div>
                </div>

                <div class="form-group">
                    <label for="confirmPassword">CONFIRM NEW PASSWORD</label>
                    <div class="password-box">    
                        <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-enter your password" minlength="8" maxlength="100" autocomplete="new-password" required>
                        <button type="button" id="toggleConfirmPassword" aria-label="Show confirm password" title="Show confirm password">
                            <i class="fa-solid fa-eye"></i>
                        </button>
                    </div>
                </div>
                
                <div id="passwordMatchMessage" class="note"> </div>

                <div class="password-rules">
                    <p>Password requirements:</p>

                    <ul>
                        <li id="lengthRule" class="invalid">
                            At least 8 characters
                        </li>
                        <li id="uppercaseRule" class="invalid">
                            At least one uppercase letter
                        </li>
                        <li id="numberRule" class="invalid">
                            At least one number
                        </li>
                        <li id="symbolRule" class="invalid">
                            At least one symbol
                        </li>
                    </ul>
                </div>
                
                <button id="submitButton" type="submit" class="reset-btn">Reset Password</button>
            </form>
        </div>
        
<script>
    document.addEventListener("DOMContentLoaded", function () {

        const form = document.getElementById("resetForm");
        const passwordInput = document.getElementById("password");
        const confirmPasswordInput = document.getElementById("confirmPassword");
        const togglePassword = document.getElementById("togglePassword");
        const toggleConfirmPassword = document.getElementById("toggleConfirmPassword");
        const submitButton = document.getElementById("submitButton");
        const clientError = document.getElementById("clientError");
        const passwordMatchMessage = document.getElementById("passwordMatchMessage");

        // Toggle new password visibility
        togglePassword.addEventListener("click", function () {
            togglePasswordVisibility(passwordInput, togglePassword);
        });

        // Toggle confirm password visibility
        toggleConfirmPassword.addEventListener("click", function () {
            togglePasswordVisibility(
                    confirmPasswordInput,
                    toggleConfirmPassword
            );
        });

        // Live password validation
        passwordInput.addEventListener("input", function () {
            updatePasswordRules();
            updatePasswordMatch();
        });

        confirmPasswordInput.addEventListener(
                "input",
                updatePasswordMatch
        );

        // Form validation
        form.addEventListener("submit", function (event) {

            clientError.style.display = "none";
            clientError.textContent = "";

            const errors = [];
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            const hasMinimumLength = password.length >= 8;
            const hasUppercase = /[A-Z]/.test(password);
            const hasNumber = /\d/.test(password);
            const hasSymbol =
                    /[!@#$%^&*()_\-+=<>?]/.test(password);

            if (!hasMinimumLength || !hasUppercase || !hasNumber || !hasSymbol) {
                errors.push( "The password does not meet all requirements." );
            }

            if (password !== confirmPassword) {
                errors.push( "The password and confirmation password do not match." );
            }

            if (errors.length > 0) {
                event.preventDefault();
                clientError.innerHTML = errors.map(
                    function (error) {
                        return "• " + escapeHtml(error);
                    }
                ).join("<br>");

                clientError.style.display = "block";
                clientError.scrollIntoView({
                    behavior: "smooth",
                    block: "center"
                });
                return;
            }

            // Prevent multiple submissions
            submitButton.disabled = true;
            submitButton.innerHTML ='<i class="fa-solid fa-spinner fa-spin"></i> '+ "Resetting password...";
        });

        // Show or hide password
        function togglePasswordVisibility(input, button) {
            const icon = button.querySelector("i");

            if (input.type === "password") {
                input.type = "text";
                icon.classList.remove("fa-eye");
                icon.classList.add("fa-eye-slash");
                button.setAttribute("aria-label","Hide password");
                button.setAttribute("title","Hide password");
            } else {
                input.type = "password";
                icon.classList.remove("fa-eye-slash");
                icon.classList.add("fa-eye");
                button.setAttribute("aria-label","Show password");
                button.setAttribute("title","Show password");
            }
        }

        // Check password requirements
        function updatePasswordRules() {
            const password = passwordInput.value;
            updateRule("lengthRule",password.length >= 8);
            updateRule("uppercaseRule",/[A-Z]/.test(password));
            updateRule("numberRule",/\d/.test(password));
            updateRule("symbolRule",/[!@#$%^&*()_\-+=<>?]/.test(password));
        }

        // Change requirement status
        function updateRule(ruleId, isValid) {
            const rule = document.getElementById(ruleId);

            if (!rule) {
                return;
            }

            rule.classList.toggle("valid",isValid);
            rule.classList.toggle("invalid",!isValid);
        }

        // Check whether passwords match
        function updatePasswordMatch() {
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;

            if (confirmPassword === "") {
                passwordMatchMessage.textContent = "";
                passwordMatchMessage.style.color = "";
                return;
            }

            if (password === confirmPassword) {
                passwordMatchMessage.textContent = "Passwords match.";
                passwordMatchMessage.style.color = "#198754";
            } else {
                passwordMatchMessage.textContent = "Passwords do not match.";
                passwordMatchMessage.style.color = "#dc3545";
            }
        }

        // Prevent HTML injection in error messages
        function escapeHtml(value) {
            const element = document.createElement("div");
            element.textContent = value;
            return element.innerHTML;
        }
    });
</script>
</body>
</html>