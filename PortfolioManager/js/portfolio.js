/**
 * portfolio.js — Portfolio CRUD and rendering.
 */

let _selectedId   = null;
let _selectedData = null;

// ─── Load portfolio list ───────────────────────────────────────
async function loadPortfolios() {
    try {
        const list = await getAllPortfolios();
        renderPortfolioList(list);
    } catch {
        const el = document.getElementById('portfolioList');
        if (el) el.innerHTML = `<div style="padding:12px 0;color:var(--neg);font-size:12px;font-family:var(--font-mono);line-height:1.5">
            Could not load portfolios.<br>Is the server running on port 8080?</div>`;
    }
}

function renderPortfolioList(portfolios) {
    const el = document.getElementById('portfolioList');
    if (!el) return;

    if (!portfolios || !portfolios.length) {
        el.innerHTML = `<div style="padding:20px 0;text-align:center;color:var(--text3);font-size:12px;line-height:1.6">
            No portfolios yet.<br>Create one above.</div>`;
        return;
    }

    el.innerHTML = portfolios.map(p => {
        const sc = signClass(p.totalProfitLossPercentage);
        return `
        <div class="portfolio-item ${p.id === _selectedId ? 'active' : ''}" onclick="selectPortfolio(${p.id})">
            <div class="pi-name">${escapeHTML(p.name)}</div>
            <div class="pi-meta">${p.numberOfStocks} holding${p.numberOfStocks !== 1 ? 's' : ''} · ${p.createdDate || '—'}</div>
            <div class="pi-stats">
                <span class="pi-value">${formatCurrency(p.totalValue)}</span>
                <span class="pi-badge ${sc}">${formatPercent(p.totalProfitLossPercentage)}</span>
            </div>
        </div>`;
    }).join('');
}

// ─── Create portfolio ──────────────────────────────────────────
async function createPortfolio() {
    const name = document.getElementById('portfolioName').value.trim();
    const desc = document.getElementById('portfolioDesc').value.trim();
    if (!name) { showToast('Portfolio name is required', 'error'); return; }

    setButtonLoading('createPortfolioBtn', true, 'Creating…');
    try {
        const p = await createPortfolioAPI(name, desc);
        document.getElementById('portfolioName').value = '';
        document.getElementById('portfolioDesc').value = '';
        showToast(`"${p.name}" created`);
        await loadPortfolios();
        selectPortfolio(p.id);
        closeDrawer();
    } catch (e) {
        showToast(e.message, 'error');
    } finally {
        setButtonLoading('createPortfolioBtn', false, '', 'Create Portfolio');
    }
}

