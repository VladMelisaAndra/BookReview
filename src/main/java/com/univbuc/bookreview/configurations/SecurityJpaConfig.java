package com.univbuc.bookreview.configurations;

import com.univbuc.bookreview.services.security.JpaUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Configuration
public class SecurityJpaConfig {

    private final JpaUserDetailsService userDetailsService;

    public SecurityJpaConfig(JpaUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests(auth -> auth
                        .requestMatchers("/books/view", "/books/add", "/categories").hasRole("ADMIN")
                        .requestMatchers("/books/*", "/webjars/**", "/login", "/resources/**", "/register").permitAll()
                        .requestMatchers("/login", "/register").anonymous()
                        .requestMatchers("/logout").authenticated()
                        .anyRequest().authenticated()
                )
                .userDetailsService(userDetailsService)
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .permitAll()
                                .loginProcessingUrl("/perform_login")
                )
                .logout(logout -> logout
                        .logoutUrl("/perform_logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(ex -> ex.accessDeniedPage("/not-authorized"))
                .build();
    }



}