/**
 * manage.js — Portfolio & stock management modal.
 */

function openManageModal() {
    if (!_selectedData) return;

    document.getElementById('editPortfolioName').value = _selectedData.name || '';
    document.getElementById('editPortfolioDesc').value = _selectedData.description || '';

    renderManageHoldings(_selectedData.stocks || [], _selectedId);
    document.getElementById('manageModal').classList.add('open');
}

function closeManageModal() {
    document.getElementById('manageModal').classList.remove('open');
}

async function savePortfolioSettings() {
    const name = document.getElementById('editPortfolioName').value.trim();
    const desc = document.getElementById('editPortfolioDesc').value.trim();
    if (!name) { showToast('Portfolio name is required', 'error'); return; }

    const btn = document.getElementById('savePortfolioBtn');
    if (btn) { btn.disabled = true; btn.textContent = 'Saving…'; }

    try {
        await updatePortfolio(_selectedId, name, desc);
        showToast('Portfolio updated');
        await selectPortfolio(_selectedId);
        document.getElementById('editPortfolioName').value = _selectedData?.name || name;
    } catch (e) {
        showToast(e.message, 'error');
    } finally {
        if (btn) { btn.disabled = false; btn.textContent = 'Save Changes'; }
    }
}

// ─── Holdings management table ─────────────────────────────────
function renderManageHoldings(stocks, portfolioId) {
    const tbody = document.getElementById('manageHoldingsBody');
    const badge = document.getElementById('manageHoldingsBadge');
    if (!tbody) return;
    if (badge) badge.textContent = stocks.length;

    if (!stocks.length) {
        tbody.innerHTML = `<tr><td colspan="5" class="manage-empty">No holdings in this portfolio.</td></tr>`;
        return;
    }
    tbody.innerHTML = stocks.map(s => buildManageRow(s, portfolioId)).join('');
}

function buildManageRow(s, portfolioId) {
    return `
    <tr id="mrow-${s.id}">
        <td>
            <div class="stock-cell">
                <span class="stock-name-cell">${escapeHTML(s.name)}</span>
                <span class="stock-ticker">${escapeHTML(s.symbol)}</span>
            </div>
        </td>
        <td class="num-col">
            <span class="mono-cell" id="mrow-qty-${s.id}">${formatNumber(s.quantity)}</span>
        </td>
        <td class="num-col">
            <span class="mono-cell" id="mrow-price-${s.id}">${formatCurrency(s.purchasePrice)}</span>
        </td>
        <td class="num-col hide-mobile">
            <span class="pnl-pct ${signClass(s.profitLoss)}">${formatPercent(s.profitLossPercentage)}</span>
        </td>
        <td>
            <div class="row-actions" id="mrow-actions-${s.id}">
                <button class="btn-icon btn-icon-accent" title="Edit"
                    onclick="startEditStock(${portfolioId},${s.id},${s.quantity},${s.purchasePrice})">
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                        <path d="M8.5 1.5l2 2L3.5 11H1V8.5l7.5-7z" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
                <button class="btn-icon btn-icon-danger" title="Delete"
                    onclick="deleteStockFromManage(${portfolioId},${s.id},'${escapeHTML(s.symbol)}')">
                    <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                        <path d="M1 2.5h10M4.5 2.5V1.5h3v1M2 2.5l.75 8h6.5L10 2.5" stroke="currentColor" stroke-width="1.2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </button>
            </div>
        </td>
    </tr>`;
}

// ─── Inline row editing ────────────────────────────────────────
function startEditStock(portfolioId, stockId, qty, price) {
    document.getElementById(`mrow-qty-${stockId}`).innerHTML =
        `<input class="field-input manage-inline-input" id="medit-qty-${stockId}" type="number" value="${qty}" min="0.01" step="0.01">`;
    document.getElementById(`mrow-price-${stockId}`).innerHTML =
        `<input class="field-input manage-inline-input" id="medit-price-${stockId}" type="number" value="${price}" min="0.01" step="0.01">`;
    document.getElementById(`mrow-actions-${stockId}`).innerHTML = `
        <button class="btn btn-primary btn-sm" onclick="saveStockEdit(${portfolioId},${stockId})">Save</button>
        <button class="btn btn-ghost btn-sm" onclick="cancelStockEdit(${portfolioId})">Cancel</button>`;
}

async function saveStockEdit(portfolioId, stockId) {
    const qty   = parseFloat(document.getElementById(`medit-qty-${stockId}`).value);
    const price = parseFloat(document.getElementById(`medit-price-${stockId}`).value);
    if (isNaN(qty)   || qty   <= 0) { showToast('Invalid quantity', 'error');       return; }
    if (isNaN(price) || price <= 0) { showToast('Invalid purchase price', 'error'); return; }

    try {
        await updateStock(portfolioId, stockId, qty, price);
        showToast('Stock updated');
        await selectPortfolio(portfolioId);
        renderManageHoldings(_selectedData?.stocks || [], portfolioId);
    } catch (e) {
        showToast(e.message, 'error');
    }
}

function cancelStockEdit(portfolioId) {
    renderManageHoldings(_selectedData?.stocks || [], portfolioId);
}

function deleteStockFromManage(portfolioId, stockId, symbol) {
    showModal('Remove Stock',
        `Remove ${symbol} from this portfolio? This cannot be undone.`,
        async () => {
            try {
                await deleteStock(portfolioId, stockId);
                showToast(`${symbol} removed`);
                clearChart();
                await selectPortfolio(portfolioId);
                renderManageHoldings(_selectedData?.stocks || [], portfolioId);
                const badge = document.getElementById('manageHoldingsBadge');
                if (badge) badge.textContent = _selectedData?.stocks?.length ?? 0;
            } catch (e) { showToast(e.message, 'error'); }
        }, 'Remove');
}
