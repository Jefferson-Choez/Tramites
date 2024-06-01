package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final UserDetailsService userDetailsService;

        @Autowired
        public SecurityConfig(UserDetailsService userDetailsService) {
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/Login", "/Registro", "/RecPass", "/RestPass",
                                        "/Registro/Politica-privacidad", "/Registro/Registro-exitoso", "/tramites/nuevo",
                                        "/tramites/lista").permitAll()
                                .anyRequest().authenticated())
                        .formLogin(form -> form
                                .loginPage("/Login")
                                .defaultSuccessUrl("/Panel-usuario", true)
                                .permitAll())
                        .logout(logout -> logout.permitAll())
                        .csrf(csrf -> csrf.disable());

                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
                AuthenticationManagerBuilder authenticationManagerBuilder =
                        http.getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                        .userDetailsService(userDetailsService)
                        .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
