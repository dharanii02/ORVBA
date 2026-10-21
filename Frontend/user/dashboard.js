
async function loadVehicleCount() {
    const token =
        localStorage.getItem("orvbaToken") ||
        sessionStorage.getItem("orvbaToken");

    const countElement = document.getElementById("vehicleCount");

    if (!token) {
        countElement.textContent = "Please log in again";
        return;
    }

    try {
        const response = await fetch(
            "http://localhost:8080/vehicles/my-vehicles",
            {
                method: "GET",
                headers: {
                    "Authorization": `Bearer ${token}`,
                    "Accept": "application/json"
                }
            }
        );

        if (!response.ok) {
            console.error(
                "Vehicle API returned status:",
                response.status
            );
            countElement.textContent = "Vehicle count unavailable";
            return;
        }

        const vehicles = await response.json();

        if (!Array.isArray(vehicles)) {
            throw new Error("Unexpected response from vehicle API");
        }

        countElement.textContent =
            `${vehicles.length} vehicle(s) registered`;

    } catch (error) {
        console.error("Error loading vehicle count:", error);
        countElement.textContent = "Vehicle count unavailable";
    }
}

function loadUserName() {
    const token =
        localStorage.getItem("orvbaToken") ||
        sessionStorage.getItem("orvbaToken");

    const role =
        localStorage.getItem("orvbaRole") ||
        sessionStorage.getItem("orvbaRole");

    if (!token || role !== "USER") {
        window.location.href = "../login.html";
        return;
    }

    const username =
        localStorage.getItem("orvbaUsername") ||
        sessionStorage.getItem("orvbaUsername");

    document.getElementById("userName").textContent =
        username || "there";
}

function logoutUser() {
    if (!confirm("Are you sure you want to logout?")) {
        return false;
    }

    const keys = [
        "orvbaToken",
        "orvbaRefreshToken",
        "orvbaRole",
        "orvbaUsername"
    ];

    keys.forEach(function (key) {
        localStorage.removeItem(key);
        sessionStorage.removeItem(key);
    });

    window.location.href = "../login.html";
    return false;
}

// Run when the dashboard opens
loadUserName();
loadVehicleCount();
