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

@WebServlet("/withdraw")
public class WithdrawServlet extends HttpServlet {

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

        String amountText =
                request.getParameter("amount");

        try {

            BigDecimal amount =
                    new BigDecimal(amountText);

            // Check valid amount
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                response.getWriter().println(
                        "<h2>Invalid amount</h2>"
                );

                response.getWriter().println(
                        "<p>Withdrawal amount must be greater than zero.</p>"
                );

                return;
            }

            try (Connection connection =
                         DBConnection.getConnection()) {

                connection.setAutoCommit(false);

                try {

                    // Get account
                    String accountSql = """
                            SELECT account_number, balance
                            FROM accounts
                            WHERE user_id = ?
                            """;

                    String accountNumber;
                    BigDecimal currentBalance;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(accountSql)) {

                        statement.setInt(1, userId);

                        ResultSet resultSet =
                                statement.executeQuery();

                        if (!resultSet.next()) {

                            throw new Exception(
                                    "Bank account not found."
                            );
                        }

                        accountNumber =
                                resultSet.getString("account_number");

                        currentBalance =
                                resultSet.getBigDecimal("balance");
                    }

                    // Check sufficient balance
                    if (currentBalance.compareTo(amount) < 0) {
                        
                        response.setStatus(
                                HttpServletResponse.SC_BAD_REQUEST
                        );

                        response.getWriter().println(
                                "<h2>Insufficient Balance</h2>"
                        );

                        response.getWriter().println(
                                "<p>Current Balance: ₹"
                                        + currentBalance
                                        + "</p>"
                        );

                        response.getWriter().println(
                                "<p>Requested Amount: ₹"
                                        + amount
                                        + "</p>"
                        );

                        connection.rollback();

                        return;
                    }

                    // Calculate new balance
                    BigDecimal newBalance =
                            currentBalance.subtract(amount);

                    // Update account balance
                    String updateSql = """
                            UPDATE accounts
                            SET balance = ?
                            WHERE user_id = ?
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(updateSql)) {

                        statement.setBigDecimal(1, newBalance);
                        statement.setInt(2, userId);

                        statement.executeUpdate();
                    }

                    // Record transaction
                    String transactionSql = """
                            INSERT INTO transactions
                            (account_number, type, amount, balance_after)
                            VALUES (?, ?, ?, ?)
                            """;

                    try (PreparedStatement statement =
                                 connection.prepareStatement(transactionSql)) {

                        statement.setString(1, accountNumber);
                        statement.setString(2, "WITHDRAW");
                        statement.setBigDecimal(3, amount);
                        statement.setBigDecimal(4, newBalance);

                        statement.executeUpdate();
                    }

                    // Save changes
                    connection.commit();

                    response.getWriter().println(
                            "<h2>Withdrawal Successful!</h2>"
                    );

                    response.getWriter().println(
                            "<p>Withdrawn Amount: ₹"
                                    + amount
                                    + "</p>"
                    );

                    response.getWriter().println(
                            "<p>New Balance: ₹"
                                    + newBalance
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

            response.getWriter().println(
                    "<p>Please enter a valid number.</p>"
            );

        } catch (Exception e) {

            response.getWriter().println(
                    "<h2>Withdrawal Failed</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}