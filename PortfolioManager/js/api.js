/**
 * api.js
 * Centralised API layer — all HTTP calls to the Spring Boot backend live here.
 * Every function returns a Promise that resolves with parsed JSON data
 * or rejects with a readable error message.
 */

const API_BASE = 'http://localhost:8080/api';

/**
 * Core HTTP request helper.
 * Handles JSON serialisation, error parsing and response conversion.
 */
async function request(method, path, body = null) {
    const options = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };

    if (body !== null) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(API_BASE + path, options);

    // 204 No Content (e.g. DELETE) — return null
    if (response.status === 204) return null;

    const data = await response.json();

    // If the backend returned an error response, throw it
    if (!response.ok) {
        throw new Error(data.message || `Request failed with status ${response.status}`);
    }

    return data;
}

// ─── Portfolio API ────────────────────────────────────────────────────────

/** Fetch all portfolios */
const getAllPortfolios = () => request('GET', '/portfolios');

/** Fetch one portfolio by ID (includes stocks with live prices) */
const getPortfolioById = (id) => request('GET', `/portfolios/${id}`);

/** Create a new portfolio */
const createPortfolioAPI = (name, description) =>
    request('POST', '/portfolios', { name, description });

/** Update portfolio name and description */
const updatePortfolio = (id, name, description) =>
    request('PUT', `/portfolios/${id}`, { name, description });

/** Delete a portfolio and all its stocks */
const deletePortfolio = (id) => request('DELETE', `/portfolios/${id}`);

// ─── Stock API ────────────────────────────────────────────────────────────

/** Fetch all stocks in a portfolio */
const getAllStocks = (portfolioId) =>
    request('GET', `/portfolios/${portfolioId}/stocks`);

/** Fetch one stock by ID */
const getStockById = (portfolioId, stockId) =>
    request('GET', `/portfolios/${portfolioId}/stocks/${stockId}`);

/** Add a stock to a portfolio */
const addStockAPI = (portfolioId, symbol, quantity, purchasePrice, purchaseDate) =>
    request('POST', `/portfolios/${portfolioId}/stocks`, {
        symbol,
        quantity,
        purchasePrice,
        purchaseDate
    });


/** Update a stock's quantity and purchase price */
const updateStock = (portfolioId, stockId, quantity, purchasePrice) =>
    request('PUT', `/portfolios/${portfolioId}/stocks/${stockId}`, {
        quantity,
        purchasePrice
    });

/** Remove a stock from a portfolio */
const deleteStock = (portfolioId, stockId) =>
    request('DELETE', `/portfolios/${portfolioId}/stocks/${stockId}`);

/** Fetch historical price trend data for a stock */
const getStockTrends = (portfolioId, stockId, months = 6) =>
    request('GET', `/portfolios/${portfolioId}/stocks/${stockId}/trends?months=${months}`);

/** Check if the backend is reachable */
async function checkHealth() {
    try {
        await fetch(API_BASE + '/portfolios', { method: 'GET' });
        return true;
    } catch {
        return false;
    }
}
