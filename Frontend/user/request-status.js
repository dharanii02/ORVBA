
"use strict";

const API_BASE_URL = "http://localhost:8080";

const params = new URLSearchParams(window.location.search);
let requestId = params.get("requestId");

const token =
    localStorage.getItem("orvbaToken") ||
    sessionStorage.getItem("orvbaToken");

const $ = id => document.getElementById(id);

$("currentYear").textContent = new Date().getFullYear();

$("requestIdLabel").textContent =
    requestId || "Finding latest request...";

let currentRequest = null;
let refreshTimer = null;
let selectedRating = 0;
let requestActionInProgress = false;

const statusDescriptions = {
    PENDING: [
        "Waiting for a mechanic",
        "Your request is recorded and waiting for mechanic assignment."
    ],
    MECHANIC_ASSIGNED: [
        "Mechanic assigned",
        "A mechanic has been assigned. Waiting for acceptance."
    ],
    ACCEPTED: [
        "Request accepted",
        "Your mechanic has accepted the request."
    ],
    ON_THE_WAY: [
        "Mechanic is on the way",
        "Your mechanic is travelling to your location."
    ],
    ARRIVED: [
        "Mechanic has arrived",
        "Your mechanic has arrived and can inspect the vehicle."
    ],
    ADMIN_REVIEW: [
        "Quotation under review",
        "The quotation exceeds the configured limit and needs admin review."
    ],
    QUOTATION_PENDING: [
        "Quotation awaiting your decision",
        "Review the quotation and choose whether to approve or reject it."
    ],
    IN_PROGRESS: [
        "Repair in progress",
        "Your quotation was approved and the repair is in progress."
    ],
    COMPLETED: [
        "Repair completed",
        "The repair is complete. Check your final bill and payment status."
    ],
    CANCELLED: [
        "Request cancelled",
        "The request was cancelled."
    ]
};

const statusToTimelineIndex = {
    PENDING: 0,
    MECHANIC_ASSIGNED: 1,
    ACCEPTED: 2,
    ON_THE_WAY: 3,
    ARRIVED: 4,
    ADMIN_REVIEW: 5,
    QUOTATION_PENDING: 5,
    IN_PROGRESS: 5,
    COMPLETED: 6,
    CANCELLED: -1
};

const timelineSteps = [
    {
        label: "Request submitted",
        description: "Your assistance request has been created."
    },
    {
        label: "Mechanic assigned",
        description: "A mechanic has been matched to your location."
    },
    {
        label: "Mechanic accepted",
        description: "The mechanic accepted the service request."
    },
    {
        label: "Mechanic on the way",
        description: "Your mechanic is travelling to your location."
    },
    {
        label: "Mechanic arrived",
        description: "Your mechanic has arrived and can inspect the vehicle."
    },
    {
        label: "Quotation decision",
        description: "Review the diagnosis and repair amount before proceeding."
    },
    {
        label: "Repair completed",
        description: "The mechanic has marked the repair as complete."
    }
];

function money(value) {
    if (value === null || value === undefined || value === "") {
        return "—";
    }

    const number = Number(value);

    if (!Number.isFinite(number)) {
        return "—";
    }

    return number.toLocaleString("en-IN", {
        style: "currency",
        currency: "INR",
        maximumFractionDigits: 2
    });
}

function showMessage(element, message, type = "info") {
    if (!element) return;

    element.textContent = message;
    element.className = `message show ${type}`;
}

function hideMessage(element) {
    if (!element) return;

    element.textContent = "";
    element.className = "message";
}

function setBadge(status) {
    const badge = $("statusBadge");

    badge.textContent = (status || "UNKNOWN").replaceAll("_", " ");
    badge.className = "status-badge";

    if (status === "COMPLETED") {
        badge.classList.add("success");
    } else if (status === "CANCELLED") {
        badge.classList.add("cancelled");
    } else if (["PENDING", "ADMIN_REVIEW"].includes(status)) {
        badge.classList.add("pending");
    }
}

