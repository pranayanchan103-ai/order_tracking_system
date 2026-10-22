"use strict";

const form = document.getElementById("track-form");
const input = document.getElementById("order-id");
const button = document.getElementById("track-btn");
const messageBox = document.getElementById("message");
const resultBox = document.getElementById("result");
const sampleButtons = document.querySelectorAll(".sample-btn");

/* Small helper: create an element and set its text safely (textContent, never innerHTML) */
function el(tag, className, text) {
    const node = document.createElement(tag);
    if (className) {
        node.className = className;
    }
    if (text !== undefined && text !== null) {
        node.textContent = text;
    }
    return node;
}

function showMessage(text, type) {
    resultBox.classList.add("hidden");
    messageBox.textContent = text;
    messageBox.className = "message " + type;
}

function clearMessage() {
    messageBox.textContent = "";
    messageBox.className = "message hidden";
}

/* Calls the Java backend and shows either the result or a clear error message */
async function trackOrder(orderId) {
    clearMessage();
    resultBox.classList.add("hidden");
    button.disabled = true;
    button.textContent = "Searching...";

    try {
        const response = await fetch("/api/track?orderId=" + encodeURIComponent(orderId));

        let data = null;
        try {
            data = await response.json();
        } catch (parseError) {
            data = null;
        }

        if (!response.ok) {
            const text = data && data.error ? data.error : "Something went wrong. Please try again.";
            showMessage(text, response.status === 404 || response.status === 400 ? "info" : "error");
            return;
        }

        renderResult(data);
    } catch (networkError) {
        showMessage("Cannot reach the server. Make sure Main.java is running.", "error");
    } finally {
        button.disabled = false;
        button.textContent = "Track Order";
    }
}

function renderResult(data) {
    document.getElementById("res-order-id").textContent = data.orderId;
    document.getElementById("res-product").textContent = data.productName;
    document.getElementById("res-description").textContent = data.currentStatusDescription;
    document.getElementById("res-customer").textContent = data.customerName;
    document.getElementById("res-placed").textContent = data.placedAt;

    const badge = document.getElementById("res-badge");
    badge.textContent = data.currentStatusLabel;
    badge.className = "badge status-" + data.currentStatus.toLowerCase().replace(/_/g, "-");

    const timeline = data.timeline || [];
    const last = timeline.length > 0 ? timeline[timeline.length - 1] : null;
    document.getElementById("res-updated").textContent =
        last ? last.eventTime + " \u2022 " + last.location : "-";

    renderProgress(data);
    renderTimeline(timeline);

    resultBox.classList.remove("hidden");
}

/* Horizontal 5-step progress bar */
function renderProgress(data) {
    const progress = document.getElementById("progress");
    progress.innerHTML = "";

    data.steps.forEach(function (label, index) {
        const stepNumber = index + 1;
        let state = "";

        if (stepNumber < data.currentStep || data.currentStep === data.totalSteps) {
            state = "done";
        } else if (stepNumber === data.currentStep) {
            state = "current";
        }

        const step = el("div", "step " + state);
        step.appendChild(el("div", "circle", state === "done" ? "\u2713" : String(stepNumber)));
        step.appendChild(el("div", "step-label", label));
        progress.appendChild(step);
    });
}

/* Vertical timeline, oldest event first (the server already sorted it) */
function renderTimeline(events) {
    const list = document.getElementById("timeline");
    list.innerHTML = "";

    events.forEach(function (event, index) {
        const item = el("li", index === events.length - 1 ? "latest" : "");
        item.appendChild(el("span", "dot"));

        const body = el("div", "event");
        body.appendChild(el("div", "event-title", event.statusLabel));
        body.appendChild(el("div", "event-time", event.eventTime));
        body.appendChild(el("div", "event-location", event.location));
        if (event.remarks) {
            body.appendChild(el("div", "event-remarks", event.remarks));
        }

        item.appendChild(body);
        list.appendChild(item);
    });
}

/* ---------- events ---------- */
form.addEventListener("submit", function (e) {
    e.preventDefault();
    const orderId = input.value.trim();

    if (!orderId) {
        showMessage("Please enter an order ID.", "info");
        input.focus();
        return;
    }
    trackOrder(orderId);
});

sampleButtons.forEach(function (btn) {
    btn.addEventListener("click", function () {
        input.value = btn.dataset.id;
        trackOrder(btn.dataset.id);
    });
});
