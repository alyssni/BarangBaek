<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

    <head>
        <meta charset="UTF-8">

        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <title>Register | BarangBaek</title>

        <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/logo.png">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/register.css">
    </head>

    <body>
        <div class="box">
            <div class="title">
                <a class="auth-brand" href="${pageContext.request.contextPath}/public?action=dashboard" aria-label="Go to BarangBaek public dashboard">
                    <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="BarangBaek logo">
                </a>

                <h1>Create Account</h1>
                <p>Join the BarangBaek community</p>
            </div>

            <!-- Message for error -->
            <% if (request.getAttribute("error") != null) {%>
            <div class="error-message">
                <i class="fa-solid fa-circle-exclamation"></i>
                <%= request.getAttribute("error")%>
            </div>
            <% } %>

            <!-- Message for success -->
            <% if (request.getAttribute("success") != null) {%>
            <div class="success-message">
                <i class="fa-solid fa-circle-check"></i>
                <%= request.getAttribute("success")%>
            </div>
            <% }%>

            <!-- -->
            <div id="clientError" class="client-error" role="alert"> </div>

            <form id="registerForm" action="${pageContext.request.contextPath}/auth?action=register" method="post" autocomplete="on">
                <div class="row">
                    <div class="form-group">
                        <label for="username"> Username <span class="required-symbol">*</span> </label>
                        <input type="text" id="username" name="username" placeholder="Choose a unique username" minlength="3" maxlength="30" pattern="[A-Za-z0-9._]+" autocomplete="username" required>
                        <div class="note"> Use 3–30 letters, numbers, dots or underscores. </div>
                    </div>

                    <div class="form-group">
                        <label for="fullname"> Full Name <span class="required-symbol">*</span> </label>
                        <input type="text" id="fullname" name="fullname" placeholder="Enter your full name" maxlength="100" autocomplete="name" required>
                    </div>
                </div>

                <div class="row">
                    <div class="form-group">
                        <label for="gender"> Gender <span class="required-symbol">*</span> </label>
                        <select id="gender" name="gender" required>
                            <option value=""> Select gender </option>
                            <option value="Male"> Male </option>
                            <option value="Female"> Female </option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="birthday"> Date of Birth <span class="required-symbol">*</span> </label>
                        <input type="date" id="birthday" name="birthday" autocomplete="bday" required>
                        <div class="note note-blue"> You must be 18 years old or above. </div>
                    </div>
                </div>

                <div class="form-group">
                    <label for="university"> University <span class="required-symbol">*</span> </label>
                    <select id="university" name="university" required>
                        <option value=""> Select university </option>

                        <option value="Universiti Malaya (UM)">
                            Universiti Malaya (UM)
                        </option>

                        <option value="Universiti Sains Malaysia (USM)">
                            Universiti Sains Malaysia (USM)
                        </option>

                        <option value="Universiti Kebangsaan Malaysia (UKM)">
                            Universiti Kebangsaan Malaysia (UKM)
                        </option>

                        <option value="Universiti Putra Malaysia (UPM)">
                            Universiti Putra Malaysia (UPM)
                        </option>

                        <option value="Universiti Teknologi MARA (UiTM)">
                            Universiti Teknologi MARA (UiTM)
                        </option>

                        <option value="Universiti Teknologi Malaysia (UTM)">
                            Universiti Teknologi Malaysia (UTM)
                        </option>

                        <option value="Universiti Utara Malaysia (UUM)">
                            Universiti Utara Malaysia (UUM)
                        </option>

                        <option value="Universiti Islam Antarabangsa Malaysia (UIAM)">
                            Universiti Islam Antarabangsa Malaysia (UIAM)
                        </option>

                        <option value="Universiti Malaysia Sarawak (UNIMAS)">
                            Universiti Malaysia Sarawak (UNIMAS)
                        </option>

                        <option value="Universiti Malaysia Sabah (UMS)">
                            Universiti Malaysia Sabah (UMS)
                        </option>

                        <option value="Universiti Pendidikan Sultan Idris (UPSI)">
                            Universiti Pendidikan Sultan Idris (UPSI)
                        </option>

                        <option value="Universiti Sains Islam Malaysia (USIM)">
                            Universiti Sains Islam Malaysia (USIM)
                        </option>

                        <option value="Universiti Malaysia Terengganu (UMT)">
                            Universiti Malaysia Terengganu (UMT)
                        </option>

                        <option value="Universiti Tun Hussein Onn Malaysia (UTHM)">
                            Universiti Tun Hussein Onn Malaysia (UTHM)
                        </option>

                        <option value="Universiti Teknikal Malaysia Melaka (UTeM)">
                            Universiti Teknikal Malaysia Melaka (UTeM)
                        </option>

                        <option value="Universiti Malaysia Pahang Al-Sultan Abdullah (UMPSA)">
                            Universiti Malaysia Pahang Al-Sultan Abdullah (UMPSA)
                        </option>

                        <option value="Universiti Malaysia Perlis (UniMAP)">
                            Universiti Malaysia Perlis (UniMAP)
                        </option>

                        <option value="Universiti Malaysia Kelantan (UMK)">
                            Universiti Malaysia Kelantan (UMK)
                        </option>

                        <option value="Universiti Pertahanan Nasional Malaysia (UPNM)">
                            Universiti Pertahanan Nasional Malaysia (UPNM)
                        </option>

                        <option value="Universiti Sultan Zainal Abidin (UniSZA)">
                            Universiti Sultan Zainal Abidin (UniSZA)
                        </option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="email"> Education Email <span class="required-symbol">*</span> </label>
                    <input type="email" id="email" name="email" placeholder="student@university.edu.my" maxlength="120" autocomplete="email" required>
                    <div class="note note-blue"> The email address must end with .edu.my. </div>
                </div>

                <div class="form-group">
                    <label for="address1"> Address Line 1 <span class="required-symbol">*</span> </label>
                    <input type="text" id="address1" name="address1" placeholder="House number and street name" maxlength="150" autocomplete="address-line1" required>
                </div>

                <div class="form-group">
                    <label for="address2"> Address Line 2 </label>
                    <input type="text" id="address2" name="address2" placeholder="Apartment, unit or building name" maxlength="150" autocomplete="address-line2">
                </div>

                <div class="row">
                    <div class="form-group">
                        <label for="city"> City <span class="required-symbol">*</span> </label>
                        <select id="city" name="city" autocomplete="address-level2" required>
                            <option value="">Select city first</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="state">State<span class="required-symbol">*</span></label>
                        <input type="text" id="state" name="state" placeholder="Automatically selected" autocomplete="address-level1" readonly required>
                    </div>
                </div>

                <div class="form-group">
                    <label for="postcode">Postcode<span class="required-symbol">*</span></label>
                    <input type="text" id="postcode" name="postcode" placeholder="Example: 81310" pattern="[0-9]{5}" maxlength="5" inputmode="numeric" autocomplete="postal-code" required>
                    <div class="note"> Enter a 5-digit Malaysian postcode. </div>
                </div>

                <div class="form-group">
                    <label for="phone">Phone Number<span class="required-symbol">*</span></label>
                    <input type="tel" id="phone" name="phone" placeholder="Example: 0123456789" maxlength="15" pattern="[0-9]{9,11}" inputmode="tel" autocomplete="tel" required>
                    <div class="note"> Only numbers is accepted. </div>
                </div>

                <div class="row">
                    <div class="form-group">
                        <label for="password">Password<span class="required-symbol">*</span></label>
                        <div class="password-box">
                            <input type="password" id="password" name="password" placeholder="Enter your password" minlength="8" maxlength="100" autocomplete="new-password" required>
                            <button type="button" id="togglePassword" aria-label="Show password" title="Show password">
                                <i class="fa-solid fa-eye"></i>
                            </button>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password<span class="required-symbol">*</span></label>
                        <div class="password-box">
                            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Re-enter your password" minlength="8" maxlength="100" autocomplete="new-password" required>
                            <button type="button" id="toggleConfirmPassword" aria-label="Show confirm password" title="Show confirm password">
                                <i class="fa-solid fa-eye"></i>
                            </button>
                        </div>
                        <div id="passwordMatchMessage" class="note"> </div>
                    </div>
                </div>

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
                <button id="submitButton" class="submit" type="submit"> Register Now </button>

                <div class="login-link">
                    <span class="already-text">Already have an account?</span>
                    <a href="${pageContext.request.contextPath}/auth?action=login">Login now</a>
                </div>

                <div class="login-link"> 
                    <a href="${pageContext.request.contextPath}/public?action=dashboard">View as Guest</a>
                </div>        
            </form>
        </div>

        <script>
            document.addEventListener("DOMContentLoaded", function () {

                const form = document.getElementById("registerForm");
                const birthdayInput = document.getElementById("birthday");
                const emailInput = document.getElementById("email");
                const passwordInput = document.getElementById("password");
                const confirmPasswordInput = document.getElementById("confirmPassword");
                const submitButton = document.getElementById("submitButton");
                const clientError = document.getElementById("clientError");
                const passwordMatchMessage = document.getElementById("passwordMatchMessage");
                const cityInput = document.getElementById("city");
                const stateInput = document.getElementById("state");

                // City & state for mapping
                const cityStateMap = {

                    // Johor
                    "Johor Bahru": "Johor",
                    "Batu Pahat": "Johor",
                    "Muar": "Johor",
                    "Kluang": "Johor",
                    "Kulai": "Johor",
                    "Segamat": "Johor",
                    "Pontian": "Johor",
                    "Kota Tinggi": "Johor",
                    "Mersing": "Johor",

                    // Kedah
                    "Alor Setar": "Kedah",
                    "Sungai Petani": "Kedah",
                    "Kulim": "Kedah",
                    "Langkawi": "Kedah",
                    "Jitra": "Kedah",
                    "Baling": "Kedah",

                    // Kelantan
                    "Kota Bharu": "Kelantan",
                    "Pasir Mas": "Kelantan",
                    "Tanah Merah": "Kelantan",
                    "Machang": "Kelantan",
                    "Kuala Krai": "Kelantan",
                    "Gua Musang": "Kelantan",

                    // Melaka
                    "Melaka City": "Melaka",
                    "Alor Gajah": "Melaka",
                    "Jasin": "Melaka",

                    // Negeri Sembilan
                    "Seremban": "Negeri Sembilan",
                    "Port Dickson": "Negeri Sembilan",
                    "Nilai": "Negeri Sembilan",
                    "Kuala Pilah": "Negeri Sembilan",
                    "Tampin": "Negeri Sembilan",

                    // Pahang
                    "Kuantan": "Pahang",
                    "Temerloh": "Pahang",
                    "Bentong": "Pahang",
                    "Raub": "Pahang",
                    "Pekan": "Pahang",
                    "Jerantut": "Pahang",
                    "Cameron Highlands": "Pahang",

                    // Pulau Pinang
                    "George Town": "Pulau Pinang",
                    "Butterworth": "Pulau Pinang",
                    "Bukit Mertajam": "Pulau Pinang",
                    "Bayan Lepas": "Pulau Pinang",
                    "Nibong Tebal": "Pulau Pinang",

                    // Perak
                    "Ipoh": "Perak",
                    "Taiping": "Perak",
                    "Teluk Intan": "Perak",
                    "Sitiawan": "Perak",
                    "Kuala Kangsar": "Perak",
                    "Batu Gajah": "Perak",
                    "Kampar": "Perak",
                    "Lumut": "Perak",

                    // Perlis
                    "Kangar": "Perlis",
                    "Arau": "Perlis",
                    "Padang Besar": "Perlis",
                    "Kuala Perlis": "Perlis",

                    // Sabah
                    "Kota Kinabalu": "Sabah",
                    "Sandakan": "Sabah",
                    "Tawau": "Sabah",
                    "Lahad Datu": "Sabah",
                    "Keningau": "Sabah",
                    "Semporna": "Sabah",
                    "Kudat": "Sabah",
                    "Beaufort": "Sabah",

                    // Sarawak
                    "Kuching": "Sarawak",
                    "Miri": "Sarawak",
                    "Sibu": "Sarawak",
                    "Bintulu": "Sarawak",
                    "Sri Aman": "Sarawak",
                    "Sarikei": "Sarawak",
                    "Limbang": "Sarawak",
                    "Kapit": "Sarawak",

                    // Selangor
                    "Shah Alam": "Selangor",
                    "Petaling Jaya": "Selangor",
                    "Subang Jaya": "Selangor",
                    "Klang": "Selangor",
                    "Kajang": "Selangor",
                    "Sepang": "Selangor",
                    "Cyberjaya": "Selangor",
                    "Puchong": "Selangor",
                    "Rawang": "Selangor",
                    "Kuala Selangor": "Selangor",
                    "Ampang": "Selangor",

                    // Terengganu
                    "Kuala Terengganu": "Terengganu",
                    "Kemaman": "Terengganu",
                    "Dungun": "Terengganu",
                    "Marang": "Terengganu",
                    "Besut": "Terengganu",
                    "Setiu": "Terengganu",

                    // Federal Territories
                    "Kuala Lumpur": "Wilayah Persekutuan Kuala Lumpur",
                    "Labuan": "Wilayah Persekutuan Labuan",
                    "Putrajaya": "Wilayah Persekutuan Putrajaya"
                };

                // Populate city drop down
                Object.keys(cityStateMap).sort().forEach(function (city) {
                    const option = document.createElement("option");

                    option.value = city;
                    option.textContent = city;
                    cityInput.appendChild(option);
                });

                // Auto select respective state once city is picked
                cityInput.addEventListener("change", function () {
                    const selectedCity = cityInput.value;
                    stateInput.value = cityStateMap[selectedCity] || "";
                });

                // Age limit ensure 18 and above
                const today = new Date();
                const maximumBirthday = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());
                birthdayInput.max = formatDate(maximumBirthday);

                // Password toggle
                setupPasswordToggle("togglePassword", "password");
                setupPasswordToggle("toggleConfirmPassword", "confirmPassword");

                // Password validation
                passwordInput.addEventListener("input", updatePasswordRules);
                passwordInput.addEventListener("input", updatePasswordMatch);
                confirmPasswordInput.addEventListener("input", updatePasswordMatch);

                // Form validation
                form.addEventListener("submit", function (event) {
                    clientError.style.display = "none";
                    clientError.textContent = "";

                    const errors = [];
                    const email = emailInput.value.trim().toLowerCase();
                    const password = passwordInput.value;
                    const confirmPassword = confirmPasswordInput.value;
                    const birthday = birthdayInput.value;
                    const selectedCity = cityInput.value;
                    const expectedState = cityStateMap[selectedCity] || "";

                    stateInput.value = expectedState;

                    // Birthday validation
                    if (!birthday) {
                        errors.push("Please select your date of birth.");
                    } else if (!isAtLeast18(birthday)) {
                        errors.push("You must be at least 18 years old.");
                    }

                    // City and state validation
                    if (!selectedCity || !expectedState) {
                        errors.push("Please select a valid city.");
                    }

                    // Education email validation
                    const educationEmailPattern = /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.edu\.my$/i;

                    if (!educationEmailPattern.test(email)) {
                        errors.push("Please enter a valid education email ending with .edu.my.");
                    }

                    /* Password validation:
                     * At least 8 characters
                     * At least one uppercase letter
                     * At least one number
                     * At least one symbol
                     * Must match password confirmation
                     */
                    const hasUppercase = /[A-Z]/.test(password);
                    const hasNumber = /\d/.test(password);
                    const hasSymbol = /[!@#$%^&*()_\-+=<>?]/.test(password);
                    const hasMinimumLength = password.length >= 8;

                    if (!hasMinimumLength || !hasUppercase || !hasNumber || !hasSymbol) {
                        errors.push("The password does not meet all requirements.");
                    }

                    if (password !== confirmPassword) {
                        errors.push("The password and confirmation password do not match.");
                    }

                    if (errors.length > 0) {
                        event.preventDefault();
                        clientError.innerHTML = errors.map(function (error) {
                            return "• " + escapeHtml(error);
                        }).join("<br>");

                        clientError.style.display = "block";
                        clientError.scrollIntoView({
                            behavior: "smooth",
                            block: "center"
                        });
                        return;
                    }

                    // Valid form continues to authservlet.
                    submitButton.disabled = true;
                    submitButton.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i> ' + "Creating account...";
                });


                // Password toggle
                function setupPasswordToggle(buttonId, inputId) {
                    const button = document.getElementById(buttonId);
                    const input = document.getElementById(inputId);

                    if (!button || !input) {
                        return;
                    }

                    const icon = button.querySelector("i");

                    button.addEventListener("click", function () {
                        const passwordIsHidden = input.type === "password";

                        input.type = passwordIsHidden ? "text" : "password";
                        icon.classList.toggle("fa-eye", !passwordIsHidden);
                        icon.classList.toggle("fa-eye-slash", passwordIsHidden);

                        const buttonText = passwordIsHidden ? "Hide password" : "Show password";
                        button.setAttribute("aria-label", buttonText);
                        button.setAttribute("title", buttonText
                                );
                    });
                }

                function updatePasswordRules() {
                    const password = passwordInput.value;
                    updateRule("lengthRule", password.length >= 8);
                    updateRule("uppercaseRule", /[A-Z]/.test(password));
                    updateRule("numberRule", /\d/.test(password));
                    updateRule("symbolRule", /[!@#$%^&*()_\-+=<>?]/.test(password));
                }

                function updateRule(ruleId, isValid) {
                    const rule = document.getElementById(ruleId);

                    if (!rule) {
                        return;
                    }

                    rule.classList.toggle("valid", isValid);
                    rule.classList.toggle("invalid", !isValid);
                }

                function updatePasswordMatch() {
                    const password = passwordInput.value;
                    const confirmPassword = confirmPasswordInput.value;

                    if (!confirmPassword) {
                        passwordMatchMessage.textContent = "";
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

                function isAtLeast18(dateValue) {
                    const birthDate = new Date(dateValue + "T00:00:00");
                    const currentDate = new Date();
                    let age = currentDate.getFullYear() - birthDate.getFullYear();
                    const monthDifference = currentDate.getMonth() - birthDate.getMonth();

                    if (monthDifference < 0 || (monthDifference === 0 && currentDate.getDate() < birthDate.getDate())) {
                        age--;
                    }
                    return age >= 18;
                }

                function formatDate(date) {
                    const year = date.getFullYear();
                    const month = String(date.getMonth() + 1).padStart(2, "0");
                    const day = String(date.getDate()).padStart(2, "0");
                    return year + "-" + month + "-" + day;
                }

                function escapeHtml(value) {
                    const element = document.createElement("div");
                    element.textContent = value;
                    return element.innerHTML;
                }
            });
        </script>
    </body>
</html>