function renderTimeline(status) {
    const timeline = $("requestTimeline");
    const quotationActions = $("quotationActions");

    timeline.replaceChildren();

    const currentIndex = statusToTimelineIndex[status] ?? 0;
    const cancelled = status === "CANCELLED";

    timelineSteps.forEach((step, index) => {
        const completed = !cancelled && currentIndex > index;
        const current = !cancelled && currentIndex === index;

        const node = document.createElement("div");
        node.className = "timeline-step";

        if (completed) node.classList.add("done");

        if (current || (cancelled && index === 0)) {
            node.classList.add("current");
        }

        const marker = document.createElement("div");
        marker.className = "timeline-marker";

        const circle = document.createElement("span");
        circle.className = "marker-circle";

        if (cancelled && index === 0) {
            circle.textContent = "!";
        } else {
            circle.textContent = completed ? "✓" : String(index + 1);
        }

        marker.appendChild(circle);

        const content = document.createElement("div");
        content.className = "step-content";

        const heading = document.createElement("h3");
        heading.textContent = step.label;

        const description = document.createElement("p");
        description.textContent = step.description;

        if (status === "PENDING" && index === 1) {
            description.textContent =
                "No mechanic has been assigned yet. Your request is pending.";
        }

        if (status === "ADMIN_REVIEW" && index === 5) {
            description.textContent =
                "Admin review is required before you can decide on the quotation.";
        }

        if (status === "CANCELLED" && index === 0) {
            description.textContent = "This request has been cancelled.";
        }

        content.append(heading, description);

        if (index === 5 && quotationActions) {
            if (status === "QUOTATION_PENDING") {
                quotationActions.hidden = false;
                content.appendChild(quotationActions);
            } else {
                quotationActions.hidden = true;
            }
        }

        node.append(marker, content);
        timeline.appendChild(node);
    });
}

function renderMechanic(mechanic) {
    if (!mechanic) {
        $("mechanicName").textContent = "Awaiting assignment";
        $("mechanicDescription").textContent =
            "ORVBA will display mechanic details once one is assigned.";
        $("mechanicPhone").textContent = "—";
        $("mechanicSpecialization").textContent = "—";
        $("mechanicAvailability").textContent = "Not assigned";
        return;
    }

    $("mechanicName").textContent =
        mechanic.name || "Assigned mechanic";

    $("mechanicDescription").textContent =
        "Your assigned roadside assistance partner.";

    $("mechanicPhone").textContent =
        mechanic.phone || "Not provided";

    $("mechanicSpecialization").textContent =
        mechanic.specialization || "General assistance";

    $("mechanicAvailability").textContent =
        mechanic.available === true
            ? "Available"
            : mechanic.available === false
                ? "Busy / unavailable"
                : "—";
}

function renderQuotation(request) {
    const visibleStatuses = [
        "ADMIN_REVIEW",
        "QUOTATION_PENDING",
        "IN_PROGRESS",
        "COMPLETED",
        "CANCELLED"
    ];

    $("quotationSection").hidden =
        !visibleStatuses.includes(request.status);

    $("adminReviewNotice").hidden =
        request.status !== "ADMIN_REVIEW";

    $("diagnosisText").textContent =
        request.mechanicDiagnosis || "Not available";

    $("diagnosisAmount").textContent = money(request.diagnosisFee);
    $("repairAmount").textContent = money(request.repairQuotation);
    $("approvedAmount").textContent = money(request.approvedRepairAmount);
    $("finalAmount").textContent = money(request.finalAmount);

    $("quotationActions").hidden =
        request.status !== "QUOTATION_PENDING";

    hideMessage($("quotationMessage"));

    if (request.status === "ADMIN_REVIEW") {
        showMessage(
            $("quotationMessage"),
            "Your quotation is waiting for admin review.",
            "info"
        );
    } else if (request.status === "CANCELLED") {
        showMessage(
            $("quotationMessage"),
            "The request is cancelled. Any payable diagnosis fee is shown in the final amount.",
            "info"
        );
    } else if (request.status === "IN_PROGRESS") {
        showMessage(
            $("quotationMessage"),
            "You approved the quotation. The repair is in progress.",
            "success"
        );
    } else if (request.status === "QUOTATION_PENDING") {
        showMessage(
            $("quotationMessage"),
            "Please review the quotation and choose Approve or Reject.",
            "info"
        );
    }
}