// ─── Select portfolio ──────────────────────────────────────────
async function selectPortfolio(id) {
    _selectedId = id;

    showElement('welcomeScreen', false);
    showElement('portfolioView', true, 'flex');
    showElement('topnavBreadcrumb', true, 'flex');
    showElement('topnavActions',    true, 'flex');

    ['totalValue','totalPnl','totalReturn','totalInvested','totalHoldings']
        .forEach(k => setText(k, '…'));

    closeDrawer();

    try {
        const p = await getPortfolioById(id);
        _selectedData = p;
        renderPortfolioView(p);
        await loadPortfolios();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

function deselectPortfolio() {
    _selectedId = _selectedData = null;
    clearChart();
    clearAllocationChart();
    clearPerformanceChart();
    showElement('portfolioView',    false);
    showElement('welcomeScreen',    true, 'flex');
    showElement('topnavBreadcrumb', false);
    showElement('topnavActions',    false);
    loadPortfolios();
}

// ─── Render view ───────────────────────────────────────────────
function renderPortfolioView(p) {
    setText('portfolioTitle',    p.name);
    setText('mobilePortfolioName', p.name);
    setText('portfolioMeta',
        `${p.numberOfStocks} holding${p.numberOfStocks !== 1 ? 's' : ''} · Created ${p.createdDate || '—'}`);

    const invested = (p.stocks || []).reduce((s, st) => s + st.purchasePrice * st.quantity, 0);
    setText('totalValue',    formatCurrency(p.totalValue));
    setText('totalInvested', formatCurrency(invested));
    setText('totalHoldings', p.numberOfStocks);

    const pnlEl    = document.getElementById('totalPnl');
    const retEl    = document.getElementById('totalReturn');
    if (pnlEl) { pnlEl.textContent = formatCurrency(p.totalProfitLoss); pnlEl.className = `summary-value ${signClass(p.totalProfitLoss)}`; }
    if (retEl) { retEl.textContent = formatPercent(p.totalProfitLossPercentage);  retEl.className = `summary-value ${signClass(p.totalProfitLossPercentage)}`; }

    renderHoldingsTable(p.stocks, p.id);
    renderAllocation(p.stocks);
    renderPerformanceChart(p.stocks);

    const dateEl = document.getElementById('stockDate');
    if (dateEl && !dateEl.value) dateEl.value = todayString();
}

// ─── Delete portfolio ──────────────────────────────────────────
function deleteCurrentPortfolio() {
    if (!_selectedId) return;
    const name = _selectedData?.name || 'this portfolio';
    showModal('Delete Portfolio',
        `Delete "${name}" and all its stocks? This cannot be undone.`,
        async () => {
            try {
                await deletePortfolio(_selectedId);
                showToast(`"${name}" deleted`);
                deselectPortfolio();
            } catch (e) { showToast(e.message, 'error'); }
        }, 'Delete');
}

// ─── Refresh ───────────────────────────────────────────────────
async function refreshPortfolio() {
    if (!_selectedId) return;
    showToast('Refreshing…', 'info');
    await selectPortfolio(_selectedId);
}

// ─── Allocation pie chart ──────────────────────────────────────
let _allocationChart = null;

const ALLOC_COLORS = ['#1a6bf5','#0d9e6e','#f59e0b','#d63b3b','#a855f7','#06b6d4','#ec4899','#84cc16','#f97316','#14b8a6'];

function clearAllocationChart() {
    if (_allocationChart) { _allocationChart.destroy(); _allocationChart = null; }
}

function renderAllocation(stocks) {
    const canvas = document.getElementById('allocationChart');
    const wrap   = document.getElementById('allocationChartWrap');
    const list   = document.getElementById('allocationList');

    clearAllocationChart();

    if (!stocks || !stocks.length) {
        if (wrap) wrap.style.display = 'none';
        if (list) list.innerHTML = '<div class="allocation-empty">Add stocks to see allocation</div>';
        return;
    }

    const total = stocks.reduce((s, st) => s + (Number(st.currentValue) || 0), 0);
    if (!total) {
        if (wrap) wrap.style.display = 'none';
        if (list) list.innerHTML = '<div class="allocation-empty">Prices loading…</div>';
        return;
    }

    const sorted = [...stocks].sort((a, b) => (Number(b.currentValue)||0) - (Number(a.currentValue)||0));

    if (wrap) wrap.style.display = 'block';

    if (canvas) {
        const ctx = canvas.getContext('2d');
        _allocationChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: sorted.map(st => st.symbol),
                datasets: [{
                    data: sorted.map(st => Number(st.currentValue) || 0),
                    backgroundColor: sorted.map((_, i) => ALLOC_COLORS[i % ALLOC_COLORS.length]),
                    borderWidth: 0,
                    hoverOffset: 6,
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                cutout: '62%',
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(17,19,24,.95)',
                        borderColor: 'rgba(255,255,255,.08)',
                        borderWidth: 1,
                        titleColor: '#8b93a4',
                        bodyColor: '#f0f1f5',
                        titleFont: { family: 'Geist Mono', size: 11 },
                        bodyFont:  { family: 'Geist Mono', size: 12 },
                        padding: 10,
                        callbacks: {
                            label: (item) => {
                                const st  = sorted[item.dataIndex];
                                const pct = (Number(st.currentValue) / total * 100).toFixed(1);
                                const sign = Number(st.profitLoss) >= 0 ? '+' : '';
                                return [
                                    `  ${formatCurrency(Number(st.currentValue))}  (${pct}%)`,
                                    `  P&L: ${sign}${formatCurrency(Number(st.profitLoss))}`
                                ];
                            }
                        }
                    }
                }
            }
        });
    }

    if (list) {
        list.innerHTML = sorted.map((st, i) => {
            const pct  = (Number(st.currentValue) || 0) / total * 100;
            const sc   = signClass(st.profitLoss);
            const sign = Number(st.profitLoss) >= 0 ? '+' : '';
            return `
            <div class="allocation-item" data-symbol="${escapeHTML(st.symbol)}">
                <span class="alloc-dot" style="background:${ALLOC_COLORS[i % ALLOC_COLORS.length]}"></span>
                <div class="alloc-label">
                    <span class="allocation-ticker">${escapeHTML(st.symbol)}</span>
                    <span class="allocation-pct">${pct.toFixed(1)}%</span>
                </div>
                <span class="alloc-pnl ${sc}">${sign}${formatCurrency(Number(st.profitLoss))}</span>
            </div>`;
        }).join('');
    }
}

