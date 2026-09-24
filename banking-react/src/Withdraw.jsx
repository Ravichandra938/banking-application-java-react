import { useState } from "react";
import "./Withdraw.css";

function Withdraw({ onBack, onSuccess }) {

  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const handleWithdraw = async (event) => {

    event.preventDefault();

    setMessage("Processing withdrawal...");

    try {

      const formData = new URLSearchParams();

      formData.append("amount", amount);

      const response = await fetch(
        "http://localhost:8080/banking-backend/withdraw",
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

      console.log("Withdraw response:", text);

      if (response.ok) {

    setMessage("Withdrawal successful!");

    setAmount("");

    setTimeout(() => {
        if (onSuccess) {
            onSuccess();
        }
    }, 1000);

} else {

    setMessage("Insufficient balance.");
}
    } catch (error) {

      console.error(error);

      setMessage(
        "Unable to connect to the server."
      );
    }
  };

  return (
    <div className="withdraw-page">

      <div className="withdraw-box">

        <h1>Withdraw Money</h1>

        <p>
          Enter the amount you want to withdraw.
        </p>

        <form onSubmit={handleWithdraw}>

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
            Withdraw Money
          </button>

        </form>

        {message && (
          <p className="withdraw-message">
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

export default Withdraw;