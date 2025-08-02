package com.booknest.booknest.config;

import com.booknest.booknest.security.CustomUserDetailsService;
import com.booknest.booknest.security.JwtAuthenticationFilter;
import com.booknest.booknest.security.JwtUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpStatus;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig  {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService, AccessDeniedHandler accessDeniedHandler){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))

                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/**",
                                "/",
                                "/css/**", "/js/**", "/images/**", "/webjars/**",
                                "/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html"
                        ).permitAll()


                        .requestMatchers(HttpMethod.GET, "/books/**").authenticated()


                        .requestMatchers(HttpMethod.POST, "/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/books/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/books/**").hasRole("ADMIN")


                        .requestMatchers("/api/admin/**").hasRole("ADMIN")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

