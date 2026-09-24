import { useState } from "react";
import "./Transfer.css";

function Transfer({ onBack, onSuccess }) {

  const [receiverAccount, setReceiverAccount] = useState("");
  const [amount, setAmount] = useState("");
  const [message, setMessage] = useState("");

  const handleTransfer = async (event) => {

    event.preventDefault();

    setMessage("Processing transfer...");

    try {

      const formData = new URLSearchParams();

      formData.append("receiverAccount", receiverAccount);
      formData.append("amount", amount);

      const response = await fetch(
        "http://localhost:8080/banking-backend/transfer",
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

      console.log("Transfer response:", text);

      if (response.ok) {

        setMessage("Transfer successful!");

        setReceiverAccount("");
        setAmount("");

        setTimeout(() => {
          if (onSuccess) {
            onSuccess();
          }
        }, 1000);

      } else {

    if (text.includes("Receiver Account Not Found")) {
        setMessage("Receiver account not found.");
    } else if (text.includes("Insufficient Balance")) {
        setMessage("Insufficient balance.");
    } else {
        setMessage("Transfer failed.");
    }

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
    <div className="transfer-page">

      <div className="transfer-box">

        <h1>Transfer Money</h1>

        <p>
          Transfer money to another account.
        </p>

        <form onSubmit={handleTransfer}>

          <label>Receiver Account Number</label>

          <input
            type="text"
            placeholder="Enter receiver account number"
            value={receiverAccount}
            onChange={(event) =>
              setReceiverAccount(event.target.value)
            }
            required
          />

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
            Transfer Money
          </button>

        </form>

        {message && (
          <p className="transfer-message">
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

export default Transfer;