// ─── Highlight a donut segment when a stock is selected ────────
function highlightAllocationSegment(symbol) {
    if (!_allocationChart) return;
    const idx = _allocationChart.data.labels.indexOf(symbol);
    if (idx === -1) return;

    _allocationChart.setActiveElements([{ datasetIndex: 0, index: idx }]);
    _allocationChart.tooltip.setActiveElements([{ datasetIndex: 0, index: idx }], { x: 0, y: 0 });
    _allocationChart.update('none');

    document.querySelectorAll('#allocationList .allocation-item').forEach(el =>
        el.classList.toggle('alloc-active', el.dataset.symbol === symbol));
}

function clearAllocationHighlight() {
    if (!_allocationChart) return;
    _allocationChart.setActiveElements([]);
    _allocationChart.tooltip.setActiveElements([], { x: 0, y: 0 });
    _allocationChart.update('none');
    document.querySelectorAll('#allocationList .allocation-item').forEach(el =>
        el.classList.remove('alloc-active'));
}

// ─── Performance (returns) bar chart ──────────────────────────
let _performanceChart = null;

function clearPerformanceChart() {
    if (_performanceChart) { _performanceChart.destroy(); _performanceChart = null; }
    const card = document.getElementById('performanceCard');
    if (card) card.style.display = 'none';
}

function renderPerformanceChart(stocks) {
    const canvas = document.getElementById('performanceChart');
    const card   = document.getElementById('performanceCard');
    clearPerformanceChart();

    if (!stocks || stocks.length < 1 || !canvas) return;

    const sorted = [...stocks].sort((a, b) =>
        (b.profitLossPercentage || 0) - (a.profitLossPercentage || 0));

    const labels    = sorted.map(s => s.symbol);
    const values    = sorted.map(s => s.profitLossPercentage || 0);
    const bgColors  = values.map(v => v >= 0 ? 'rgba(13,158,110,0.75)' : 'rgba(214,59,59,0.75)');
    const bdrColors = values.map(v => v >= 0 ? '#0d9e6e' : '#d63b3b');

    // Dynamic canvas height
    const wrap = document.getElementById('performanceChartWrap');
    if (wrap) wrap.style.height = Math.max(80, sorted.length * 34) + 'px';
    if (card) card.style.display = '';

    const ctx = canvas.getContext('2d');
    _performanceChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels,
            datasets: [{
                data: values,
                backgroundColor: bgColors,
                borderColor: bdrColors,
                borderWidth: 1,
                borderRadius: 4,
                borderSkipped: false,
            }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: 'rgba(17,19,24,.95)',
                    borderColor: 'rgba(255,255,255,.08)',
                    borderWidth: 1,
                    titleColor: '#8b93a4',
                    bodyColor: '#f0f1f5',
                    titleFont: { family: 'Geist Mono', size: 11 },
                    bodyFont:  { family: 'Geist Mono', size: 12 },
                    padding: 10,
                    callbacks: {
                        label: item => {
                            const st   = sorted[item.dataIndex];
                            const sign = item.raw >= 0 ? '+' : '';
                            return [
                                `  Return: ${sign}${item.raw.toFixed(2)}%`,
                                `  P&L: ${sign}${formatCurrency(Number(st.profitLoss))}`
                            ];
                        }
                    }
                }
            },
            scales: {
                x: {
                    grid:   { color: 'rgba(0,0,0,.05)', drawBorder: false },
                    ticks:  { color: '#8b93a4', font: { size: 10 }, callback: v => (v >= 0 ? '+' : '') + v + '%' },
                    border: { display: false }
                },
                y: {
                    grid:   { display: false },
                    ticks:  { color: '#8b93a4', font: { family: 'Geist Mono', size: 11 } },
                    border: { display: false }
                }
            }
        }
    });
}