function renderPayment(request) {
    const hasFinalAmount =
        request.finalAmount !== null &&
        request.finalAmount !== undefined;

    const eligibleStatus = [
        "COMPLETED",
        "CANCELLED"
    ].includes(request.status);

    const paymentStatus = request.paymentStatus || "NOT_REQUIRED";

    $("paymentSection").hidden =
        !(eligibleStatus || hasFinalAmount);

    $("paymentStatusText").textContent =
        paymentStatus.replaceAll("_", " ");

    const canPay =
        paymentStatus === "PENDING" &&
        hasFinalAmount &&
        Number(request.finalAmount) > 0 &&
        eligibleStatus;

    $("payNowButton").hidden = !canPay;

    $("summaryDiagnosisFee").textContent = money(request.diagnosisFee);
    $("summaryRepairAmount").textContent = money(request.repairQuotation);
    $("summaryApprovedAmount").textContent =
        money(request.approvedRepairAmount);
    $("summaryAmount").textContent = money(request.finalAmount);

    $("summaryNote").textContent =
        paymentStatus === "PAID"
            ? "Payment has been marked as paid."
            : canPay
                ? "Payment is pending for the final amount shown above."
                : hasFinalAmount
                    ? "This is the final amount currently recorded for the request."
                    : "The final bill will appear when the amount is confirmed.";

    hideMessage($("paymentMessage"));

    if (paymentStatus === "PAID") {
        showMessage(
            $("paymentMessage"),
            "Payment has already been recorded as paid.",
            "success"
        );
    } else if (canPay) {
        showMessage(
            $("paymentMessage"),
            "Payment is pending. Use the button below to confirm simulated payment.",
            "info"
        );
    } else {
        showMessage(
            $("paymentMessage"),
            "No payment is currently marked as due.",
            "info"
        );
    }
}

function renderRequest(request) {
    currentRequest = request;

    const status = request.status || "UNKNOWN";

    const details = statusDescriptions[status] || [
        "Request status",
        "The server returned a status this page does not recognize."
    ];

    $("currentStatusTitle").textContent = details[0];
    $("currentStatusDescription").textContent = details[1];
    $("updatedText").textContent =
        "Request details loaded from ORVBA.";

    $("requestIdLabel").textContent =
        request.requestId ?? requestId ?? "Unknown";

    setBadge(status);
    renderTimeline(status);
    renderMechanic(request.mechanic);
    renderQuotation(request);
    renderPayment(request);

    // Update the bill link only if this element exists in the HTML.
    const billLink = $("viewBillLink");

    if (billLink) {
        billLink.href =
            `bill.html?requestId=${encodeURIComponent(request.requestId ?? requestId)}`;
    }

    $("feedbackSection").hidden = status !== "COMPLETED";

    if (["COMPLETED", "CANCELLED"].includes(status) && refreshTimer) {
        clearInterval(refreshTimer);
        refreshTimer = null;
    }
}

async function readResponse(response) {
    const text = await response.text();

    if (!text) return {};

    try {
        return JSON.parse(text);
    } catch {
        return { message: text };
    }
}

function getErrorMessage(data, fallback) {
    if (typeof data === "string") return data;

    return data.message || data.error || fallback;
}

function extractRequests(data) {
    if (Array.isArray(data)) {
        return data;
    }

    if (data && Array.isArray(data.requests)) {
        return data.requests;
    }

    if (data && Array.isArray(data.content)) {
        return data.content;
    }

    if (data && Array.isArray(data.data)) {
        return data.data;
    }

    return [];
}

