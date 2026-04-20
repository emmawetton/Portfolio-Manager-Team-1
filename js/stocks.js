/**
 * stocks.js
 * Handles all stock-related UI logic:
 * adding, deleting, displaying stocks and rendering the holdings table.
 */

// ─── Holdings Table ───────────────────────────────────────────────────────

/**
 * Render the holdings table for the selected portfolio.
 * @param {Array}  stocks      - Array of StockResponse objects from the API
 * @param {number} portfolioId
 */
function renderHoldingsTable(stocks, portfolioId) {
    const tbody = document.getElementById('holdingsBody');
    const badge = document.getElementById('holdingsBadge');

    if (badge) badge.textContent = stocks ? stocks.length : 0;

    if (!stocks || stocks.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8">
                    <div class="table-empty">
                        <span class="empty-icon">📭</span>
                        <span>No holdings yet. Add your first stock above.</span>
                    </div>
                </td>
            </tr>`;
        return;
    }

    tbody.innerHTML = stocks.map(stock => buildStockRow(stock, portfolioId)).join('');
}

/**
 * Build a single table row HTML string for a stock.
 */
function buildStockRow(stock, portfolioId) {
    const pnlClass = signClass(stock.profitLoss);

    return `
        <tr>
            <td>
                <div class="stock-cell">
                    <span class="stock-name-cell">${escapeHTML(stock.name)}</span>
                    <span class="stock-ticker">${escapeHTML(stock.symbol)}</span>
                </div>
            </td>
            <td class="num-col">
                <span class="mono-cell">${formatNumber(stock.quantity)}</span>
            </td>
            <td class="num-col hide-mobile">
                <span class="mono-cell">${formatCurrency(stock.purchasePrice)}</span>
            </td>
            <td class="num-col">
                <span class="mono-cell">${formatCurrency(stock.currentPrice)}</span>
            </td>
            <td class="num-col hide-mobile">
                <span class="mono-cell">${formatCurrency(stock.currentValue)}</span>
            </td>
            <td class="num-col hide-mobile">
                <div class="pnl-cell">
                    <span class="pnl-amount ${pnlClass}">${formatCurrency(stock.profitLoss)}</span>
                </div>
            </td>
            <td class="num-col">
                <span class="pnl-pct ${pnlClass}">${formatPercent(stock.profitLossPercentage)}</span>
            </td>
            <td>
                <div class="row-actions">
                    <button
                        class="btn-icon btn-icon-accent"
                        title="View price chart"
                        onclick="loadStockChart(${portfolioId}, ${stock.id}, '${escapeHTML(stock.symbol)}', '${escapeHTML(stock.name)}', 6)"
                        aria-label="View chart for ${escapeHTML(stock.symbol)}"
                    >
                        <svg width="13" height="13" viewBox="0 0 13 13" fill="none">
                            <path d="M1 10l3.5-4 2.5 2.5L11 3.5" stroke="currentColor" stroke-width="1.4" stroke-linecap="round" stroke-linejoin="round"/>
                            <path d="M1 12h11" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" opacity=".4"/>
                        </svg>
                    </button>
                    <button
                        class="btn-icon btn-icon-danger"
                        title="Remove stock"
                        onclick="confirmDeleteStock(${portfolioId}, ${stock.id}, '${escapeHTML(stock.symbol)}')"
                        aria-label="Remove ${escapeHTML(stock.symbol)}"
                    >
                        <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                            <path d="M1 2.5h10M4.5 2.5V1.5h3v1M2 2.5l.75 8h6.5L10 2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                        </svg>
                    </button>
                </div>
            </td>
        </tr>`;
}

// ─── Add Stock ────────────────────────────────────────────────────────────

/**
 * Validate form inputs, call the API and refresh the portfolio view.
 * NOTE: calls window.addStock_API to avoid collision with the function name.
 */
async function addStock() {
    const symbol        = document.getElementById('stockSymbol').value.trim().toUpperCase();
    const quantity      = parseFloat(document.getElementById('stockQty').value);
    const purchasePrice = parseFloat(document.getElementById('stockPrice').value);
    const purchaseDate  = document.getElementById('stockDate').value;

    // Validation
    if (!symbol) {
        showToast('Please enter a stock symbol', 'error');
        document.getElementById('stockSymbol').focus();
        return;
    }
    if (isNaN(quantity) || quantity <= 0) {
        showToast('Quantity must be greater than zero', 'error');
        document.getElementById('stockQty').focus();
        return;
    }
    if (isNaN(purchasePrice) || purchasePrice <= 0) {
        showToast('Purchase price must be greater than zero', 'error');
        document.getElementById('stockPrice').focus();
        return;
    }
    if (!purchaseDate) {
        showToast('Please select a purchase date', 'error');
        document.getElementById('stockDate').focus();
        return;
    }

    const portfolioId = _selectedPortfolioId;
    if (!portfolioId) {
        showToast('No portfolio selected', 'error');
        return;
    }

    setButtonLoading('addStockBtn', true, 'Adding…');

    try {
        await window.addStock_API(portfolioId, symbol, quantity, purchasePrice, purchaseDate);

        // Clear inputs
        document.getElementById('stockSymbol').value = '';
        document.getElementById('stockQty').value    = '';
        document.getElementById('stockPrice').value  = '';
        document.getElementById('stockDate').value   = todayString();

        showToast(`${symbol} added to portfolio`);

        // Refresh the full portfolio view
        await selectPortfolio(portfolioId);

    } catch (e) {
        showToast(e.message || 'Could not add stock', 'error');
    } finally {
        setButtonLoading('addStockBtn', false, '', '+ Add Stock');
    }
}

// ─── Delete Stock ─────────────────────────────────────────────────────────

/**
 * Show a confirmation modal then delete the stock.
 */
function confirmDeleteStock(portfolioId, stockId, symbol) {
    showModal(
        'Remove Stock',
        `Remove ${symbol} from this portfolio? The stock and its data will be permanently deleted.`,
        async () => {
            try {
                await deleteStock(portfolioId, stockId);
                showToast(`${symbol} removed`);
                clearChart();
                await selectPortfolio(portfolioId);
            } catch (e) {
                showToast(e.message, 'error');
            }
        },
        'Remove'
    );
}
