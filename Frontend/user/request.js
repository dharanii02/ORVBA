
const API_BASE_URL = "http://localhost:8080";

const requestForm = document.getElementById("requestForm");
const requestMessage = document.getElementById("requestMessage");
const locationStatus = document.getElementById("locationStatus");
const getLocationButton = document.getElementById("getLocationButton");
const submitRequest = document.getElementById("submitRequest");
const submitText = document.getElementById("submitText");

const vehicleSelect = document.getElementById("vehicleId");
const serviceSelect = document.getElementById("serviceId");

let currentLatitude = null;
let currentLongitude = null;

// Display the current year
document.getElementById("currentYear").textContent =
    new Date().getFullYear();

// Get the token from either storage location
function getToken() {
    return localStorage.getItem("orvbaToken")
        || sessionStorage.getItem("orvbaToken");
}

// Display messages
function showRequestMessage(message, type) {
    requestMessage.textContent = message;
    requestMessage.className = `form-message show ${type}`;
}

function clearRequestMessage() {
    requestMessage.textContent = "";
    requestMessage.className = "form-message";
}

// Load vehicles belonging to the logged-in user
async function loadVehicles() {
    const token = getToken();

    if (!token) {
        vehicleSelect.innerHTML =
            '<option value="">Please log in again</option>';
        return;
    }

    try {
        const response = await fetch(
            `${API_BASE_URL}/vehicles/my-vehicles`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Accept": "application/json"
                }
            }
        );

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                throw new Error(
                    "Your session is invalid or expired. Please log in again."
                );
            }

            throw new Error(
                `Could not load vehicles. Status: ${response.status}`
            );
        }

        const vehicles = await response.json();

        vehicleSelect.innerHTML =
            '<option value="">-- Select your vehicle --</option>';

        if (!Array.isArray(vehicles) || vehicles.length === 0) {
            vehicleSelect.innerHTML =
                '<option value="">No registered vehicles found</option>';
            return;
        }

        vehicles.forEach(vehicle => {
            const option = document.createElement("option");

            option.value = vehicle.vehicleId;

            const registrationNumber =
                vehicle.vehicleNumber || vehicle.registrationNumber || "";

            option.textContent =
                `${registrationNumber} - ${vehicle.brand || ""} ${vehicle.model || ""}`.trim();

            vehicleSelect.appendChild(option);
        });

    } catch (error) {
        console.error("Vehicle loading error:", error);

        vehicleSelect.innerHTML =
            '<option value="">Unable to load vehicles</option>';

        showRequestMessage(error.message, "error");
    }
}

// Load available services
async function loadServices() {
    const token = getToken();

    if (!token) {
        serviceSelect.innerHTML =
            '<option value="">Please log in again</option>';
        return;
    }

    try {
        const response = await fetch(
            `${API_BASE_URL}/services`,
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Accept": "application/json"
                }
            }
        );

        if (!response.ok) {
            if (response.status === 401 || response.status === 403) {
                throw new Error(
                    "Your session is invalid or expired. Please log in again."
                );
            }

            throw new Error(
                `Could not load services. Status: ${response.status}`
            );
        }

        const services = await response.json();

        serviceSelect.innerHTML =
            '<option value="">-- Select a service --</option>';

        if (!Array.isArray(services) || services.length === 0) {
            serviceSelect.innerHTML =
                '<option value="">No services available</option>';
            return;
        }

        services.forEach(service => {
            const option = document.createElement("option");

            option.value = service.serviceId;

            const price = service.standardPrice == null
                ? "Price unavailable"
                : `₹${service.standardPrice}`;

            option.textContent = `${service.serviceName} - ${price}`;

            serviceSelect.appendChild(option);
        });

    } catch (error) {
        console.error("Service loading error:", error);

        serviceSelect.innerHTML =
            '<option value="">Unable to load services</option>';

        showRequestMessage(error.message, "error");
    }
}

