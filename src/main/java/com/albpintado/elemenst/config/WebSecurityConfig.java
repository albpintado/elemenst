package com.albpintado.elemenst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

// Spring Security main configuration class
@Configuration
public class WebSecurityConfig {

    /*
    Get a IUserDetails object to check if an account that
    matches the email sent exists in DB.
    */
    private final IUserDetailsService userDetailsService;

    /*
    Get the JWTAuthorizationFilter to filter the request in
    order to get the access token.
    */
    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    public WebSecurityConfig(IUserDetailsService IUserDetailsService, JWTAuthorizationFilter jwtAuthorizationFilter) {
        this.userDetailsService = IUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    /*
    Make the filter that imports the filter to manage the request
    in order to authenticate the user.
    */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager)
            throws Exception {
        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

    /*
    Returns the filter that matches against the http request
    filters it to authenticate the user.
    It disables the csrf to avoid that it will cloak the JWT
    authentication. Also, it permits all request just for
    authenticated users, but /login permits all requests without
    authentication. It manages the session creating one if any
    is already created and passes the filters before build the chain.
    */
        return http
                .csrf().disable()
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers("/login")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/users")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /*
    Instantiate the manager that handles the authentication
    process for the user, checking its existence with the
    password matching function with the encoder.
     */
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(this.userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /*
    Create a password encoder for the user's password using
    BCrypt password-hashing function.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebMvcConfigurer customConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
                configurer.defaultContentType(MediaType.APPLICATION_JSON);
            }

            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/login").allowedOrigins("http://localhost:5173").exposedHeaders("Authorization");
                registry.addMapping("/logout").allowedOrigins("http://localhost:5173");
            }

            @Bean
            public CorsConfigurationSource corsConfigurationSource() {
                final CorsConfiguration configuration = new CorsConfiguration();
                List<String> origins = new java.util.ArrayList<>();
                origins.add("*");
                configuration.setAllowedOrigins(origins);
                List<String> allowedMethods = new java.util.ArrayList<>();
                allowedMethods.add("HEAD");
                allowedMethods.add("GET");
                allowedMethods.add("POST");
                allowedMethods.add("PUT");
                allowedMethods.add("DELETE");
                allowedMethods.add("PATCH");
                configuration.setAllowedMethods(allowedMethods);
                // setAllowCredentials(true) is important, otherwise:
                // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
                configuration.setAllowCredentials(true);
                // setAllowedHeaders is important! Without it, OPTIONS preflight request
                // will fail with 403 Invalid CORS request
                List<String> allowedHeaders = new java.util.ArrayList<>();
                allowedHeaders.add("Authorization");
                allowedHeaders.add("Cache-Control");
                allowedHeaders.add("Content-Type");
                configuration.setAllowedHeaders(allowedHeaders);
                configuration.addExposedHeader("Authorization");
                final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
            }
        };
    }
}
