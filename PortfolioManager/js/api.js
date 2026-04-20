/**
 * api.js — All HTTP calls to the Spring Boot backend.
 * Named to avoid conflicts: createPortfolioAPI, addStockAPI
 */

const API_BASE = 'http://localhost:8080/api';

async function request(method, path, body = null) {
    const options = { method, headers: { 'Content-Type': 'application/json' } };
    if (body !== null) options.body = JSON.stringify(body);

    const response = await fetch(API_BASE + path, options);
    if (response.status === 204) return null;

    const data = await response.json();
    if (!response.ok) throw new Error(data.message || `Request failed (${response.status})`);
    return data;
}

// ─── Portfolios ───────────────────────────────────────────────
const getAllPortfolios    = ()               => request('GET',    '/portfolios');
const getPortfolioById   = (id)             => request('GET',    `/portfolios/${id}`);
const createPortfolioAPI = (name, desc)     => request('POST',   '/portfolios', { name, description: desc });
const updatePortfolio    = (id, name, desc) => request('PUT',    `/portfolios/${id}`, { name, description: desc });
const deletePortfolio    = (id)             => request('DELETE', `/portfolios/${id}`);

// ─── Stocks ───────────────────────────────────────────────────
const getAllStocks  = (pid)          => request('GET',    `/portfolios/${pid}/stocks`);
const getStockById = (pid, sid)     => request('GET',    `/portfolios/${pid}/stocks/${sid}`);
const addStockAPI  = (pid, symbol, quantity, purchasePrice, purchaseDate) =>
    request('POST', `/portfolios/${pid}/stocks`, { symbol, quantity, purchasePrice, purchaseDate });
const updateStock  = (pid, sid, quantity, purchasePrice) =>
    request('PUT',  `/portfolios/${pid}/stocks/${sid}`, { quantity, purchasePrice });
const deleteStock  = (pid, sid)     => request('DELETE', `/portfolios/${pid}/stocks/${sid}`);

// period: '1d' | '5d' | '1w' | '1m' | '3m' | '6m' | '1y'
const getStockTrends = (pid, sid, period = '6m') =>
    request('GET', `/portfolios/${pid}/stocks/${sid}/trends?period=${encodeURIComponent(period)}`);

// ─── Health ───────────────────────────────────────────────────
async function checkHealth() {
    try { await fetch(API_BASE + '/portfolios'); return true; }
    catch { return false; }
}
