let tempStock = {};

if (window.location.pathname.includes("stocks.html")) {
    const username = localStorage.getItem("username") || "User";
    const portfolio = localStorage.getItem("portfolio") || "Portfolio";

    document.getElementById("usernameDisplay").innerText = username;
    document.getElementById("portfolioDisplay").innerText = portfolio;
}

// Add purchase to table
function addStock() {
    const portfolio = localStorage.getItem("portfolio");

    let allStocks = JSON.parse(localStorage.getItem("stocks")) || {};
    let portfolioStocks = allStocks[portfolio] || [];

    // Use the confirmed values
    portfolioStocks.push(tempStock);

    allStocks[portfolio] = portfolioStocks;
    localStorage.setItem("stocks", JSON.stringify(allStocks));

    // Add visually to table
    addStockRow(
        tempStock.stockName,
        tempStock.purchasePrice,
        tempStock.quantity,
        tempStock.purchaseDate
    );

    // Reset temp storage
    tempStock = {};

    // Return to stock list
    showSection("stockListSection");
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
function showSection(sectionId) {
    document.querySelectorAll(".section").forEach(s => s.classList.remove("active"));
    document.querySelectorAll(".sidebar-tab").forEach(t => t.classList.remove("active"));

    document.getElementById(sectionId).classList.add("active");

    // Highlight sidebar tab if applicable
    if (sectionId === "homeSection") {
        document.querySelector(".sidebar-tab:nth-child(1)").classList.add("active");
    }
    if (sectionId === "stockListSection") {
        document.querySelector(".sidebar-tab:nth-child(2)").classList.add("active");
    }
}

function confirmStock() {
    const name = document.getElementById("stockName").value;
    const price = document.getElementById("purchasePrice").value;
    const quantity = document.getElementById("quantity").value;
    const date = document.getElementById("purchaseDate").value;

    if (!name || !price || !quantity || !date) {
        alert("Please fill in all fields");
        return;
    }

    // Store temporarily
    tempStock = {
        stockName: name,
        purchasePrice: parseFloat(price),
        quantity: parseInt(quantity),
        purchaseDate: date
    };

    document.getElementById("confirmName").innerText = name;
    document.getElementById("confirmPrice").innerText = price;
    document.getElementById("confirmQuantity").innerText = quantity;
    document.getElementById("confirmDate").innerText = date;

    showSection("confirmSection");
}
