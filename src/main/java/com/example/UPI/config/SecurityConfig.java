package com.example.UPI.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for API endpoints so bridge nodes can POST data
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
                .authorizeHttpRequests(auth -> auth
                        // Allow mesh and bridge APIs to be accessed without browser login
                        .requestMatchers("/api/**").permitAll()
                        // Allow health checks for Docker/Kubernetes
                        .requestMatchers("/actuator/health", "/actuator/prometheus").permitAll()
                        // Protect the dashboard and H2 console
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/", true) // Redirect to dashboard on login
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        // H2 console frame options (required if you use H2 in dev mode)
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Simple in-memory admin user for the dashboard
        // In production, wire this to your database or an OAuth2 provider
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}admin123") // {noop} means plain text; use BCrypt in real prod!
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}