import { useEffect, useState } from "react";
import "./Transactions.css";

function Transactions({ onBack }) {

  const [transactions, setTransactions] = useState([]);
  const [message, setMessage] = useState("Loading transactions...");

  useEffect(() => {

    const loadTransactions = async () => {

      try {

        const response = await fetch(
          "http://localhost:8080/banking-backend/transactions",
          {
            method: "GET",
            credentials: "include"
          }
        );

        const data = await response.json();

        if (data.success) {

          setTransactions(data.transactions);
          setMessage("");

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

    loadTransactions();

  }, []);

  return (
    <div className="transactions-page">

      <div className="transactions-box">

        <h1>Transaction History</h1>

        {message && (
          <p className="transactions-message">
            {message}
          </p>
        )}

        {!message && transactions.length === 0 && (
          <p className="transactions-message">
            No transactions found.
          </p>
        )}

        {!message && transactions.length > 0 && (

          <div className="table-container">

            <table>

              <thead>
                <tr>
                  <th>Date</th>
                  <th>Type</th>
                  <th>Amount</th>
                  <th>Balance After</th>
                </tr>
              </thead>

              <tbody>

                {transactions.map((transaction, index) => (

                  <tr key={index}>

                    <td>
                      {transaction.transactionDate}
                    </td>

                    <td>
                      {transaction.type}
                    </td>

                    <td>
                      ₹{transaction.amount}
                    </td>

                    <td>
                      ₹{transaction.balanceAfter}
                    </td>

                  </tr>

                ))}

              </tbody>

            </table>

          </div>

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

export default Transactions;