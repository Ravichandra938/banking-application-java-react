import { useState } from "react";
import "./Register.css";

function Register({ onBack }) {

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [message, setMessage] = useState("");

  const handleRegister = async (event) => {

    event.preventDefault();

    setMessage("Creating account...");

    try {

      const formData = new URLSearchParams();

      formData.append("name", name);
      formData.append("email", email);
      formData.append("username", username);
      formData.append("password", password);

      const response = await fetch(
        "http://localhost:8080/banking-backend/register",
        {
          method: "POST",

          headers: {
            "Content-Type":
              "application/x-www-form-urlencoded"
          },

          body: formData
        }
      );

      const text = await response.text();

      console.log("Register response:", text);

      if (response.ok) {

        setMessage(
          "Registration successful! You can now login."
        );

        setName("");
        setEmail("");
        setUsername("");
        setPassword("");

      } else {

        setMessage("Registration failed.");
      }

    } catch (error) {

      console.error(error);

      setMessage(
        "Unable to connect to the server."
      );
    }
  };

  return (
    <div className="register-page">

      <div className="register-box">

        <h1>Create Account</h1>

        <p>
          Register for a new bank account
        </p>

        <form onSubmit={handleRegister}>

          <label>Full Name</label>

          <input
            type="text"
            placeholder="Enter your name"
            value={name}
            onChange={(event) =>
              setName(event.target.value)
            }
            required
          />

          <label>Email</label>

          <input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
            required
          />

          <label>Username</label>

          <input
            type="text"
            placeholder="Choose a username"
            value={username}
            onChange={(event) =>
              setUsername(event.target.value)
            }
            required
          />

          <label>Password</label>

          <input
            type="password"
            placeholder="Create a password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            required
          />

          <button type="submit">
            Register
          </button>

        </form>

        {message && (
          <p className="register-message">
            {message}
          </p>
        )}

        <button
          type="button"
          className="back-button"
          onClick={onBack}
        >
          Back to Login
        </button>

      </div>

    </div>
  );
}

export default Register;