/**
 * utils.js
 * Shared utility functions used across the entire application.
 * Covers formatting, toast notifications, modal dialogs, DOM helpers,
 * theme management and mobile drawer control.
 */

// ─── Number Formatting ────────────────────────────────────────────────────

/**
 * Format a number as a USD dollar amount.
 * e.g. 1234.5 → "$1,234.50"
 */
function formatCurrency(value) {
    if (value == null || isNaN(value)) return '$0.00';
    const num = Number(value);
    const formatted = Math.abs(num).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return (num < 0 ? '-$' : '$') + formatted;
}

/**
 * Format a percentage value with a sign prefix.
 * e.g. 12.34 → "+12.34%", -5.6 → "-5.60%"
 */
function formatPercent(value) {
    if (value == null || isNaN(value)) return '0.00%';
    const num = Number(value);
    return (num >= 0 ? '+' : '') + num.toFixed(2) + '%';
}

/**
 * Return 'pos' or 'neg' CSS class based on value sign.
 */
function signClass(value) {
    return Number(value) >= 0 ? 'pos' : 'neg';
}

/**
 * Format a decimal number to fixed places.
 */
function formatNumber(value, decimals = 2) {
    if (value == null || isNaN(value)) return '0';
    return Number(value).toFixed(decimals);
}

/**
 * Shorten a large number for display (e.g. 1500000 → "1.5M")
 */
function formatCompact(value) {
    const num = Number(value);
    if (num >= 1_000_000) return '$' + (num / 1_000_000).toFixed(1) + 'M';
    if (num >= 1_000)     return '$' + (num / 1_000).toFixed(1) + 'K';
    return formatCurrency(value);
}

// ─── Theme ────────────────────────────────────────────────────────────────

/**
 * Apply a theme ('light' | 'dark') to the document and persist it.
 */
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('folio-theme', theme);
}

/**
 * Toggle between light and dark mode.
 */
function toggleTheme() {
    const current = document.documentElement.getAttribute('data-theme') || 'light';
    applyTheme(current === 'light' ? 'dark' : 'light');
}

/**
 * Load saved theme preference on startup.
 */
function initTheme() {
    const saved = localStorage.getItem('folio-theme');
    if (saved) {
        applyTheme(saved);
    } else {
        // Respect system preference as fallback
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        applyTheme(prefersDark ? 'dark' : 'light');
    }
}

// ─── Mobile Drawer ────────────────────────────────────────────────────────

function openDrawer() {
    document.getElementById('sidebar').classList.add('open');
    document.getElementById('drawerOverlay').classList.add('open');
    document.body.style.overflow = 'hidden';
}

function closeDrawer() {
    document.getElementById('sidebar').classList.remove('open');
    document.getElementById('drawerOverlay').classList.remove('open');
    document.body.style.overflow = '';
}

// ─── Toast Notifications ──────────────────────────────────────────────────

const TOAST_DURATION = 3500;

/**
 * Show a toast notification.
 * @param {string} message - The message to display
 * @param {'success'|'error'|'info'} type - Visual style
 */
function showToast(message, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const icons = { success: '✓', error: '✕', info: 'i' };

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerHTML = `
        <span class="toast-icon">${icons[type] || '●'}</span>
        <span>${escapeHTML(message)}</span>
    `;

    container.appendChild(toast);

    requestAnimationFrame(() => {
        requestAnimationFrame(() => toast.classList.add('show'));
    });

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 350);
    }, TOAST_DURATION);
}

// ─── Modal Dialog ─────────────────────────────────────────────────────────

let _modalCallback = null;

/**
 * Show a confirmation modal dialog.
 * @param {string} title       - Modal heading
 * @param {string} body        - Modal message
 * @param {Function} onConfirm - Called when user confirms
 * @param {string} confirmLabel- Text on confirm button
 */
function showModal(title, body, onConfirm, confirmLabel = 'Confirm') {
    document.getElementById('modalTitle').textContent   = title;
    document.getElementById('modalBody').textContent    = body;
    document.getElementById('modalConfirm').textContent = confirmLabel;
    document.getElementById('modalOverlay').classList.add('open');

    _modalCallback = onConfirm;

    document.getElementById('modalConfirm').onclick = () => {
        closeModal();
        if (_modalCallback) _modalCallback();
    };
}

function closeModal() {
    document.getElementById('modalOverlay').classList.remove('open');
    _modalCallback = null;
}

// ─── DOM Helpers ──────────────────────────────────────────────────────────

/** Set element text content safely. */
function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text;
}

/** Set element inner HTML. */
function setHTML(id, html) {
    const el = document.getElementById(id);
    if (el) el.innerHTML = html;
}

/** Add or remove a CSS class based on a condition. */
function toggleClass(el, className, condition) {
    if (typeof el === 'string') el = document.getElementById(el);
    if (!el) return;
    el.classList.toggle(className, condition);
}

/** Show or hide an element. */
function showElement(id, show = true, display = 'block') {
    const el = document.getElementById(id);
    if (el) el.style.display = show ? display : 'none';
}

/** Disable/enable a button and optionally change its text. */
function setButtonLoading(id, loading, loadingText = 'Loading…', originalText = null) {
    const btn = document.getElementById(id);
    if (!btn) return;
    btn.disabled = loading;
    if (loading) {
        btn.dataset.originalText = btn.textContent.trim();
        btn.textContent = loadingText;
    } else {
        btn.textContent = originalText || btn.dataset.originalText || btn.textContent;
    }
}

/** Get today's date as YYYY-MM-DD. */
function todayString() {
    return new Date().toISOString().split('T')[0];
}

/** Escape HTML to prevent XSS. */
function escapeHTML(str) {
    const div = document.createElement('div');
    div.appendChild(document.createTextNode(String(str)));
    return div.innerHTML;
}
