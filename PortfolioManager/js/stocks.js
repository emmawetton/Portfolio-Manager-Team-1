/**
 * stocks.js — Stock CRUD, holdings table, and symbol autocomplete.
 */

// ─── Holdings Table ────────────────────────────────────────────
function renderHoldingsTable(stocks, portfolioId) {
    const tbody = document.getElementById('holdingsBody');
    const badge = document.getElementById('holdingsBadge');
    if (badge) badge.textContent = stocks ? stocks.length : 0;

    if (!stocks || !stocks.length) {
        tbody.innerHTML = `<tr><td colspan="8"><div class="table-empty">
            <span class="empty-icon">📭</span>
            <span>No holdings yet. Add your first stock above.</span>
        </div></td></tr>`;
        return;
    }

    tbody.innerHTML = stocks.map(s => buildRow(s, portfolioId)).join('');
}

function buildRow(s, pid) {
    const pc = signClass(s.profitLoss);
    return `
    <tr class="holding-row"
        onclick="openStockChart(${pid},${s.id},'${escapeHTML(s.symbol)}','${escapeHTML(s.name)}')"
        onmouseenter="highlightAllocationSegment('${escapeHTML(s.symbol)}')"
        onmouseleave="clearAllocationHighlight()">
        <td>
            <div class="stock-cell">
                <span class="stock-name-cell">${escapeHTML(s.name)}</span>
                <span class="stock-ticker">${escapeHTML(s.symbol)}</span>
            </div>
        </td>
        <td class="num-col"><span class="mono-cell">${formatNumber(s.quantity)}</span></td>
        <td class="num-col hide-mobile"><span class="mono-cell">${formatCurrency(s.purchasePrice)}</span></td>
        <td class="num-col"><span class="mono-cell">${formatCurrency(s.currentPrice)}</span></td>
        <td class="num-col hide-mobile"><span class="mono-cell">${formatCurrency(s.currentValue)}</span></td>
        <td class="num-col hide-mobile">
            <div class="pnl-cell">
                <span class="pnl-amount ${pc}">${formatCurrency(s.profitLoss)}</span>
            </div>
        </td>
        <td class="num-col">
            <span class="pnl-pct ${pc}">${formatPercent(s.profitLossPercentage)}</span>
        </td>
        <td onclick="event.stopPropagation()">
            <div class="row-actions">
                <button class="btn-icon btn-icon-danger" title="Remove stock"
                    onclick="confirmDeleteStock(${pid},${s.id},'${escapeHTML(s.symbol)}')"
                    aria-label="Remove ${escapeHTML(s.symbol)}">
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                        <path d="M1 2.5h10M4.5 2.5V1.5h3v1M2 2.5l.75 8h6.5L10 2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </div>
        </td>
    </tr>`;
}

// ─── Add Stock ─────────────────────────────────────────────────
async function addStock() {
    const symbol        = document.getElementById('stockSymbol').value.trim().toUpperCase();
    const quantity      = parseFloat(document.getElementById('stockQty').value);
    const purchasePrice = parseFloat(document.getElementById('stockPrice').value);
    const purchaseDate  = document.getElementById('stockDate').value;

    if (!symbol)                          { showToast('Please enter a ticker symbol', 'error'); return; }
    if (isNaN(quantity) || quantity <= 0) { showToast('Quantity must be greater than zero', 'error'); return; }
    if (isNaN(purchasePrice) || purchasePrice <= 0) { showToast('Purchase price must be greater than zero', 'error'); return; }
    if (!purchaseDate)                    { showToast('Please select a purchase date', 'error'); return; }
    if (!_selectedId)                     { showToast('No portfolio selected', 'error'); return; }

    setButtonLoading('addStockBtn', true, 'Adding…');
    try {
        await addStockAPI(_selectedId, symbol, quantity, purchasePrice, purchaseDate);

        document.getElementById('stockSymbol').value = '';
        document.getElementById('stockQty').value    = '';
        document.getElementById('stockPrice').value  = '';
        document.getElementById('stockDate').value   = todayString();

        showToast(`${symbol} added to portfolio`);
        await selectPortfolio(_selectedId);
    } catch (e) {
        showToast(e.message || 'Could not add stock', 'error');
    } finally {
        setButtonLoading('addStockBtn', false, '', 'Add Stock');
    }
}

// ─── Open stock chart fullscreen ──────────────────────────────
async function openStockChart(portfolioId, stockId, symbol, name) {
    highlightAllocationSegment(symbol);
    await loadStockChart(portfolioId, stockId, symbol, name, _activePeriod || '6m');
    expandChart();
}

// ─── Delete Stock ──────────────────────────────────────────────
function confirmDeleteStock(portfolioId, stockId, symbol) {
    showModal('Remove Stock',
        `Remove ${symbol} from this portfolio? This cannot be undone.`,
        async () => {
            try {
                await deleteStock(portfolioId, stockId);
                showToast(`${symbol} removed`);
                clearChart();
                await selectPortfolio(portfolioId);
            } catch (e) { showToast(e.message, 'error'); }
        }, 'Remove');
}

// ─── Symbol Autocomplete ───────────────────────────────────────
let _dropdownIdx = -1;

function initSymbolAutocomplete() {
    const input    = document.getElementById('stockSymbol');
    const dropdown = document.getElementById('symbolDropdown');
    if (!input || !dropdown) return;

    input.addEventListener('input', () => {
        const q = input.value.trim().toUpperCase();
        if (q.length < 1) { hideSymbolDropdown(); return; }

        const matches = STOCK_SYMBOLS.filter(s =>
            s.symbol.startsWith(q) || s.name.toUpperCase().includes(q)
        ).slice(0, 8);

        if (!matches.length) { hideSymbolDropdown(); return; }

        _dropdownIdx    = -1;
        dropdown.innerHTML = matches.map((s, i) => `
            <div class="symbol-option" data-symbol="${s.symbol}" data-idx="${i}">
                <span class="symbol-opt-ticker">${s.symbol}</span>
                <span class="symbol-opt-name">${escapeHTML(s.name)}</span>
            </div>`).join('');

        dropdown.querySelectorAll('.symbol-option').forEach(el => {
            el.addEventListener('mousedown', e => {
                e.preventDefault();
                input.value = el.dataset.symbol;
                hideSymbolDropdown();
            });
        });

        dropdown.style.display = 'block';
    });

    input.addEventListener('keydown', e => {
        const options = dropdown.querySelectorAll('.symbol-option');
        if (!options.length || dropdown.style.display === 'none') return;

        if (e.key === 'ArrowDown') {
            e.preventDefault();
            _dropdownIdx = Math.min(_dropdownIdx + 1, options.length - 1);
            _updateDropdownHighlight(options);
        } else if (e.key === 'ArrowUp') {
            e.preventDefault();
            _dropdownIdx = Math.max(_dropdownIdx - 1, -1);
            _updateDropdownHighlight(options);
        } else if (e.key === 'Enter' && _dropdownIdx >= 0) {
            e.preventDefault();
            input.value = options[_dropdownIdx].dataset.symbol;
            hideSymbolDropdown();
        } else if (e.key === 'Escape') {
            hideSymbolDropdown();
        }
    });

    input.addEventListener('blur', () => setTimeout(hideSymbolDropdown, 150));
}

function _updateDropdownHighlight(options) {
    options.forEach((el, i) => el.classList.toggle('active', i === _dropdownIdx));
}

function hideSymbolDropdown() {
    const dropdown = document.getElementById('symbolDropdown');
    if (dropdown) dropdown.style.display = 'none';
    _dropdownIdx = -1;
}
