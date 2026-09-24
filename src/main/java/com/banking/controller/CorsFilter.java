package com.banking.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        httpResponse.setHeader(
                "Access-Control-Allow-Origin",
                "http://localhost:5173"
        );

        httpResponse.setHeader(
                "Access-Control-Allow-Methods",
                "GET, POST, OPTIONS"
        );

        httpResponse.setHeader(
                "Access-Control-Allow-Headers",
                "Content-Type"
        );

        httpResponse.setHeader(
                "Access-Control-Allow-Credentials",
                "true"
        );

        chain.doFilter(request, response);
    }
}