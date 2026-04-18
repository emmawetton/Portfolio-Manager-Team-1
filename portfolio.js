/**
 * portfolio.js
 * Handles all portfolio-related UI logic:
 * creating, listing, selecting and deleting portfolios.
 */

let _selectedPortfolioId = null;
let _selectedPortfolioData = null;

// ─── Load & Render Portfolio List ─────────────────────────────────────────

/**
 * Fetch all portfolios from the API and render the sidebar list.
 */
async function loadPortfolios() {
    try {
        const portfolios = await getAllPortfolios();
        renderPortfolioList(portfolios);
    } catch (e) {
        document.getElementById('portfolioList').innerHTML = `
            <div style="padding:12px 4px;color:var(--danger);font-size:12px;font-family:var(--mono)">
                Could not load portfolios.<br>Is the server running?
            </div>`;
    }
}

/**
 * Render the portfolio list in the sidebar.
 */
function renderPortfolioList(portfolios) {
    const container = document.getElementById('portfolioList');

    if (!portfolios || portfolios.length === 0) {
        container.innerHTML = `
            <div style="padding:20px 4px;text-align:center;color:var(--text-3);font-size:12px;font-family:var(--mono)">
                No portfolios yet.<br>Create one above.
            </div>`;
        return;
    }

    container.innerHTML = portfolios.map(p => {
        const isActive = p.id === _selectedPortfolioId;
        const returnSign = signClass(p.totalProfitLossPercentage);
        return `
            <div class="portfolio-item ${isActive ? 'active' : ''}" onclick="selectPortfolio(${p.id})">
                <div class="pi-name">${escapeHTML(p.name)}</div>
                <div class="pi-meta">${p.numberOfStocks} holding${p.numberOfStocks !== 1 ? 's' : ''} · ${p.createdDate || '—'}</div>
                <div class="pi-stats">
                    <span class="pi-value">${formatCurrency(p.totalValue)}</span>
                    <span class="pi-return ${returnSign}">${formatPercent(p.totalProfitLossPercentage)}</span>
                </div>
            </div>`;
    }).join('');
}

// ─── Create Portfolio ─────────────────────────────────────────────────────

/**
 * Read form inputs, validate, call the API and refresh the list.
 */
async function createPortfolio() {
    const name = document.getElementById('portfolioName').value.trim();
    const description = document.getElementById('portfolioDesc').value.trim();

    if (!name) {
        showToast('Portfolio name is required', 'error');
        document.getElementById('portfolioName').focus();
        return;
    }

    setButtonLoading('createPortfolioBtn', true, '+ Creating...');

    try {
        const portfolio = await window.createPortfolio_API(name, description);
        document.getElementById('portfolioName').value = '';
        document.getElementById('portfolioDesc').value = '';
        showToast(`"${portfolio.name}" created`);
        await loadPortfolios();
        selectPortfolio(portfolio.id);
    } catch (e) {
        showToast(e.message, 'error');
    } finally {
        setButtonLoading('createPortfolioBtn', false, '', '+ Create Portfolio');
    }
}

// ─── Select Portfolio ─────────────────────────────────────────────────────

/**
 * Select a portfolio: fetch its full data and render the main view.
 */
async function selectPortfolio(id) {
    _selectedPortfolioId = id;

    showElement('welcomeScreen', false);
    showElement('portfolioView', true, 'flex');

    // Show loading state in summary
    setText('totalValue', '...');
    setText('totalPnl', '...');
    setText('totalReturn', '...');
    setText('totalInvested', '...');
    setText('totalHoldings', '...');

    try {
        const portfolio = await getPortfolioById(id);
        _selectedPortfolioData = portfolio;
        renderPortfolioView(portfolio);
        await loadPortfolios(); // refresh sidebar with latest values
    } catch (e) {
        showToast(e.message, 'error');
    }
}

/**
 * Deselect the current portfolio and show the welcome screen.
 */
function deselectPortfolio() {
    _selectedPortfolioId = null;
    _selectedPortfolioData = null;
    clearChart();
    showElement('portfolioView', false);
    showElement('welcomeScreen', true, 'flex');
    loadPortfolios();
}

// ─── Render Portfolio View ────────────────────────────────────────────────

/**
 * Populate all parts of the main portfolio view with data.
 */
