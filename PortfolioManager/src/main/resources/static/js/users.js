localStorage.clear();

function createUser() {
  const username = document.getElementById("username").value;
  const email = document.getElementById("email").value;

  if (!username || !email) return alert("Missing name/email");

  localStorage.setItem("username", username);
  localStorage.setItem("email",email);
  window.location.href = "create-portfolio.html";
  // document.getElementById("result").innerHTML =
  //   `User created: ${username}`;

  // Redirect to portfolio creation
  // setTimeout(() => {
  //   window.location.href = `create-portfolio.html?userId=${username}`;
  // }, 1500);
}