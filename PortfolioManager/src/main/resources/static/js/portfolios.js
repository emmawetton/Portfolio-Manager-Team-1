
if (window.location.pathname.includes("create-portfolio.html")) {
    const username = localStorage.getItem("username");
    //replace with something like below
    //const username = fetch("/api/users/username")
    document.getElementById("usernameDisplay").innerText = username || "Guest";

    //Load portfolios from localStorage (mock)
    const portfolios =JSON.parse(localStorage.getItem("portfolios")) || [];
    const listDiv = document.getElementById("portfolioList");

    if (portfolios.length === 0) {
        listDiv.innerHTML = "<p>No existing portfolios</p>";
    } else {
        let html = "";
        portfolios.forEach((p, index) => {
        html += `
            <div class="portfolio-card">
                <div class="portfolio-info">
                    <h3>${p}</h3>
                </div>
                <div class="portfolio-actions">
                    <button class="open-btn" onclick="openPortfolio('${p}')">Open</button>
                    <button class="delete-btn" onclick="deletePortfolio(${index})">Delete</button>
                </div>
            </div>
        `;
        });
        listDiv.innerHTML = html;
    }
}

function createPortfolio() {
  const portfolioName = document.getElementById("portfolioName").value;
  if (!portfolioName) return alert("Please enter a portfolio name");

  localStorage.setItem("portfolio", portfolioName);
  
  let portfolios = JSON.parse(localStorage.getItem("portfolios")) || [];
  portfolios.push(portfolioName);
  localStorage.setItem("portfolios", JSON.stringify(portfolios));
  
  window.location.href = "stocks.html";
  // const portfolio = await apiPost(`/users/${userId}/portfolios`, {
  //   name
  // });

  // document.getElementById("result").textContent =
  //   `Portfolio created with ID: ${portfolio.id}`;

  // setTimeout(() => {
  //   window.location.href = `stocks.html?portfolioId=${portfolio.id}`;
  // }, 1500);
}

function goBackToUser(){
  window.location.href = "create-user.html";
}

function openPortfolio(name) {
    localStorage.setItem("portfolio", name);
    window.location.href = "stocks.html";
}

function deletePortfolio(index) {
    let portfolios = JSON.parse(localStorage.getItem("portfolios")) || [];
    portfolios.splice(index, 1);
    localStorage.setItem("portfolios", JSON.stringify(portfolios));

    // Refresh the page to update the list
    window.location.reload();
}

