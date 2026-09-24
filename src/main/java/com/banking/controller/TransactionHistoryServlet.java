package com.banking.controller;

import com.banking.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/transactions")
public class TransactionHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        if (session == null ||
            session.getAttribute("userId") == null) {

            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.getWriter().println("""
                    {
                        "success": false,
                        "message": "User is not logged in"
                    }
                    """);

            return;
        }

        int userId =
                (Integer) session.getAttribute("userId");

        String sql = """
                SELECT
                    t.type,
                    t.amount,
                    t.balance_after,
                    t.transaction_date
                FROM transactions t
                JOIN accounts a
                    ON t.account_number = a.account_number
                WHERE a.user_id = ?
                ORDER BY t.transaction_date DESC
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet resultSet =
                    statement.executeQuery();

            StringBuilder json =
                    new StringBuilder();

            json.append("""
                    {
                        "success": true,
                        "transactions": [
                    """);

            boolean first = true;

            while (resultSet.next()) {

                if (!first) {
                    json.append(",");
                }

                first = false;

                String type =
                        resultSet.getString("type");

                String amount =
                        resultSet.getBigDecimal("amount")
                                .toString();

                String balanceAfter =
                        resultSet.getBigDecimal(
                                "balance_after")
                                .toString();

                String transactionDate =
                        resultSet.getTimestamp(
                                "transaction_date")
                                .toString();

                json.append("""
                        {
                            "type": "%s",
                            "amount": "%s",
                            "balanceAfter": "%s",
                            "transactionDate": "%s"
                        }
                        """.formatted(
                                type,
                                amount,
                                balanceAfter,
                                transactionDate
                        ));
            }

            json.append("""
                        ]
                    }
                    """);

            response.getWriter().println(
                    json.toString()
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR
            );

            response.getWriter().println("""
                    {
                        "success": false,
                        "message": "Server error occurred"
                    }
                    """);
        }
    }
}