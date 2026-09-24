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

@WebServlet("/dashboard-data")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session =
                request.getSession(false);

        // Check login session
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

        String userName =
                (String) session.getAttribute("userName");

        String sql = """
                SELECT account_number, balance
                FROM accounts
                WHERE user_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String accountNumber =
                        resultSet.getString("account_number");

                String balance =
                        resultSet.getBigDecimal("balance")
                                .toString();

                response.getWriter().println("""
                        {
                            "success": true,
                            "name": "%s",
                            "accountNumber": "%s",
                            "balance": "%s"
                        }
                        """.formatted(
                                userName,
                                accountNumber,
                                balance
                        ));

            } else {

                response.setStatus(
                        HttpServletResponse.SC_NOT_FOUND
                );

                response.getWriter().println("""
                        {
                            "success": false,
                            "message": "Account not found"
                        }
                        """);
            }

        } catch (Exception e) {

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