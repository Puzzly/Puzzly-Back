package com.puzzly.configuration;

import com.puzzly.api.coreComponent.securityCore.filter.CustomUsernamePasswordAuthenticationFilter;
import com.puzzly.api.coreComponent.securityCore.filter.JwtAuthenticationFilter;
import com.puzzly.api.coreComponent.securityCore.handler.CustomUsernamePasswordSuccessHandler;
import com.puzzly.api.coreComponent.securityCore.provider.CustomUsernamePasswordAuthenticationProvider;
import com.puzzly.api.coreComponent.securityCore.securityService.CustomUserDetailsService;
import com.puzzly.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class SecurityConfig {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

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
                                .requestMatchers("/api/admin/test/admin").hasRole("ADMIN")
                                .requestMatchers("/api/user/test/user").hasRole("USER")
                                //.anyRequest().authenticated()
                                // TODO 개발용 모든 APi Setup (until User Controller FIN)
                                .anyRequest().permitAll()
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
        return new CustomUsernamePasswordAuthenticationProvider(userDetailsService, bCryptPasswordEncoder);
        // 이거 하면서 custom-provider에 @component 붙였음
        //return new CustomUsernamePasswordAuthenticationProvider(userDetailsService);
    }
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtils, userDetailsService);
    }


}