// Capture the user's current location
getLocationButton.addEventListener("click", function () {
    clearRequestMessage();

    if (!navigator.geolocation) {
        locationStatus.textContent =
            "Geolocation is not supported by this browser.";
        return;
    }

    locationStatus.textContent = "Getting your location...";
    getLocationButton.disabled = true;

    navigator.geolocation.getCurrentPosition(
        function (position) {
            currentLatitude = position.coords.latitude;
            currentLongitude = position.coords.longitude;

            document.getElementById("latitude").value =
                currentLatitude.toFixed(6);

            document.getElementById("longitude").value =
                currentLongitude.toFixed(6);

            locationStatus.textContent =
                "Location captured successfully.";

            getLocationButton.disabled = false;
        },
        function (error) {
            const messages = {
                1: "Location permission denied. Enter coordinates manually.",
                2: "Location unavailable. Try again or enter coordinates manually.",
                3: "Location request timed out. Please try again."
            };

            locationStatus.textContent =
                messages[error.code] ||
                "Unable to get location. Enter coordinates manually.";

            getLocationButton.disabled = false;
        },
        {
            enableHighAccuracy: true,
            timeout: 15000,
            maximumAge: 60000
        }
    );
});

// Keep manually entered coordinates synchronized
document.getElementById("latitude").addEventListener("input", function () {
    currentLatitude = this.value === "" ? null : Number(this.value);
});

document.getElementById("longitude").addEventListener("input", function () {
    currentLongitude = this.value === "" ? null : Number(this.value);
});

// Submit the assistance request
requestForm.addEventListener("submit", async function (event) {
    event.preventDefault();
    clearRequestMessage();

    const token = getToken();

    if (!token) {
        showRequestMessage(
            "Your login session was not found. Please log in again.",
            "error"
        );
        return;
    }

    const vehicleId = Number(vehicleSelect.value);
    const serviceId = Number(serviceSelect.value);

    const latitude = Number(
        document.getElementById("latitude").value
    );

    const longitude = Number(
        document.getElementById("longitude").value
    );

    const problemDescription = document
        .getElementById("problemDescription")
        .value.trim();

    if (!vehicleSelect.value || !Number.isInteger(vehicleId) || vehicleId <= 0) {
        showRequestMessage("Please select a valid vehicle.", "error");
        return;
    }

    if (!serviceSelect.value || !Number.isInteger(serviceId) || serviceId <= 0) {
        showRequestMessage("Please select a service.", "error");
        return;
    }

    if (!problemDescription || problemDescription.length < 5) {
        showRequestMessage(
            "Please describe the breakdown in at least 5 characters.",
            "error"
        );
        return;
    }

    if (
        !Number.isFinite(latitude) ||
        latitude < -90 ||
        latitude > 90 ||
        !Number.isFinite(longitude) ||
        longitude < -180 ||
        longitude > 180
    ) {
        showRequestMessage(
            "Enter valid latitude and longitude, or use the location button.",
            "error"
        );
        return;
    }

    // The backend identifies the user from the authenticated token.
    const payload = {
        vehicle: {
            vehicleId: vehicleId
        },
        service: {
            serviceId: serviceId
        },
        latitude: latitude,
        longitude: longitude,
        problemDescription: problemDescription
    };

    submitRequest.disabled = true;
    submitText.textContent = "Finding assistance...";

    try {
        const response = await fetch(`${API_BASE_URL}/requests`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Accept": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        const responseText = await response.text();
        let data = {};

        if (responseText) {
            try {
                data = JSON.parse(responseText);
            } catch {
                data = { message: responseText };
            }
        }

        if (!response.ok) {
            const errorMessage =
                typeof data === "string"
                    ? data
                    : data.message ||
                      data.error ||
                      `Could not create the request. Status: ${response.status}`;

            showRequestMessage(errorMessage, "error");
            return;
        }

        if (!data || !data.requestId) {
            showRequestMessage(
                "The server responded, but no request ID was returned. Check the API response.",
                "error"
            );
            return;
        }

        sessionStorage.setItem(
            "orvbaLastRequestId",
            String(data.requestId)
        );

        showRequestMessage(
            data.mechanic
                ? "Request created and a mechanic has been assigned."
                : "Request created. No mechanic is assigned yet; the request is pending.",
            "success"
        );

        window.setTimeout(function () {
            window.location.href =
                `request-status.html?requestId=${encodeURIComponent(data.requestId)}`;
        }, 1000);

    } catch (error) {
        console.error("Create request failed:", error);

        showRequestMessage(
            "Unable to connect to ORVBA. Check that Spring Boot is running and CORS is configured correctly.",
            "error"
        );
    } finally {
        submitRequest.disabled = false;
        submitText.textContent = "Find nearby assistance";
    }
});


// Initialize dropdowns
loadVehicles();
loadServices();
