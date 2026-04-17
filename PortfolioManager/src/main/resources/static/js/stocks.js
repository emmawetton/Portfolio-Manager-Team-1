
if (window.location.pathname.includes("stocks.html")) {
    const portfolio = localStorage.getItem("portfolio");
    document.getElementById("portfolioName").innerText = portfolio || "Unknown Portfolio";

    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    portfolioStocks.forEach(stock => {
        addStockRow(stock.stockName, stock.purchasePrice, stock.quantity, stock.purchaseDate);
    });   
}

// Add purchase to table
function addStock() {
    const stockName = document.getElementById("stockName").value;
    const purchasePrice = document.getElementById("purchasePrice").value;
    const quantity = document.getElementById("quantity").value;
    const purchaseDate = document.getElementById("purchaseDate").value;

    if (!stockName || !purchasePrice || !quantity || !purchaseDate) 
        return alert("Please fill in all fields");
    
    const portfolio = localStorage.getItem("portfolio");
    // Load existing stocks for this portfolio
    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    // Add new stock
    portfolioStocks.push({
        stockName: stockName,
        purchasePrice: parseFloat(purchasePrice),
        quantity: parseInt(quantity),
        purchaseDate: purchaseDate
    });

    // Save back to localStorage
    allStocks[portfolio] = portfolioStocks;
    localStorage.setItem("stocks", JSON.stringify(allStocks));

     // Add to table visually
    addStockRow(stockName, purchasePrice, quantity, purchaseDate);

    document.getElementById("stockName").value = "";
    document.getElementById("purchasePrice").value = "";
    document.getElementById("quantity").value = "";
    document.getElementById("purchaseDate").value = "";

    showTab('managerTab');
}


function goBackToPortfolios() {
    window.location.href = "create-portfolio.html";
}

function deleteStock(stockName) {
    const confirmed = confirm('Are you sure you want to delete "${stockName}"?');
    if (!confirmed) return;

    const portfolio = localStorage.getItem("portfolio");

    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    // Remove the stock
    portfolioStocks = portfolioStocks.filter(s => s.stockName !== stockName);

    // Save updated list
    allStocks[portfolio] = portfolioStocks;
    localStorage.setItem("stocks", JSON.stringify(allStocks));

    // Refresh table
    window.location.reload();
}

function addStockRow(name, price, quantity, date) {
    const table = document.getElementById("stockTable");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td>${name}</td>
        <td>${parseFloat(price).toFixed(2)}</td>
        <td>${quantity}</td>
        <td>${date}</td>
        <td><button class="delete-btn" onclick="deleteStock('${name}')">Delete</button></td>
    `;

    table.appendChild(row);
}

// -------------------------
// TAB SWITCHING
// -------------------------
function showTab(tabId) {
    document.querySelectorAll(".tab-btn").forEach(btn => btn.classList.remove("active"));
    document.querySelectorAll(".tab-content").forEach(tab => tab.classList.remove("active"));

    document.querySelector(`[onclick="showTab('${tabId}')"]`).classList.add("active");
    document.getElementById(tabId).classList.add("active");
}