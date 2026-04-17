

if (window.location.pathname.includes("stocks.html")) {
    document.getElementById("portfolioName").innerText =
        localStorage.getItem("portfolio") || "Unknown Portfolio";

   const portfolio = localStorage.getItem("portfolio");
    document.getElementById("portfolioName").innerText = portfolio;

    // Load stocks for this portfolio
    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    portfolioStocks.forEach(stock => {
        addStockRow(stock.stockName, stock.purchasePrice);
    });    
}

// Add purchase to table
function addStock() {
    const stockName = document.getElementById("stockName").value;
    const purchasePrice = document.getElementById("purchasePrice").value;

    if (!stockName || !purchasePrice) return alert("Enter stock name and purchase price");
    
    const portfolio = localStorage.getItem("portfolio");
    // Load existing stocks for this portfolio
    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    // Add new stock
    portfolioStocks.push({
        stockName: stockName,
        purchasePrice: parseFloat(purchasePrice)
    });

    // Save back to localStorage
    allStocks[portfolio] = portfolioStocks;
    localStorage.setItem("stocks", JSON.stringify(allStocks));

     // Add to table visually
    addStockRow(stockName, purchasePrice);

    document.getElementById("stockName").value = "";
    document.getElementById("purchasePrice").value = "";
}




function goBackToPortfolios() {
    window.location.href = "create-portfolio.html";
}

function deleteStock(name) {
    const portfolio = localStorage.getItem("portfolio");

    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    // Remove the stock
    portfolioStocks = portfolioStocks.filter(s => s.stockName !== name);

    // Save updated list
    allStocks[portfolio] = portfolioStocks;
    localStorage.setItem("stocks", JSON.stringify(allStocks));

    // Refresh table
    window.location.reload();
}

function addStockRow(name, price) {
    const table = document.getElementById("stockTable");
    const row = document.createElement("tr");

    row.innerHTML = `
        <td>${name}</td>
        <td>${parseFloat(price).toFixed(2)}</td>
        <td><button onclick="deleteStock('${name}')">Delete</button></td>
    `;

    table.appendChild(row);
}