/**
 * charts.js — Chart.js integration for stock price history.
 */

let _chart         = null;
let _chartExpanded = null;
let _activeStock   = null;
let _activePeriod  = null; 
let _lastTrends    = null;

// ─── Load chart for a stock (populates small card + stores data) ──
async function loadStockChart(portfolioId, stockId, symbol, name, period = '6m') {
    _activeStock  = { portfolioId, stockId, symbol, name };
    _activePeriod = period;

    setText('chartTitle',    name);
    setText('chartSubtitle', `${symbol} · ${period.toUpperCase()}`);
    showElement('expandChartBtn',   true, 'flex');
    showElement('chartPlaceholder', false);
    showElement('chartWrap',        true, 'block');

    try {
        const data = await getStockTrends(portfolioId, stockId, period);

        if (!data.trends || !data.trends.length) {
            showToast('No data available for this period', 'info');
            showElement('chartWrap',        false);
            showElement('chartPlaceholder', true);
            return;
        }

        const trends = [...data.trends].reverse();
        _lastTrends  = { trends, symbol, period };
        renderChartOnCanvas('priceChart', trends, period);

    } catch {
        showToast('Could not load chart data', 'error');
        showElement('chartWrap',        false);
        showElement('chartPlaceholder', true);
    }
}

// ─── Period button handler (expanded modal only) ───────────────
async function changePeriod(_btn, period) {
    if (!_activeStock) return;
    const { portfolioId, stockId, symbol } = _activeStock;
    _activePeriod = period;

    document.querySelectorAll('#chartModalControls .period-btn').forEach(b =>
        toggleClass(b, 'active', b.dataset.period === period));

    setText('chartModalSubtitle', `${symbol} · ${period.toUpperCase()}`);
    setText('chartSubtitle',      `${symbol} · ${period.toUpperCase()}`);

    try {
        const data = await getStockTrends(portfolioId, stockId, period);
        if (!data.trends || !data.trends.length) {
            showToast('No data available for this period', 'info'); return;
        }
        const trends = [...data.trends].reverse();
        _lastTrends  = { trends, symbol, period };
        renderChartOnCanvas('priceChart',         trends, period);
        renderChartOnCanvas('priceChartExpanded', trends, period);
    } catch {
        showToast('Could not load chart data', 'error');
    }
}

function changeExpandedPeriod(btn, period) {
    return changePeriod(btn, period);
}

// ─── Expand chart to fullscreen ────────────────────────────────
function expandChart() {
    if (!_lastTrends || !_activeStock) return;
    const { symbol, name } = _activeStock;

    setText('chartModalTitle',    name);
    setText('chartModalSubtitle', `${symbol} · ${_activePeriod.toUpperCase()}`);

    document.querySelectorAll('#chartModalControls .period-btn').forEach(b =>
        toggleClass(b, 'active', b.dataset.period === _activePeriod));

    document.getElementById('chartModal').classList.add('open');

    if (_chartExpanded) { _chartExpanded.destroy(); _chartExpanded = null; }
    setTimeout(() => renderChartOnCanvas('priceChartExpanded', _lastTrends.trends, _lastTrends.period), 40);
}

function closeChartModal() {
    document.getElementById('chartModal').classList.remove('open');
    if (_chartExpanded) { _chartExpanded.destroy(); _chartExpanded = null; }
}

// ─── Render on a specific canvas ──────────────────────────────
function renderChartOnCanvas(canvasId, trends, period) {
    const canvas = document.getElementById(canvasId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');

    if (canvasId === 'priceChart'         && _chart)         { _chart.destroy();         _chart         = null; }
    if (canvasId === 'priceChartExpanded' && _chartExpanded) { _chartExpanded.destroy(); _chartExpanded = null; }

    const labels    = trends.map(t => fmtDate(t.date, period));
    const prices    = trends.map(t => parseFloat(t.price));
    const isPos     = prices[prices.length - 1] >= prices[0];
    const lineColor = isPos ? '#1a6bf5' : '#d63b3b';
    const gradA     = isPos ? 'rgba(26,107,245,0.14)' : 'rgba(214,59,59,0.14)';
    const gradB     = isPos ? 'rgba(26,107,245,0)'    : 'rgba(214,59,59,0)';

    const grad = ctx.createLinearGradient(0, 0, 0, 260);
    grad.addColorStop(0, gradA);
    grad.addColorStop(1, gradB);

    const isSmall      = canvasId === 'priceChart';
    const maxTicksLimit = isSmall ? 4 : 8;

    const instance = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                data: prices,
                borderColor: lineColor,
                backgroundColor: grad,
                borderWidth: 2,
                pointBackgroundColor: lineColor,
                pointRadius: prices.length > 20 ? 0 : 3,
                pointHoverRadius: 6,
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: lineColor,
                pointHoverBorderWidth: 2,
                fill: true,
                tension: 0.35
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { intersect: false, mode: 'index' },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: 'rgba(17,19,24,.95)',
                    borderColor: 'rgba(255,255,255,.08)',
                    borderWidth: 1,
                    titleColor: '#8b93a4',
                    bodyColor: '#f0f1f5',
                    titleFont: { family: 'Geist Mono', size: 11 },
                    bodyFont:  { family: 'Geist Mono', size: 13 },
                    padding: 12,
                    callbacks: { label: i => ' $' + Number(i.raw).toFixed(2) }
                }
            },
            scales: {
                x: {
                    grid:  { color: 'rgba(0,0,0,.05)', drawBorder: false },
                    ticks: { color: '#8b93a4', font: { size: 10 }, maxRotation: 0, maxTicksLimit, autoSkip: true },
                    border: { display: false }
                },
                y: {
                    position: 'right',
                    grid:  { color: 'rgba(0,0,0,.05)', drawBorder: false },
                    ticks: { color: '#8b93a4', font: { size: 10 }, callback: v => '$' + v.toFixed(0), maxTicksLimit: 5 },
                    border: { display: false }
                }
            }
        }
    });

    if (canvasId === 'priceChart')         _chart         = instance;
    if (canvasId === 'priceChartExpanded') _chartExpanded = instance;
}

// ─── Date formatter ────────────────────────────────────────────
function fmtDate(str, period) {
    const s = str.length > 10 ? str.replace(' ', 'T') : str + 'T00:00:00';
    const d = new Date(s);
    if (period === '1d') {
        return d.toLocaleTimeString('en-US', { hour: 'numeric', minute: '2-digit', hour12: true });
    }
    if (['5d', '1w', '1m'].includes(period)) {
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
    }
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: '2-digit' });
}

// ─── Clear chart ───────────────────────────────────────────────
function clearChart() {
    if (_chart)         { _chart.destroy();         _chart         = null; }
    if (_chartExpanded) { _chartExpanded.destroy(); _chartExpanded = null; }
    _activeStock = null;
    _lastTrends  = null;
    showElement('chartWrap',        false);
    showElement('chartPlaceholder', true);
    showElement('expandChartBtn',   false);
    setText('chartTitle',    'Price History');
    setText('chartSubtitle', 'Click any stock row to view its chart');
}
