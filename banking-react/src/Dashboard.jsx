import { useEffect, useState } from "react";
import Deposit from "./Deposit";
import Withdraw from "./Withdraw";
import Transfer from "./Transfer";
import Transactions from "./Transactions";
import "./Dashboard.css";

function Dashboard({ onLogout }) {

  const [user, setUser] = useState(null);
  const [message, setMessage] = useState("");

  const [showDeposit, setShowDeposit] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false);
  const [showTransfer, setShowTransfer] = useState(false);
  const [showTransactions, setShowTransactions] = useState(false);

  const loadDashboard = async () => {

    try {

      const response = await fetch(
        "http://localhost:8080/banking-backend/dashboard-data",
        {
          method: "GET",
          credentials: "include"
        }
      );

      const data = await response.json();

      if (data.success) {

        setUser(data);
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

  useEffect(() => {
    loadDashboard();
  }, []);

  const handleLogout = async () => {

    try {

      const response = await fetch(
        "http://localhost:8080/banking-backend/logout",
        {
          method: "POST",
          credentials: "include"
        }
      );

      const data = await response.json();

      if (data.success) {
        onLogout();
      }

    } catch (error) {

      console.error(error);
      onLogout();
    }
  };

  if (showDeposit) {

    return (
      <Deposit
        onBack={() => setShowDeposit(false)}
        onSuccess={async () => {
          await loadDashboard();
          setShowDeposit(false);
        }}
      />
    );
  }

  if (showWithdraw) {

    return (
      <Withdraw
        onBack={() => setShowWithdraw(false)}
        onSuccess={async () => {
          await loadDashboard();
          setShowWithdraw(false);
        }}
      />
    );
  }

  if (showTransfer) {

    return (
      <Transfer
        onBack={() => setShowTransfer(false)}
        onSuccess={async () => {
          await loadDashboard();
          setShowTransfer(false);
        }}
      />
    );
  }

  if (showTransactions) {

    return (
      <Transactions
        onBack={() => setShowTransactions(false)}
      />
    );
  }

  return (
    <div className="dashboard">

      <header className="dashboard-header">

        <div>
          <h1>Banking Application</h1>
          <p>Secure Online Banking</p>
        </div>

        <button
          className="logout-button"
          onClick={handleLogout}
        >
          Logout
        </button>

      </header>

      <main className="dashboard-content">

        {user && (
          <>
            <div className="welcome-section">

              <h2>
                Welcome, {user.name} 👋
              </h2>

              <p>
                Manage your account and transactions
                from one place.
              </p>

            </div>

            <div className="account-summary">

              <div className="account-card">

                <span className="card-icon">
                  🏦
                </span>

                <div>
                  <p className="card-label">
                    Account Number
                  </p>

                  <h3>
                    {user.accountNumber}
                  </h3>
                </div>

              </div>

              <div className="account-card balance-card">

                <span className="card-icon">
                  💰
                </span>

                <div>
                  <p className="card-label">
                    Available Balance
                  </p>

                  <h3>
                    ₹{user.balance}
                  </h3>
                </div>

              </div>

            </div>

            <h2 className="services-title">
              Banking Services
            </h2>

            <div className="services">

              <button
                className="service-card"
                onClick={() => setShowDeposit(true)}
              >
                <span>📥</span>
                <h3>Deposit</h3>
                <p>
                  Add money to your account
                </p>
              </button>

              <button
                className="service-card"
                onClick={() => setShowWithdraw(true)}
              >
                <span>📤</span>
                <h3>Withdraw</h3>
                <p>
                  Withdraw money from your account
                </p>
              </button>

              <button
                className="service-card"
                onClick={() => setShowTransfer(true)}
              >
                <span>💸</span>
                <h3>Transfer</h3>
                <p>
                  Transfer money to another account
                </p>
              </button>

              <button
                className="service-card"
                onClick={() => setShowTransactions(true)}
              >
                <span>📋</span>
                <h3>Transactions</h3>
                <p>
                  View your transaction history
                </p>
              </button>

            </div>

          </>
        )}

        {message && (
          <p className="dashboard-message">
            {message}
          </p>
        )}

      </main>

    </div>
  );
}

export default Dashboard;