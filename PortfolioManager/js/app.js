/**
 * app.js — Entry point: aliases, connection status, keyboard shortcuts, init.
 */

// ─── API Aliases ───────────────────────────────────────────────
// Expose the API functions under window so they can be called by
// portfolio.js / stocks.js without naming collisions.
window.createPortfolioAPI = createPortfolioAPI;
window.addStockAPI        = addStockAPI;

// ─── Connection Status ─────────────────────────────────────────
async function updateConnectionStatus() {
    const dot  = document.getElementById('statusDot');
    const text = document.getElementById('statusText');
    if (!dot || !text) return;

    const online = await checkHealth();
    dot.className    = 'status-dot ' + (online ? 'connected' : 'disconnected');
    text.textContent = online ? 'Connected' : 'Server offline';
}

// ─── Keyboard Shortcuts ────────────────────────────────────────
document.addEventListener('keydown', e => {
    if (e.key === 'Escape') { closeModal(); closeChartModal(); closeManageModal(); closeDrawer(); }
    if (e.key === 'Enter' && document.activeElement.id === 'portfolioName') createPortfolio();
    if (e.key === 'Enter' && ['stockSymbol','stockQty','stockPrice','stockDate'].includes(document.activeElement.id)) addStock();
});

// ─── Symbol auto-uppercase ────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {
    const sym = document.getElementById('stockSymbol');
    if (sym) sym.addEventListener('input', () => {
        const p = sym.selectionStart;
        sym.value = sym.value.toUpperCase();
        sym.setSelectionRange(p, p);
    });
});

// ─── Mobile swipe-to-close sidebar ────────────────────────────
(function () {
    let sx = 0;
    document.addEventListener('touchstart', e => { sx = e.touches[0].clientX; }, { passive: true });
    document.addEventListener('touchend', e => {
        if (e.changedTouches[0].clientX - sx < -60) {
            const sb = document.getElementById('sidebar');
            if (sb && sb.classList.contains('open')) closeDrawer();
        }
    }, { passive: true });
})();

// ─── Init ──────────────────────────────────────────────────────
async function init() {
    initTheme();
    initSymbolAutocomplete();
    await updateConnectionStatus();
    await loadPortfolios();
    setInterval(updateConnectionStatus, 30_000);
}

init();
