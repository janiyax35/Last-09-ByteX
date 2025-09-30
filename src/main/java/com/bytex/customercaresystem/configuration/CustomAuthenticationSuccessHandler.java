package com.bytex.customercaresystem.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String redirectUrl = "/login?error"; // Default redirect URL if no role matches

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            // Spring Security prefixes roles with "ROLE_", so we check for that
            if (authorityName.equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (authorityName.equals("ROLE_STAFF")) {
                redirectUrl = "/staff/dashboard";
                break;
            } else if (authorityName.equals("ROLE_TECHNICIAN")) {
                redirectUrl = "/technician/dashboard";
                break;
            } else if (authorityName.equals("ROLE_PRODUCT_MANAGER")) {
                redirectUrl = "/product-manager/dashboard";
                break;
            } else if (authorityName.equals("ROLE_WAREHOUSE_MANAGER")) {
                redirectUrl = "/warehouse-manager/dashboard";
                break;
            } else if (authorityName.equals("ROLE_CUSTOMER")) {
                redirectUrl = "/customer/dashboard";
                break;
            }
        }
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }
}