async function resolveLatestRequestId() {
    // Keep a valid ID explicitly supplied in the URL.
    if (requestId && /^\d+$/.test(requestId)) {
        localStorage.setItem("orvbaLastRequestId", String(requestId));
        sessionStorage.setItem("orvbaLastRequestId", String(requestId));

        $("requestIdLabel").textContent = requestId;

        return true;
    }

    // No ID in the URL: ask the backend for this user's requests.
    const response = await fetch(
        `${API_BASE_URL}/requests/my-requests`,
        {
            headers: {
                Accept: "application/json",
                Authorization: `Bearer ${token}`
            }
        }
    );

    const data = await readResponse(response);

    if (!response.ok) {
        throw new Error(
            getErrorMessage(
                data,
                `Unable to find your requests (${response.status}).`
            )
        );
    }

    const requests = extractRequests(data);

    if (requests.length === 0) {
        requestId = null;

        $("requestIdLabel").textContent = "None found";

        showMessage(
            $("generalMessage"),
            "You have not created a service request yet.",
            "info"
        );

        $("currentStatusTitle").textContent =
            "No service requests found";

        $("currentStatusDescription").textContent =
            "Create a request to track its progress here.";

        return false;
    }

    // Assumes request IDs increase as requests are created.
    requests.sort(
        (a, b) =>
            Number(b.requestId || 0) - Number(a.requestId || 0)
    );

    requestId = String(requests[0].requestId || "");

    if (!/^\d+$/.test(requestId)) {
        throw new Error(
            "The server returned requests without a valid request ID."
        );
    }

    localStorage.setItem("orvbaLastRequestId", requestId);
    sessionStorage.setItem("orvbaLastRequestId", requestId);

    $("requestIdLabel").textContent = requestId;

    return true;
}

async function loadRequest(showLoadingMessage = false) {
    if (!token) {
        showMessage(
            $("generalMessage"),
            "Your login session is missing. Please log in again.",
            "error"
        );

        return;
    }

    if (showLoadingMessage) {
        $("updatedText").textContent =
            "Refreshing request details...";
    }

    $("refreshButton").disabled = true;

    try {
        const hasRequest = await resolveLatestRequestId();

        if (!hasRequest) return;

        const response = await fetch(
            `${API_BASE_URL}/requests/${encodeURIComponent(requestId)}`,
            {
                headers: {
                    Accept: "application/json",
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await readResponse(response);

        if (!response.ok) {
            showMessage(
                $("generalMessage"),
                getErrorMessage(
                    data,
                    `Unable to load request (${response.status}).`
                ),
                "error"
            );

            $("updatedText").textContent =
                "Could not refresh request details.";

            return;
        }

        renderRequest(data);
        hideMessage($("generalMessage"));

    } catch (error) {
        console.error("Request loading failed:", error);

        showMessage(
            $("generalMessage"),
            error.message ||
                "Cannot connect to ORVBA. Check that the backend is running and CORS is configured.",
            "error"
        );

    } finally {
        $("refreshButton").disabled = false;
    }
}

async function sendRequestAction(url, button, successMessage) {
    if (!currentRequest || !token) {
        showMessage(
            $("generalMessage"),
            "Request details or login token are missing.",
            "error"
        );

        return;
    }

    if (requestActionInProgress) return;

    requestActionInProgress = true;
    button.disabled = true;

    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            method: "PUT",
            headers: {
                Accept: "application/json",
                Authorization: `Bearer ${token}`
            }
        });

        const data = await readResponse(response);

        if (!response.ok) {
            showMessage(
                $("generalMessage"),
                getErrorMessage(data, `Action failed (${response.status}).`),
                "error"
            );

            return;
        }

        showMessage(
            $("generalMessage"),
            successMessage,
            "success"
        );

        await loadRequest();

    } catch (error) {
        console.error("Request action failed:", error);

        showMessage(
            $("generalMessage"),
            "Could not connect to the server. Please try again.",
            "error"
        );

    } finally {
        requestActionInProgress = false;
        button.disabled = false;
    }
}

