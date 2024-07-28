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
        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

        @Autowired
        public SecurityConfig(UserDetailsService userDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler) {
                this.userDetailsService = userDetailsService;
                this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                                .requestMatchers("/Login", "/Registro", "/RecPass", "/RestPass",
                                        "/Politica-privacidad", "/Registro/Registro-exitoso", "/robots.txt").permitAll()
                                .requestMatchers("/Panel-usuario").hasRole("USER")
                                .requestMatchers("/Panel-gestion").hasRole("Admin1")
                                .requestMatchers("/Panel-admin").hasRole("Admin2")
                                .requestMatchers("/Roles").hasRole("Admin2")
                                .requestMatchers("/Reportes/**").hasAnyRole("Admin1", "Admin2")
                                .anyRequest().authenticated())
                        .formLogin(form -> form
                                .loginPage("/Login")// especifica la pagina de inicio de sesion
                                .successHandler(customAuthenticationSuccessHandler)//maneja direcciones despues del is
                                .permitAll())//permite el acceso sin autenticacion
                        .logout(logout -> logout//cierre de sesion
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/Login?logout")
                                .permitAll());
                return http.build();
        }

        @Bean
        public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {//carga detalles del usuario
                AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
                authenticationManagerBuilder
                        .userDetailsService(userDetailsService)
                        .passwordEncoder(passwordEncoder());
                return authenticationManagerBuilder.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {//codifica las contraseñas
                return new BCryptPasswordEncoder();
        }
}
