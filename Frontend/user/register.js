
const API_BASE_URL = "http://localhost:8080";

const registerForm = document.getElementById("registerForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const registerButton = document.getElementById("registerButton");
const registerMessage = document.getElementById("registerMessage");

function showMessage(message, type = "error") {
    registerMessage.textContent = message;
    registerMessage.className = `message ${type}`;
    registerMessage.hidden = false;
}

document.querySelectorAll(".toggle-password").forEach(button => {
    button.addEventListener("click", () => {
        const input = document.getElementById(button.dataset.target);
        const showPassword = input.type === "password";

        input.type = showPassword ? "text" : "password";
        button.textContent = showPassword ? "HIDE" : "SHOW";
        button.setAttribute(
            "aria-label",
            showPassword ? "Hide password" : "Show password"
        );
    });
});

registerForm.addEventListener("submit", async event => {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value;
    const confirmPassword = confirmPasswordInput.value;

    if (username.length < 3) {
        showMessage("Username must contain at least 3 characters.");
        usernameInput.focus();
        return;
    }

    if (password.length < 8) {
        showMessage("Password must contain at least 8 characters.");
        passwordInput.focus();
        return;
    }

    if (password !== confirmPassword) {
        showMessage("Passwords do not match. Please try again.");
        confirmPasswordInput.focus();
        return;
    }

    if (!document.getElementById("terms").checked) {
        showMessage("Please confirm that you agree to the terms.");
        return;
    }

    registerButton.disabled = true;
    registerButton.querySelector("span").textContent = "Creating account...";
    registerMessage.hidden = true;

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            // The backend should assign Role.USER itself.
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const responseText = await response.text();

        if (!response.ok) {
            throw new Error(
                responseText || `Registration failed (${response.status}).`
            );
        }

        showMessage(
            "Your account was created successfully! Redirecting to login...",
            "success"
        );

        registerForm.reset();

        setTimeout(() => {
            window.location.href = "../login.html";
        }, 1500);

    } catch (error) {
        console.error("Registration error:", error);

        if (error instanceof TypeError) {
            showMessage(
                "Cannot connect to the server. Make sure Spring Boot is running."
            );
        } else {
            showMessage(error.message || "Registration failed. Please try again.");
        }
    } finally {
        registerButton.disabled = false;
        registerButton.querySelector("span").textContent = "Create my account";
    }
});
