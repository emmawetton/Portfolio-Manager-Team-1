/**
 * charts.js
 * Manages all Chart.js chart instances used in the application.
 * Reads CSS variables at render time so charts respect light/dark mode.
 */

let _priceChart     = null;
let _activeTrendStock = null;
let _activePeriod   = 6;

// ─── CSS variable helpers ─────────────────────────────────────────────────

/** Read a computed CSS variable from :root */
function cssVar(name) {
    return getComputedStyle(document.documentElement).getPropertyValue(name).trim();
}

// ─── Price History Chart ──────────────────────────────────────────────────

/**
 * Load and display price trend data for a given stock.
 */
async function loadStockChart(portfolioId, stockId, symbol, name, months = 6) {
    _activeTrendStock = { portfolioId, stockId, symbol, name };
    _activePeriod     = months;

    setText('chartTitle',    name);
    setText('chartSubtitle', `${symbol} · ${months}M price history`);
    showElement('chartControls', true, 'flex');
    showElement('chartPlaceholder', false);
    showElement('chartWrap', true, 'block');

    // Highlight active period button
    document.querySelectorAll('.period-btn').forEach(btn => {
        toggleClass(btn, 'active', parseInt(btn.dataset.months) === months);
    });

    try {
        const data   = await getStockTrends(portfolioId, stockId, months);
        const trends = [...data.trends].reverse(); // chronological order
        renderPriceChart(trends, symbol);
    } catch (e) {
        showToast('Could not load chart data', 'error');
        showElement('chartWrap', false);
        showElement('chartPlaceholder', true);
    }
}

/**
 * Called when user clicks a period button (3M, 6M, 1Y).
 */
async function changePeriod(btn, months) {
    if (!_activeTrendStock) return;
    const { portfolioId, stockId, symbol, name } = _activeTrendStock;
    await loadStockChart(portfolioId, stockId, symbol, name, months);
}

/**
 * Render or update the Chart.js price chart.
 * Uses CSS variables to stay in sync with the active theme.
 */
function renderPriceChart(trends, symbol) {
    const canvas = document.getElementById('priceChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    const labels = trends.map(t => formatChartDate(t.date));
    const prices = trends.map(t => parseFloat(t.price));

    const firstPrice = prices[0]                 || 0;
    const lastPrice  = prices[prices.length - 1] || 0;
    const isPositive = lastPrice >= firstPrice;

    // Resolve colours from CSS variables for theme-awareness
    const lineColor    = isPositive ? cssVar('--pos') : cssVar('--neg');
    const gradientStop = isPositive
        ? ['rgba(13,158,110,0.14)', 'rgba(13,158,110,0)']
        : ['rgba(214,59,59,0.14)',  'rgba(214,59,59,0)'];

    const gridColor    = document.documentElement.getAttribute('data-theme') === 'dark'
        ? 'rgba(255,255,255,0.04)'
        : 'rgba(0,0,0,0.05)';

    const tickColor    = cssVar('--text-3');
    const tooltipBg    = cssVar('--surface');
    const tooltipBorder= cssVar('--border');
    const tooltipTitle = cssVar('--text-2');
    const tooltipBody  = cssVar('--text-1');

    // Destroy old chart
    if (_priceChart) {
        _priceChart.destroy();
        _priceChart = null;
    }

    // Gradient fill
    const gradient = ctx.createLinearGradient(0, 0, 0, 200);
    gradient.addColorStop(0, gradientStop[0]);
    gradient.addColorStop(1, gradientStop[1]);

    _priceChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels,
            datasets: [{
                label: symbol,
                data: prices,
                borderColor: lineColor,
                backgroundColor: gradient,
                borderWidth: 2,
                pointBackgroundColor: lineColor,
                pointBorderColor: lineColor,
                pointRadius: 2,
                pointHoverRadius: 5,
                pointHoverBackgroundColor: tooltipBg,
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
                    backgroundColor: tooltipBg,
                    borderColor: tooltipBorder,
                    borderWidth: 1,
                    titleColor: tooltipTitle,
                    bodyColor: tooltipBody,
                    titleFont: { family: 'Geist Mono, monospace', size: 11 },
                    bodyFont: { family: 'Geist Mono, monospace', size: 13 },
                    padding: 12,
                    displayColors: false,
                    callbacks: {
                        title: (items) => items[0].label,
                        label: (item) => ' $' + Number(item.raw).toFixed(2)
                    }
                }
            },
            scales: {
                x: {
                    grid: { color: gridColor, drawBorder: false },
                    ticks: {
                        color: tickColor,
                        font: { family: 'Geist Mono, monospace', size: 10 },
                        maxRotation: 0,
                        maxTicksLimit: 8
                    },
                    border: { display: false }
                },
                y: {
                    position: 'right',
                    grid: { color: gridColor, drawBorder: false },
                    ticks: {
                        color: tickColor,
                        font: { family: 'Geist Mono, monospace', size: 10 },
                        callback: (val) => '$' + val.toFixed(0),
                        maxTicksLimit: 5
                    },
                    border: { display: false }
                }
            }
        }
    });
}

/**
 * Format a YYYY-MM-DD date string into a short readable label.
 * e.g. "2024-03-01" → "Mar '24"
 */
function formatChartDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', { month: 'short', year: '2-digit' });
}

/**
 * Destroy and reset the price chart.
 */
function clearChart() {
    if (_priceChart) {
        _priceChart.destroy();
        _priceChart = null;
    }
    _activeTrendStock = null;
    showElement('chartWrap', false);
    showElement('chartPlaceholder', true);
    showElement('chartControls', false);
    setText('chartTitle',    'Price History');
    setText('chartSubtitle', 'Select a stock to view its chart');
}
