package com.banking.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static final Properties properties =
            new Properties();

    static {

        try (InputStream input =
                     DBConnection.class
                             .getClassLoader()
                             .getResourceAsStream("db.properties")) {

            if (input == null) {

                throw new RuntimeException(
                        "db.properties not found"
                );
            }

            properties.load(input);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to load db.properties",
                    e
            );
        }
    }

    private static final String URL =
            properties.getProperty("db.url");

    private static final String USER =
            properties.getProperty("db.user");

    private static final String PASSWORD =
            properties.getProperty("db.password");

    public static Connection getConnection()
            throws SQLException {

        try {

            Class.forName(
                    "com.mysql.cj.jdbc.Driver"
            );

        } catch (ClassNotFoundException e) {

            throw new SQLException(
                    "MySQL JDBC Driver not found",
                    e
            );
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}