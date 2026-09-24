import { useState } from "react";
import "./Deposit.css";

function Deposit({ onBack, onSuccess }) {

  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const handleDeposit = async (event) => {

    event.preventDefault();

    setMessage("Processing deposit...");

    try {

      const formData = new URLSearchParams();

      formData.append("amount", amount);

      const response = await fetch(
        "http://localhost:8080/banking-backend/deposit",
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

      const text = await response.text();

      console.log("Deposit response:", text);

      if (response.ok) {

        setMessage("Deposit successful!");

        setAmount("");

        setTimeout(() => {
          if (onSuccess) {
            onSuccess();
          }
        }, 1000);

      } else {

        setMessage("Deposit failed.");

        console.log(text);
      }

    } catch (error) {

      console.error(error);

      setMessage(
        "Unable to connect to the server."
      );
    }
  };

  return (
    <div className="deposit-page">

      <div className="deposit-box">

        <h1>Deposit Money</h1>

        <p>
          Enter the amount you want to deposit.
        </p>

        <form onSubmit={handleDeposit}>

          <label>Amount</label>

          <input
            type="number"
            step="0.01"
            min="0.01"
            placeholder="Enter amount"
            value={amount}
            onChange={(event) =>
              setAmount(event.target.value)
            }
            required
          />

          <button type="submit">
            Deposit Money
          </button>

        </form>

        {message && (
          <p className="deposit-message">
            {message}
          </p>
        )}

        <button
          type="button"
          className="back-button"
          onClick={onBack}
        >
          Back to Dashboard
        </button>

      </div>

    </div>
  );
}

export default Deposit;