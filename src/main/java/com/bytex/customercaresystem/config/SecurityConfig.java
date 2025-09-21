package com.bytex.customercaresystem.config;

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

    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    /**
     * WARNING: This PasswordEncoder is for development purposes only and is NOT SECURE.
     * It stores passwords in plain text as requested.
     * For a production environment, use a strong hashing algorithm like BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Publicly accessible URLs
                .requestMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/images/**", "/webfonts/**", "/favicon.ico").permitAll()
                // Role-based authorization
                .requestMatchers("/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/staff/**").hasAuthority("STAFF")
                .requestMatchers("/technician/**").hasAuthority("TECHNICIAN")
                .requestMatchers("/productmanager/**").hasAuthority("PRODUCT_MANAGER")
                .requestMatchers("/warehouse/**").hasAuthority("WAREHOUSE_MANAGER")
                .requestMatchers("/customer/**").hasAuthority("CUSTOMER")
                // All other requests must be authenticated
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
            );

        return http.build();
    }
}
