package com.banking.controller;

import com.banking.util.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String username =
                request.getParameter("username");

        String password =
                request.getParameter("password");

        String sql = """
                SELECT id, name, email, password
                FROM users
                WHERE username = ?
                """;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, username);

            ResultSet resultSet =
                    statement.executeQuery();

            if (resultSet.next()) {

                String storedPassword =
                        resultSet.getString("password");

                if (BCrypt.checkpw(
                        password,
                        storedPassword
                )) {

                    HttpSession session =
                            request.getSession();

                    session.setAttribute(
                            "userId",
                            resultSet.getInt("id")
                    );

                    session.setAttribute(
                            "userName",
                            resultSet.getString("name")
                    );

                    session.setAttribute(
                            "email",
                            resultSet.getString("email")
                    );

                    response.getWriter().println("""
                            {
                                "success": true,
                                "message": "Login successful"
                            }
                            """);

                } else {

                    response.setStatus(
                            HttpServletResponse.SC_UNAUTHORIZED
                    );

                    response.getWriter().println("""
                            {
                                "success": false,
                                "message": "Invalid username or password"
                            }
                            """);
                }

            } else {

                response.setStatus(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                response.getWriter().println("""
                        {
                            "success": false,
                            "message": "Invalid username or password"
                        }
                        """);
            }

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