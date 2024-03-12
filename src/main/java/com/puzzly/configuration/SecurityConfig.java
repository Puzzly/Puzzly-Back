package com.puzzly.configuration;

import com.puzzly.Utils.JwtUtils;
import com.puzzly.security.filter.CustomUsernamePasswordAuthenticationFilter;
import com.puzzly.security.filter.JwtAuthenticationFilter;
import com.puzzly.security.handler.CustomUsernamePasswordSuccessHandler;
import com.puzzly.security.provider.CustomUsernamePasswordAuthenticationProvider;
import com.puzzly.security.securityService.CustomUserDetailsService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    public SecurityConfig(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        // 비밀번호 암호화 용도
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter,
                                           JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception{

        httpSecurity.csrf((auth) -> auth.disable())
                .headers(headers -> headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                // form 로그인 off
                .formLogin((auth) -> auth.disable())

                // http basic 인증방식 disable
                .httpBasic((auth) -> auth.disable())
                .authorizeHttpRequests(
                        (auth) -> auth
                                .requestMatchers("/resources/**").permitAll()
                                .requestMatchers("/api/user/join", "/api/user/login", "/api/user/jwttest").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/api/auth/refresh").permitAll()
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers("/api/user/test/admin").hasRole("ADMIN")
                                .requestMatchers("/api/user/test/user").hasRole("USER")
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, BasicAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAt(customUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Content-Type", "Authorization", "X-XSRF-token"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter(
            AuthenticationManager authenticationManager,
            CustomUsernamePasswordSuccessHandler customUsernamePasswordSuccessHandler
    ) {
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl("/api/user/login");
        customUsernamePasswordAuthenticationFilter.setUsernameParameter("email");
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(customUsernamePasswordSuccessHandler);
        //customAuthenticationFilter.setAuthenticationFailureHandler(customAuthFailureHandler);
        customUsernamePasswordAuthenticationFilter.afterPropertiesSet();
        return customUsernamePasswordAuthenticationFilter;
    }
    @Bean
    public AuthenticationManager authenticationManager(CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(customUsernamePasswordAuthenticationProvider));
    }

    @Bean
    public CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService) {
        return new CustomUsernamePasswordAuthenticationProvider(
                userDetailsService
        );
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }


}
