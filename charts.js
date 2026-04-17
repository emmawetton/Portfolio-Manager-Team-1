/**
 * charts.js
 * Manages all Chart.js chart instances used in the application.
 * Currently handles the stock price history line chart.
 */

let _priceChart = null;
let _activeTrendStock = null;
let _activePeriod = 6;

// ─── Price History Chart ──────────────────────────────────────────────────

/**
 * Load and display price trend data for a given stock.
 * @param {number} portfolioId
 * @param {number} stockId
 * @param {string} symbol
 * @param {string} name
 * @param {number} months - Number of months of history to fetch
 */
async function loadStockChart(portfolioId, stockId, symbol, name, months = 6) {
    _activeTrendStock = { portfolioId, stockId, symbol, name };
    _activePeriod = months;

    // Update panel header
    setText('chartTitle', `${name}`);
    setText('chartSubtitle', `${symbol} · ${months}M price history`);
    showElement('chartControls', true, 'flex');
    showElement('chartPlaceholder', false);
    showElement('chartWrap', true, 'block');

    // Set active period button
    document.querySelectorAll('.period-btn').forEach(btn => {
        toggleClass(btn, 'active', parseInt(btn.dataset.months) === months);
    });

    try {
        const data = await getStockTrends(portfolioId, stockId, months);
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
 */
function renderPriceChart(trends, symbol) {
    const canvas = document.getElementById('priceChart');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    const labels = trends.map(t => formatChartDate(t.date));
    const prices = trends.map(t => parseFloat(t.price));

    const firstPrice = prices[0] || 0;
    const lastPrice = prices[prices.length - 1] || 0;
    const isPositive = lastPrice >= firstPrice;

    const lineColor = isPositive ? '#00ff88' : '#ff3d55';
    const gradientColor = isPositive ? 'rgba(0,255,136,' : 'rgba(255,61,85,';

    // Destroy old chart if it exists
    if (_priceChart) {
        _priceChart.destroy();
        _priceChart = null;
    }

    // Build gradient fill
    const gradient = ctx.createLinearGradient(0, 0, 0, 220);
    gradient.addColorStop(0, gradientColor + '0.15)');
    gradient.addColorStop(1, gradientColor + '0)');

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
                pointRadius: 3,
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
            interaction: {
                intersect: false,
                mode: 'index'
            },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#191928',
                    borderColor: '#2a2a40',
                    borderWidth: 1,
                    titleColor: '#9090b0',
                    bodyColor: '#e2e2f0',
                    titleFont: { family: 'DM Mono', size: 11 },
                    bodyFont: { family: 'DM Mono', size: 13 },
                    padding: 12,
                    callbacks: {
                        title: (items) => items[0].label,
                        label: (item) => ' $' + Number(item.raw).toFixed(2)
                    }
                }
            },
            scales: {
                x: {
                    grid: {
                        color: 'rgba(255,255,255,0.03)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#505070',
                        font: { family: 'DM Mono', size: 10 },
                        maxRotation: 0
                    },
                    border: { display: false }
                },
                y: {
                    position: 'right',
                    grid: {
                        color: 'rgba(255,255,255,0.03)',
                        drawBorder: false
                    },
                    ticks: {
                        color: '#505070',
                        font: { family: 'DM Mono', size: 10 },
                        callback: (val) => '$' + val.toFixed(0),
                        maxTicksLimit: 6
                    },
                    border: { display: false }
                }
            }
        }
    });
}

/**
 * Format a YYYY-MM-DD date string into a short readable label.
 * e.g. "2024-03-01" → "Mar 24"
 */
function formatChartDate(dateStr) {
    const date = new Date(dateStr + 'T00:00:00');
    return date.toLocaleDateString('en-US', { month: 'short', year: '2-digit' });
}

/**
 * Destroy and clear the price chart.
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
    setText('chartTitle', 'Price History');
    setText('chartSubtitle', 'Select a stock to view its chart');
}
