import { useState } from "react";
import Dashboard from "./Dashboard";
import Register from "./Register";
import "./App.css";

function App() {

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showRegister, setShowRegister] = useState(false);

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState("");

  const handleLogin = async (event) => {

    event.preventDefault();

    setMessage("Logging in...");

    try {

      const formData = new URLSearchParams();

      formData.append("username", username);
      formData.append("password", password);

      const response = await fetch(
        "http://localhost:8080/banking-backend/login",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/x-www-form-urlencoded"
          },

          credentials: "include",

          body: formData
        }
      );

      const data = await response.json();

      if (data.success) {

        setIsLoggedIn(true);

      } else {

        setMessage(data.message);
      }

    } catch (error) {

      console.error(error);

      setMessage(
        "Unable to connect to the server."
      );
    }
  };

  if (showRegister) {
  return (
    <Register
      onBack={() => setShowRegister(false)}
    />
  );
}

  if (isLoggedIn) {
  return (
    <Dashboard
      onLogout={() => setIsLoggedIn(false)}
    />
  );
}

  return (
    <div className="login-container">

      <div className="login-box">

        <h1>Banking Application</h1>

        <p className="subtitle">
          Login to your account
        </p>

        <form onSubmit={handleLogin}>

          <label>Username</label>

          <input
            type="text"
            placeholder="Enter username"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
            required
          />

          <label>Password</label>

          <input
            type="password"
            placeholder="Enter password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            required
          />

          <button type="submit">
            Login
          </button>

        </form>

        {message && (
          <p className="login-message">
            {message}
          </p>
        )}

        <p className="register-text">
  Don't have an account?
  <button
    type="button"
    onClick={() => setShowRegister(true)}
  >
    Register
  </button>
</p>

      </div>

    </div>
  );
}

export default App;