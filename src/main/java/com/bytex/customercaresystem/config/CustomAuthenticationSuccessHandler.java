package com.bytex.customercaresystem.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {

        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            return;
        }

        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            String authorityName = grantedAuthority.getAuthority();
            switch (authorityName) {
                case "ROLE_ADMIN":
                    return "/admin/dashboard";
                case "ROLE_STAFF":
                    return "/staff/dashboard";
                case "ROLE_TECHNICIAN":
                    return "/technician/dashboard";
                case "ROLE_PRODUCT_MANAGER":
                    return "/productmanager/dashboard";
                case "ROLE_WAREHOUSE_MANAGER":
                    return "/warehouse/dashboard";
                case "ROLE_CUSTOMER":
                    return "/customer/dashboard";
                default:
                    throw new IllegalStateException("Unknown user role: " + authorityName);
            }
        }
        // Should not happen if user has a role
        return "/login?error";
    }
}
