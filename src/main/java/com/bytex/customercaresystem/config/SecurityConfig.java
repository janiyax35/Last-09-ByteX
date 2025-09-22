package com.bytex.customercaresystem.config;

import com.bytex.customercaresystem.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    @SuppressWarnings("deprecation")
    public PasswordEncoder passwordEncoder() {
        // As requested, using plain text for passwords. NOT for production.
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public URLs
                .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/images/**").permitAll()
                // Role-based authorization
                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/staff/**").hasAnyAuthority("ROLE_STAFF", "ROLE_ADMIN")
                .requestMatchers("/technician/**").hasAnyAuthority("ROLE_TECHNICIAN", "ROLE_ADMIN")
                .requestMatchers("/productmanager/**").hasAnyAuthority("ROLE_PRODUCT_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/warehouse/**").hasAnyAuthority("ROLE_WAREHOUSE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/customer/**").hasAuthority("ROLE_CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .userDetailsService(userDetailsService);

        return http.build();
    }
}
