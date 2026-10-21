
const API_BASE_URL = "http://localhost:8080";

const mechanicForm = document.getElementById("mechanicForm");
const steps = [...document.querySelectorAll(".form-step")];
const indicators = [...document.querySelectorAll(".step-indicator")];

const stepHeading = document.getElementById("stepHeading");
const stepDescription = document.getElementById("stepDescription");
const progressFill = document.getElementById("progressFill");

const backButton = document.getElementById("backButton");
const nextButton = document.getElementById("nextButton");
const registerButton = document.getElementById("registerButton");
const registerMessage = document.getElementById("registerMessage");

const getLocationButton = document.getElementById("getLocationButton");
const locationButtonText = document.getElementById("locationButtonText");
const locationMessage = document.getElementById("locationMessage");
const latitudeInput = document.getElementById("latitude");
const longitudeInput = document.getElementById("longitude");

let currentStep = 0;
let locationRequestInProgress = false;

function showLocationMessage(message, type = "info") {
    locationMessage.textContent = message;
    locationMessage.className = `location-message ${type}`;
}

function getMyLocation() {
    if (!navigator.geolocation) {
        showLocationMessage(
            "GPS location is not supported by this browser. You can register without it and add your location later.",
            "error"
        );
        return;
    }

    if (locationRequestInProgress) return;

    locationRequestInProgress = true;
    getLocationButton.disabled = true;
    locationButtonText.textContent = "Getting location...";
    getLocationButton.classList.add("loading");
    showLocationMessage(
        "Please allow location access in your browser. Waiting for GPS...",
        "info"
    );

    navigator.geolocation.getCurrentPosition(
        position => {
            latitudeInput.value = String(position.coords.latitude);
            longitudeInput.value = String(position.coords.longitude);

            locationRequestInProgress = false;
            getLocationButton.disabled = false;
            getLocationButton.classList.remove("loading");
            getLocationButton.classList.add("location-success");
            locationButtonText.textContent = "Refresh My Location";

            showLocationMessage(
                "Location captured successfully. Your coordinates are ready for registration.",
                "success"
            );
        },
        error => {
            locationRequestInProgress = false;
            getLocationButton.disabled = false;
            getLocationButton.classList.remove("loading");
            locationButtonText.textContent = "Use My Location";

            let message;
            switch (error.code) {
                case error.PERMISSION_DENIED:
                    message = "Location permission was denied. Allow location access in your browser settings, then try again.";
                    break;
                case error.POSITION_UNAVAILABLE:
                    message = "Your current location is unavailable. Check your device's location services and try again.";
                    break;
                case error.TIMEOUT:
                    message = "Location request timed out. Please try again.";
                    break;
                default:
                    message = "Could not get your location. Please try again.";
            }

            showLocationMessage(
                `${message} Location is optional, so you can continue without it.`,
                "error"
            );
        },
        {
            enableHighAccuracy: true,
            timeout: 15000,
            maximumAge: 60000
        }
    );
}

getLocationButton.addEventListener("click", getMyLocation);

const stepInformation = [
    {
        heading: "Create your login",
        description: "First, set up your account credentials."
    },
    {
        heading: "Your professional profile",
        description: "Tell us about yourself and your experience."
    },
    {
        heading: "Location & registration",
        description: "Almost there! Review your details and register."
    }
];

function showMessage(message, type = "error") {
    registerMessage.textContent = message;
    registerMessage.className = `message ${type}`;
    registerMessage.hidden = false;
}

function clearMessage() {
    registerMessage.hidden = true;
    registerMessage.textContent = "";
}

