/**
 * utils.js — Shared helpers: formatting, theme, drawer, toast, modal, DOM.
 */

// ─── Currency & Number Formatting ─────────────────────────────
function formatCurrency(v) {
    if (v == null || isNaN(v)) return '$0.00';
    const n = Number(v);
    const f = Math.abs(n).toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',');
    return (n < 0 ? '-$' : '$') + f;
}

function formatPercent(v) {
    if (v == null || isNaN(v)) return '+0.00%';
    const n = Number(v);
    return (n >= 0 ? '+' : '') + n.toFixed(2) + '%';
}

function formatNumber(v, d = 2) {
    if (v == null || isNaN(v)) return '0';
    return Number(v).toFixed(d);
}

function signClass(v) { return Number(v) >= 0 ? 'pos' : 'neg'; }

// ─── Theme ─────────────────────────────────────────────────────
function applyTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    try { localStorage.setItem('folio-theme', theme); } catch {}
}

function toggleTheme() {
    const cur = document.documentElement.getAttribute('data-theme') || 'light';
    applyTheme(cur === 'light' ? 'dark' : 'light');
}

function initTheme() {
    try {
        const saved = localStorage.getItem('folio-theme');
        if (saved) { applyTheme(saved); return; }
    } catch {}
    applyTheme(window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
}

// ─── Mobile Drawer ──────────────────────────────────────────────
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

// ─── Toast ──────────────────────────────────────────────────────
const TOAST_MS = 3500;

function showToast(msg, type = 'success') {
    const container = document.getElementById('toastContainer');
    if (!container) return;

    const icons = { success: '✓', error: '✕', info: 'i' };
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.innerHTML = `<span class="toast-icon">${icons[type] || '●'}</span><span>${escapeHTML(String(msg))}</span>`;
    container.appendChild(el);

    requestAnimationFrame(() => requestAnimationFrame(() => el.classList.add('show')));
    setTimeout(() => {
        el.classList.remove('show');
        setTimeout(() => el.remove(), 350);
    }, TOAST_MS);
}

// ─── Modal ──────────────────────────────────────────────────────
let _modalCb = null;

function showModal(title, body, onConfirm, label = 'Confirm') {
    document.getElementById('modalTitle').textContent   = title;
    document.getElementById('modalBody').textContent    = body;
    document.getElementById('modalConfirm').textContent = label;
    document.getElementById('modalOverlay').classList.add('open');
    _modalCb = onConfirm;
    document.getElementById('modalConfirm').onclick = () => { const cb = _modalCb; closeModal(); if (cb) cb(); };
}

function closeModal() {
    document.getElementById('modalOverlay').classList.remove('open');
    _modalCb = null;
}

// ─── DOM Helpers ─────────────────────────────────────────────────
function setText(id, text) {
    const el = document.getElementById(id);
    if (el) el.textContent = text;
}

function showElement(id, show = true, display = 'block') {
    const el = document.getElementById(id);
    if (el) el.style.display = show ? display : 'none';
}

function toggleClass(el, cls, cond) {
    if (typeof el === 'string') el = document.getElementById(el);
    if (el) el.classList.toggle(cls, cond);
}

function setButtonLoading(id, loading, loadingText = 'Loading…', orig = null) {
    const btn = document.getElementById(id);
    if (!btn) return;
    btn.disabled = loading;
    if (loading) { btn.dataset.orig = btn.textContent.trim(); btn.textContent = loadingText; }
    else btn.textContent = orig || btn.dataset.orig || btn.textContent;
}

function todayString() { return new Date().toISOString().split('T')[0]; }

function escapeHTML(str) {
    const d = document.createElement('div');
    d.appendChild(document.createTextNode(String(str)));
    return d.innerHTML;
}