function renderPortfolioView(portfolio) {
    // Topbar
    setText('topbarName', portfolio.name);
    setText('topbarMeta',
        `${portfolio.numberOfStocks} holding${portfolio.numberOfStocks !== 1 ? 's' : ''} · Created ${portfolio.createdDate || '—'}`
    );

    // Summary strip
    const totalInvested = calculateTotalInvested(portfolio.stocks);
    updateSummaryStrip(portfolio, totalInvested);

    // Holdings table
    renderHoldingsTable(portfolio.stocks, portfolio.id);

    // Allocation panel
    renderAllocation(portfolio.stocks);

    // Pre-fill the date input
    document.getElementById('stockDate').value = todayString();
}

/**
 * Update the summary strip numbers.
 */
function updateSummaryStrip(portfolio, totalInvested) {
    const totalValueEl = document.getElementById('totalValue');
    const totalPnlEl = document.getElementById('totalPnl');
    const totalReturnEl = document.getElementById('totalReturn');

    setText('totalValue', formatCurrency(portfolio.totalValue));
    setText('totalInvested', formatCurrency(totalInvested));
    setText('totalHoldings', portfolio.numberOfStocks);

    if (totalValueEl) {
        totalValueEl.className = 'summary-value';
    }

    if (totalPnlEl) {
        totalPnlEl.textContent = formatCurrency(portfolio.totalProfitLoss);
        totalPnlEl.className = `summary-value ${signClass(portfolio.totalProfitLoss)}`;
    }

    if (totalReturnEl) {
        totalReturnEl.textContent = formatPercent(portfolio.totalProfitLossPercentage);
        totalReturnEl.className = `summary-value ${signClass(portfolio.totalProfitLossPercentage)}`;
    }
}

/**
 * Calculate the total amount originally invested across all stocks.
 */
function calculateTotalInvested(stocks) {
    if (!stocks || !stocks.length) return 0;
    return stocks.reduce((sum, s) => sum + (s.purchasePrice * s.quantity), 0);
}

// ─── Delete Portfolio ─────────────────────────────────────────────────────

/**
 * Show a confirmation modal then delete the current portfolio.
 */
function deleteCurrentPortfolio() {
    if (!_selectedPortfolioId) return;
    const name = _selectedPortfolioData?.name || 'this portfolio';

    showModal(
        'Delete Portfolio',
        `Are you sure you want to delete "${name}" and all its stocks? This cannot be undone.`,
        async () => {
            try {
                await deletePortfolio(_selectedPortfolioId);
                showToast(`"${name}" deleted`);
                deselectPortfolio();
            } catch (e) {
                showToast(e.message, 'error');
            }
        },
        'Delete'
    );
}

// ─── Refresh Portfolio ────────────────────────────────────────────────────

/**
 * Re-fetch and re-render the current portfolio.
 */
async function refreshPortfolio() {
    if (!_selectedPortfolioId) return;
    showToast('Refreshing prices...', 'info');
    await selectPortfolio(_selectedPortfolioId);
}

// ─── Allocation Panel ─────────────────────────────────────────────────────

/**
 * Render the portfolio allocation breakdown.
 */
function renderAllocation(stocks) {
    const container = document.getElementById('allocationList');
    if (!container) return;

    if (!stocks || !stocks.length) {
        container.innerHTML = `<div class="allocation-empty">Add stocks to see allocation breakdown</div>`;
        return;
    }

    const totalValue = stocks.reduce((sum, s) => sum + (Number(s.currentValue) || 0), 0);

    if (totalValue === 0) {
        container.innerHTML = `<div class="allocation-empty">Prices loading...</div>`;
        return;
    }

    // Sort by value descending
    const sorted = [...stocks].sort((a, b) =>
        (Number(b.currentValue) || 0) - (Number(a.currentValue) || 0)
    );

    // Colour palette for bars
    const colors = ['#00ff88', '#3d8bff', '#ffb340', '#ff3d55', '#a855f7', '#06b6d4'];

    container.innerHTML = sorted.map((stock, i) => {
        const pct = totalValue > 0 ? ((Number(stock.currentValue) || 0) / totalValue * 100) : 0;
        const color = colors[i % colors.length];
        return `
            <div class="allocation-item">
                <div class="allocation-header">
                    <span class="allocation-ticker">${escapeHTML(stock.symbol)}</span>
                    <span class="allocation-pct">${pct.toFixed(1)}%</span>
                </div>
                <div class="allocation-bar-track">
                    <div class="allocation-bar-fill" style="width:${pct}%;background:${color}"></div>
                </div>
            </div>`;
    }).join('');
}