$("approveQuotation").addEventListener("click", function () {
    sendRequestAction(
        `/requests/${encodeURIComponent(requestId)}/quotation/approve`,
        this,
        "Quotation approved."
    );
});

$("rejectQuotation").addEventListener("click", function () {
    if (!confirm(
        "Reject this quotation? The request will be cancelled, and the diagnosis fee may remain payable."
    )) {
        return;
    }

    sendRequestAction(
        `/requests/${encodeURIComponent(requestId)}/quotation/reject`,
        this,
        "Quotation rejected."
    );
});

$("payNowButton").addEventListener("click", async function () {
    if (!currentRequest || !token) return;

    if (!confirm(
        `Confirm simulated payment of ${money(currentRequest.finalAmount)}? No real money will be charged.`
    )) {
        return;
    }

    this.disabled = true;

    try {
        const response = await fetch(
            `${API_BASE_URL}/payments/${encodeURIComponent(requestId)}`,
            {
                method: "POST",
                headers: {
                    Accept: "application/json",
                    Authorization: `Bearer ${token}`
                }
            }
        );

        const data = await readResponse(response);

        if (!response.ok) {
            showMessage(
                $("paymentMessage"),
                getErrorMessage(data, `Payment failed (${response.status}).`),
                "error"
            );

            return;
        }

        showMessage(
            $("paymentMessage"),
            "Simulated payment recorded successfully.",
            "success"
        );

        await loadRequest();

    } catch (error) {
        console.error("Payment request failed:", error);

        showMessage(
            $("paymentMessage"),
            "Could not connect to the payment endpoint.",
            "error"
        );

    } finally {
        this.disabled = false;
    }
});

$("refreshButton").addEventListener("click", () => {
    loadRequest(true);
});

// Star rating interaction.
const starButtons = document.querySelectorAll(".star-button");
const ratingText = $("ratingText");

const ratingLabels = {
    1: "Poor — we'll work to improve.",
    2: "Fair — thanks for your feedback.",
    3: "Good — we're glad to hear it.",
    4: "Very good — thank you!",
    5: "Excellent — we're happy you had a great experience!"
};

starButtons.forEach(button => {
    button.addEventListener("click", function () {
        selectedRating = Number(this.dataset.rating);

        starButtons.forEach(star => {
            const value = Number(star.dataset.rating);

            star.classList.toggle(
                "active",
                value <= selectedRating
            );

            star.setAttribute(
                "aria-pressed",
                String(value === selectedRating)
            );
        });

        if (ratingText) {
            ratingText.textContent = ratingLabels[selectedRating];
        }
    });
});

$("feedbackForm").addEventListener("submit", function (event) {
    event.preventDefault();

    const message = $("feedbackMessage");
    hideMessage(message);

    if (!currentRequest || currentRequest.status !== "COMPLETED") {
        showMessage(
            message,
            "Feedback is available after the repair is completed.",
            "error"
        );

        return;
    }

    if (!selectedRating) {
        showMessage(
            message,
            "Please select a star rating.",
            "error"
        );

        return;
    }

    const category = $("feedbackCategory").value;
    const comments = $("feedbackComments").value.trim();

    if (!category || !comments) {
        showMessage(
            message,
            "Please select a category and enter your comments.",
            "error"
        );

        return;
    }

    showMessage(
        message,
        "Your feedback is validated, but it has not been saved because the feedback API is not connected yet.",
        "info"
    );
});

// Load the latest request on opening the page.
loadRequest();

// Refresh active requests every 15 seconds.
refreshTimer = setInterval(() => {
    if (
        currentRequest &&
        !["COMPLETED", "CANCELLED"].includes(currentRequest.status)
    ) {
        loadRequest();
    }
}, 15000);
