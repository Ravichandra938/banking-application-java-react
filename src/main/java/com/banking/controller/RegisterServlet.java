package com.banking.controller;

import com.banking.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.*;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String userSql = """
                INSERT INTO users
                (name, email, username, password)
                VALUES (?, ?, ?, ?)
                """;

        String accountSql = """
                INSERT INTO accounts
                (user_id, account_number, balance)
                VALUES (?, ?, ?)
                """;

        response.setContentType("text/html");

        try (Connection connection = DBConnection.getConnection()) {

            // Start transaction
            connection.setAutoCommit(false);

            try {

                // 1. Create user
                int userId;

                try (PreparedStatement userStatement =
                             connection.prepareStatement(
                                     userSql,
                                     Statement.RETURN_GENERATED_KEYS)) {

                    userStatement.setString(1, name);
                    userStatement.setString(2, email);
                    userStatement.setString(3, username);

                    String hashedPassword =
                          BCrypt.hashpw(password, BCrypt.gensalt());


                    userStatement.setString(4, hashedPassword);

                    userStatement.executeUpdate();

                    try (ResultSet keys =
                                 userStatement.getGeneratedKeys()) {

                        if (keys.next()) {
                            userId = keys.getInt(1);
                        } else {
                            throw new SQLException(
                                    "Unable to get user ID"
                            );
                        }
                    }
                }

                // 2. Generate account number
                String accountNumber =
                        "10000000" + userId;

                // 3. Create bank account
                try (PreparedStatement accountStatement =
                             connection.prepareStatement(accountSql)) {

                    accountStatement.setInt(1, userId);
                    accountStatement.setString(2, accountNumber);
                    accountStatement.setBigDecimal(
                            3,
                            new java.math.BigDecimal("0.00")
                    );

                    accountStatement.executeUpdate();
                }

                // Save everything
                connection.commit();

                response.getWriter().println(
                        "<h2>Registration Successful!</h2>"
                );

                response.getWriter().println(
                        "<p>Welcome, " + name + "</p>"
                );

                response.getWriter().println(
                        "<p>Your Account Number: "
                                + accountNumber
                                + "</p>"
                );

                response.getWriter().println(
                        "<p>Initial Balance: ₹0.00</p>"
                );

                response.getWriter().println(
                        "<a href='login.html'>Go to Login</a>"
                );

            } catch (Exception e) {

                // Undo both user and account creation
                connection.rollback();

                throw e;
            }

        } catch (Exception e) {

            response.getWriter().println(
                    "<h2>Registration Failed</h2>"
            );

            response.getWriter().println(
                    "<p>" + e.getMessage() + "</p>"
            );
        }
    }
}