function showStep(index) {
    currentStep = index;

    steps.forEach((step, stepIndex) => {
        const isActive = stepIndex === index;

        step.hidden = !isActive;
        step.classList.toggle("active", isActive);
    });

    indicators.forEach((indicator, indicatorIndex) => {
        indicator.classList.toggle("active", indicatorIndex === index);
        indicator.classList.toggle("completed", indicatorIndex < index);

        const number = indicator.querySelector(".step-number");

        number.textContent = indicatorIndex < index
            ? "✓"
            : String(indicatorIndex + 1);
    });

    stepHeading.textContent = stepInformation[index].heading;
    stepDescription.textContent = stepInformation[index].description;

    progressFill.style.width = `${(index / (steps.length - 1)) * 100}%`;

    backButton.hidden = index === 0;
    nextButton.hidden = index === steps.length - 1;
    registerButton.hidden = index !== steps.length - 1;

    clearMessage();

    // Bring the current form section into view on smaller screens.
    if (window.innerWidth <= 850) {
        document.querySelector(".form-panel").scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
}

function validateCurrentStep() {
    const currentFields = [
        ...steps[currentStep].querySelectorAll("input, select, textarea")
    ];

    for (const field of currentFields) {
        if (!field.checkValidity()) {
            field.reportValidity();
            field.focus();
            return false;
        }
    }

    if (currentStep === 0) {
        const password = document.getElementById("password").value;
        const confirmPassword =
            document.getElementById("confirmPassword").value;

        if (password.length < 8) {
            showMessage("Password must contain at least 8 characters.");
            document.getElementById("password").focus();
            return false;
        }

        if (password !== confirmPassword) {
            showMessage("Passwords do not match.");
            document.getElementById("confirmPassword").focus();
            return false;
        }
    }

    if (currentStep === 1) {
        const phone = document.getElementById("phone").value.trim();

        if (!/^[0-9]{10}$/.test(phone)) {
            showMessage("Enter a valid 10-digit phone number.");
            document.getElementById("phone").focus();
            return false;
        }
    }

    if (currentStep === 2) {
        const latitudeText =
            document.getElementById("latitude").value.trim();
        const longitudeText =
            document.getElementById("longitude").value.trim();

        if (Boolean(latitudeText) !== Boolean(longitudeText)) {
            showMessage(
                "Enter both latitude and longitude, or leave both blank."
            );
            document.getElementById(
                latitudeText ? "longitude" : "latitude"
            ).focus();
            return false;
        }

        if (latitudeText && longitudeText) {
            const latitude = Number(latitudeText);
            const longitude = Number(longitudeText);

            if (
                !Number.isFinite(latitude) ||
                latitude < -90 ||
                latitude > 90
            ) {
                showMessage("Latitude must be between -90 and 90.");
                document.getElementById("latitude").focus();
                return false;
            }

            if (
                !Number.isFinite(longitude) ||
                longitude < -180 ||
                longitude > 180
            ) {
                showMessage("Longitude must be between -180 and 180.");
                document.getElementById("longitude").focus();
                return false;
            }
        }
    }

    return true;
}

// Password visibility controls.
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

// Continue to the next step only after validating the current one.
nextButton.addEventListener("click", () => {
    if (!validateCurrentStep()) {
        return;
    }

    showStep(currentStep + 1);
});

// Return to the previous step without losing entered data.
backButton.addEventListener("click", () => {
    if (currentStep > 0) {
        showStep(currentStep - 1);
    }
});

// Submit the registration after the final step is validated.
mechanicForm.addEventListener("submit", async event => {
    event.preventDefault();

    if (currentStep !== steps.length - 1) {
        return;
    }

    if (!validateCurrentStep()) {
        return;
    }

    const latitudeText =
        document.getElementById("latitude").value.trim();
    const longitudeText =
        document.getElementById("longitude").value.trim();

    const requestData = {
        username: document.getElementById("username").value.trim(),
        password: document.getElementById("password").value,

        name: document.getElementById("name").value.trim(),
        phone: document.getElementById("phone").value.trim(),
        email: document.getElementById("email").value.trim(),
        address: document.getElementById("address").value.trim(),

        specialization: document.getElementById("specialization").value,
        experience: document.getElementById("experience").value,

        idProof: document.getElementById("idProof").value,
        idProofNumber:
            document.getElementById("idProofNumber").value.trim(),

        latitude: latitudeText ? Number(latitudeText) : null,
        longitude: longitudeText ? Number(longitudeText) : null
    };

    registerButton.disabled = true;
    registerButton.querySelector("span").textContent = "Registering...";
    backButton.disabled = true;
    clearMessage();

    try {
        const response = await fetch(`${API_BASE_URL}/mechanics/register`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        const responseText = await response.text();

        if (!response.ok) {
            let errorMessage = "Mechanic registration failed.";

            if (responseText) {
                try {
                    const errorData = JSON.parse(responseText);

                    errorMessage =
                        errorData.message ||
                        errorData.error ||
                        responseText;
                } catch {
                    errorMessage = responseText;
                }
            }

            throw new Error(errorMessage);
        }

        showMessage(
            "Registration successful! Your profile is awaiting admin approval. Redirecting to login...",
            "success"
        );

        mechanicForm.reset();
        locationButtonText.textContent = "Use My Location";
        getLocationButton.classList.remove("location-success");
        showLocationMessage(
            "Location is optional. Tap the button to capture your GPS coordinates.",
            "info"
        );

        setTimeout(() => {
            window.location.href = "../login.html";
        }, 2200);

    } catch (error) {
        console.error("Mechanic registration error:", error);

        if (error instanceof TypeError) {
            showMessage(
                "Cannot connect to the server. Check that Spring Boot is running and CORS is configured."
            );
        } else {
            showMessage(
                error.message || "Registration failed. Please try again."
            );
        }
    } finally {
        registerButton.disabled = false;
        registerButton.querySelector("span").textContent =
            "Register mechanic";
        backButton.disabled = false;
    }
});

// Initialize the first step.
showStep(0);
