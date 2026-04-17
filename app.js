/**
 * app.js
 * Application entry point.
 * Handles initialisation, connection status polling and
 * wires up API function name aliases to avoid naming conflicts.
 */

// ─── API Aliases ──────────────────────────────────────────────────────────
// Some module functions have the same name as API functions.
// We alias the API functions here so modules can call them clearly.
window.createPortfolio_API = createPortfolio;
window.addStock_API = addStock;

// ─── Connection Status ────────────────────────────────────────────────────

/**
 * Check backend connectivity and update the status indicator.
 */
async function updateConnectionStatus() {
    const dot  = document.getElementById('statusDot');
    const text = document.getElementById('statusText');
    if (!dot || !text) return;

    const isOnline = await checkHealth();

    dot.className  = 'status-dot ' + (isOnline ? 'connected' : 'disconnected');
    text.textContent = isOnline ? 'Connected' : 'Server offline';
}

// ─── Keyboard Shortcuts ───────────────────────────────────────────────────

document.addEventListener('keydown', (e) => {
    // Escape closes the modal
    if (e.key === 'Escape') {
        closeModal();
    }

    // Enter on portfolio name input triggers create
    if (e.key === 'Enter' && document.activeElement.id === 'portfolioName') {
        createPortfolio();
    }

    // Enter on any stock form input triggers add
    const stockInputs = ['stockSymbol', 'stockQty', 'stockPrice', 'stockDate'];
    if (e.key === 'Enter' && stockInputs.includes(document.activeElement.id)) {
        addStock();
    }
});

// ─── Symbol Input Auto-uppercase ─────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    const symbolInput = document.getElementById('stockSymbol');
    if (symbolInput) {
        symbolInput.addEventListener('input', () => {
            const pos = symbolInput.selectionStart;
            symbolInput.value = symbolInput.value.toUpperCase();
            symbolInput.setSelectionRange(pos, pos);
        });
    }
});

// ─── Init ─────────────────────────────────────────────────────────────────

/**
 * Bootstrap the application on page load.
 */
async function init() {
    // Check connection immediately
    await updateConnectionStatus();

    // Load portfolio list in sidebar
    await loadPortfolios();

    // Poll connection status every 30 seconds
    setInterval(updateConnectionStatus, 30_000);
}

// Start the app
init();
