
const API_BASE_URL = "http://localhost:8080";

const loginForm = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const togglePasswordButton = document.getElementById("togglePassword");
const rememberMeCheckbox = document.getElementById("rememberMe");
const loginMessage = document.getElementById("loginMessage");

// Show success or error messages
function showMessage(message, type = "error") {
    loginMessage.textContent = message;
    loginMessage.style.display = "block";
    loginMessage.style.padding = "11px 12px";
    loginMessage.style.borderRadius = "9px";
    loginMessage.style.fontSize = "12px";

    if (type === "success") {
        loginMessage.style.color = "#166534";
        loginMessage.style.backgroundColor = "#dcfce7";
        loginMessage.style.border = "1px solid #bbf7d0";
    } else {
        loginMessage.style.color = "#b91c1c";
        loginMessage.style.backgroundColor = "#fee2e2";
        loginMessage.style.border = "1px solid #fecaca";
    }
}

// Show or hide the password
togglePasswordButton.addEventListener("click", () => {
    const isPasswordHidden = passwordInput.type === "password";

    passwordInput.type = isPasswordHidden ? "text" : "password";
    togglePasswordButton.textContent = isPasswordHidden ? "HIDE" : "SHOW";
    togglePasswordButton.setAttribute(
        "aria-label",
        isPasswordHidden ? "Hide password" : "Show password"
    );
});

// Save authentication data
function saveAuthentication(data) {
    const accessToken = data.accessToken;
    const refreshToken = data.refreshToken;
    const role = String(data.role || "").toUpperCase().replace(/^ROLE_/, "");

    // Store only recognized roles
    if (!["USER", "MECHANIC", "ADMIN"].includes(role)) {
        throw new Error("Your account role is missing or unsupported.");
    }

    // Clear old login data first
    localStorage.removeItem("orvbaToken");
    localStorage.removeItem("orvbaRefreshToken");
    localStorage.removeItem("orvbaRole");

    sessionStorage.removeItem("orvbaToken");
    sessionStorage.removeItem("orvbaRefreshToken");
    sessionStorage.removeItem("orvbaRole");

    const storage = rememberMeCheckbox.checked
        ? localStorage
        : sessionStorage;

    storage.setItem("orvbaToken", accessToken);
    storage.setItem("orvbaRole", role);

    if (refreshToken) {
        storage.setItem("orvbaRefreshToken", refreshToken);
    }

    return role;
}

// Redirect according to the backend-provided role
function redirectByRole(role) {
    const dashboards = {
        USER: "user/dashboard.html",
        MECHANIC: "mechanic/dashboard.html",
        ADMIN: "admin/dashboard.html"
    };

    window.location.href = dashboards[role];
}

// Submit login form
loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value;

    if (!username || !password) {
        showMessage("Please enter your username and password.");
        return;
    }

    const submitButton = loginForm.querySelector(
        'button[type="submit"]'
    );

    const originalButtonText = submitButton.querySelector("span");

    submitButton.disabled = true;
    originalButtonText.textContent = "Signing in...";
    loginMessage.textContent = "";
    loginMessage.style.display = "none";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        // Read the backend response
        const responseText = await response.text();
        let data = {};

        if (responseText) {
            try {
                data = JSON.parse(responseText);
            } catch {
                throw new Error(
                    "The server returned an unexpected response."
                );
            }
        }

        if (!response.ok) {
            const message =
                typeof data.message === "string"
                    ? data.message
                    : typeof data.error === "string"
                        ? data.error
                        : "Login failed. Check your credentials.";

            throw new Error(message);
        }

        if (!data.accessToken || !data.role) {
            throw new Error(
                "Login succeeded, but the server response is missing the access token or role."
            );
        }

        const role = saveAuthentication(data);

        showMessage("Login successful! Redirecting...", "success");

        // Brief pause so the success message can be seen
        setTimeout(() => {
            redirectByRole(role);
        }, 500);

    } catch (error) {
        console.error("Login error:", error);

        if (error instanceof TypeError) {
            showMessage(
                "Cannot connect to the server. Make sure Spring Boot is running and CORS is configured."
            );
        } else {
            showMessage(error.message || "Something went wrong. Please try again.");
        }
    } finally {
        submitButton.disabled = false;
        originalButtonText.textContent = "Login to your account";
    }
});

function saveAuthentication(data) {
    const accessToken = data.accessToken;
    const refreshToken = data.refreshToken;
    const role = String(data.role || "")
        .toUpperCase()
        .replace(/^ROLE_/, "");

    // Validate the account role
    if (!["USER", "MECHANIC", "ADMIN"].includes(role)) {
        throw new Error("Your account role is missing or unsupported.");
    }

    // Clear old login data
    const keys = [
        "orvbaToken",
        "orvbaRefreshToken",
        "orvbaRole",
        "orvbaUsername"
    ];

    keys.forEach(key => {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    });

    // Respect the Remember Me checkbox
    const storage = rememberMeCheckbox.checked
        ? localStorage
        : sessionStorage;

    // Store authentication details
    storage.setItem("orvbaToken", accessToken);
    storage.setItem("orvbaRole", role);
    storage.setItem("orvbaUsername", usernameInput.value.trim());

    if (refreshToken) {
        storage.setItem("orvbaRefreshToken", refreshToken);
    }

    return role;
}
