/**
 * app.js
 * Application entry point.
 * Handles initialisation, connection polling, keyboard shortcuts,
 * theme persistence and API function name aliasing.
 */

// ─── API Aliases ──────────────────────────────────────────────────────────
// Alias the raw API functions so portfolio.js / stocks.js can call them
// without naming conflicts with their own local functions.
window.createPortfolio_API = createPortfolio;
window.addStock_API        = addStock;

// ─── Connection Status ────────────────────────────────────────────────────

async function updateConnectionStatus() {
    const dot  = document.getElementById('statusDot');
    const text = document.getElementById('statusText');
    if (!dot || !text) return;

    const isOnline = await checkHealth();
    dot.className    = 'status-dot ' + (isOnline ? 'connected' : 'disconnected');
    text.textContent = isOnline ? 'Connected' : 'Server offline';
}

// ─── Keyboard Shortcuts ───────────────────────────────────────────────────

document.addEventListener('keydown', (e) => {
    // Escape: close modal or drawer
    if (e.key === 'Escape') {
        closeModal();
        closeDrawer();
    }

    // Enter on portfolio name → create portfolio
    if (e.key === 'Enter' && document.activeElement.id === 'portfolioName') {
        createPortfolio();
    }

    // Enter on any stock form field → add stock
    const stockInputs = ['stockSymbol', 'stockQty', 'stockPrice', 'stockDate'];
    if (e.key === 'Enter' && stockInputs.includes(document.activeElement.id)) {
        addStock();
    }
});

// ─── Symbol input auto-uppercase ─────────────────────────────────────────

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

// ─── Swipe-to-close drawer (mobile UX) ───────────────────────────────────

(function initSwipeClose() {
    let startX = 0;

    document.addEventListener('touchstart', (e) => {
        startX = e.touches[0].clientX;
    }, { passive: true });

    document.addEventListener('touchend', (e) => {
        const deltaX = e.changedTouches[0].clientX - startX;
        const sidebar = document.getElementById('sidebar');
        // Swipe left ≥ 60px while drawer is open → close
        if (deltaX < -60 && sidebar && sidebar.classList.contains('open')) {
            closeDrawer();
        }
    }, { passive: true });
})();

// ─── Init ─────────────────────────────────────────────────────────────────

async function init() {
    // Apply saved or system theme before anything renders
    initTheme();

    // Check backend connectivity
    await updateConnectionStatus();

    // Load portfolio list
    await loadPortfolios();

    // Poll every 30 s
    setInterval(updateConnectionStatus, 30_000);
}

init();
