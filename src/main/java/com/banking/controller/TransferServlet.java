package com.banking.controller;

import com.banking.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        response.setContentType("text/html");

        // Check login
        if (session == null ||
            session.getAttribute("userId") == null) {

            response.getWriter().println(
                    "<h2>Session expired or user is not logged in.</h2>"
            );

            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        String receiverAccount =
                request.getParameter("receiverAccount");

        String amountText =
                request.getParameter("amount");

        try {

            BigDecimal amount =
                    new BigDecimal(amountText);

            // Validate amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                response.getWriter().println(
                        "<h2>Invalid amount</h2>"
                );

                return;
            }

            try (Connection connection =
                         DBConnection.getConnection()) {

                connection.setAutoCommit(false);

                try {

                    // Get sender account
                    String senderSql = """
                            SELECT account_number, balance
                            FROM accounts
                            WHERE user_id = ?
                            """;

                    String senderAccount;
                    BigDecimal senderBalance;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(senderSql)) {

                        statement.setInt(1, userId);

                        ResultSet resultSet =
                                statement.executeQuery();

                        if (!resultSet.next()) {

                            throw new Exception(
                                    "Sender account not found."
                            );
                        }

                        senderAccount =
                                resultSet.getString("account_number");

                        senderBalance =
                                resultSet.getBigDecimal("balance");
                    }

                    // Don't transfer to same account
                    if (senderAccount.equals(receiverAccount)) {

                        response.getWriter().println(
                                "<h2>Invalid Transfer</h2>"
                        );

                        response.getWriter().println(
                                "<p>You cannot transfer money "
                                        + "to your own account.</p>"
                        );

                        connection.rollback();

                        return;
                    }

                    // Check sender balance
                    if (senderBalance.compareTo(amount) < 0) {
                        
                        response.setStatus(
                             HttpServletResponse.SC_BAD_REQUEST
                        );
                        response.getWriter().println(
                                "<h2>Insufficient Balance</h2>"
                        );

                        response.getWriter().println(
                                "<p>Available Balance: ₹"
                                        + senderBalance
                                        + "</p>"
                        );

                        connection.rollback();

                        return;
                    }

                    // Get receiver account
                    String receiverSql = """
                            SELECT balance
                            FROM accounts
                            WHERE account_number = ?
                            """;

                    BigDecimal receiverBalance;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(receiverSql)) {

                        statement.setString(1, receiverAccount);

                        ResultSet resultSet =
                                statement.executeQuery();

                        if (!resultSet.next()) {

                          response.setStatus(
                            HttpServletResponse.SC_BAD_REQUEST
                         );

                          response.getWriter().println(
                                        "<h2>Receiver Account Not Found</h2>"
                        );

                         connection.rollback();

                         return;
                    }

                        receiverBalance =
                                resultSet.getBigDecimal("balance");
                    }

                    // Calculate new balances
                    BigDecimal newSenderBalance =
                            senderBalance.subtract(amount);

                    BigDecimal newReceiverBalance =
                            receiverBalance.add(amount);

                    // Update sender
                    String updateSenderSql = """
                            UPDATE accounts
                            SET balance = ?
                            WHERE account_number = ?
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         updateSenderSql)) {

                        statement.setBigDecimal(
                                1,
                                newSenderBalance
                        );

                        statement.setString(
                                2,
                                senderAccount
                        );

                        statement.executeUpdate();
                    }

                    // Update receiver
                    String updateReceiverSql = """
                            UPDATE accounts
                            SET balance = ?
                            WHERE account_number = ?
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         updateReceiverSql)) {

                        statement.setBigDecimal(
                                1,
                                newReceiverBalance
                        );

                        statement.setString(
                                2,
                                receiverAccount
                        );

                        statement.executeUpdate();
                    }

                    // Sender transaction
                    String transactionSql = """
                            INSERT INTO transactions
                            (account_number, type, amount, balance_after)
                            VALUES (?, ?, ?, ?)
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         transactionSql)) {

                        statement.setString(
                                1,
                                senderAccount
                        );

                        statement.setString(
                                2,
                                "TRANSFER_OUT"
                        );

                        statement.setBigDecimal(
                                3,
                                amount
                        );

                        statement.setBigDecimal(
                                4,
                                newSenderBalance
                        );

                        statement.executeUpdate();
                    }

                    // Receiver transaction
                    try (PreparedStatement statement =
                                 connection.prepareStatement(
                                         transactionSql)) {

                        statement.setString(
                                1,
                                receiverAccount
                        );

                        statement.setString(
                                2,
                                "TRANSFER_IN"
                        );

                        statement.setBigDecimal(
                                3,
                                amount
                        );

                        statement.setBigDecimal(
                                4,
                                newReceiverBalance
                        );

                        statement.executeUpdate();
                    }

                    // Save everything
                    connection.commit();

                    response.getWriter().println(
                            "<h2>Transfer Successful!</h2>"
                    );

                    response.getWriter().println(
                            "<p>Transferred Amount: ₹"
                                    + amount
                                    + "</p>"
                    );

                    response.getWriter().println(
                            "<p>To Account: "
                                    + receiverAccount
                                    + "</p>"
                    );

                    response.getWriter().println(
                            "<p>Your New Balance: ₹"
                                    + newSenderBalance
                                    + "</p>"
                    );

                    response.getWriter().println(
                            "<br><a href='dashboard'>"
                                    + "Back to Dashboard"
                                    + "</a>"
                    );

                } catch (Exception e) {

                    connection.rollback();

                    throw e;
                }
            }

        } catch (NumberFormatException e) {

            response.getWriter().println(
                    "<h2>Invalid amount</h2>"
            );

        } catch (Exception e) {

            response.getWriter().println(
                    "<h2>Transfer Failed</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}