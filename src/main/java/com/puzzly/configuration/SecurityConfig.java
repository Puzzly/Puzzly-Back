package com.puzzly.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.puzzly.api.coreComponent.securityCore.filter.CustomUsernamePasswordAuthenticationFilter;
import com.puzzly.api.coreComponent.securityCore.filter.JwtAuthenticationFilter;
import com.puzzly.api.coreComponent.securityCore.handler.CustomAccessDeniedHandler;
import com.puzzly.api.coreComponent.securityCore.handler.CustomAuthenticationEntryPoint;
import com.puzzly.api.coreComponent.securityCore.handler.CustomUsernamePasswordFailureHandler;
import com.puzzly.api.coreComponent.securityCore.handler.CustomUsernamePasswordSuccessHandler;
import com.puzzly.api.coreComponent.securityCore.provider.CustomUsernamePasswordAuthenticationProvider;
import com.puzzly.api.coreComponent.securityCore.securityService.CustomUserDetailsService;
import com.puzzly.api.util.JwtUtils;
import com.puzzly.api.util.CustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;

    private final CustomUtils customUtils;
    private final ObjectMapper mapper;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity,
                                           CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter,
                                           JwtAuthenticationFilter jwtAuthenticationFilter,
                                           CustomAuthenticationEntryPoint entryPoint,
                                           CustomAccessDeniedHandler handler) throws Exception{

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                // form 로그인 off
                .formLogin((auth) -> auth.disable())

                // http basic 인증방식 disable
                .httpBasic((auth) -> auth.disable())
                .authorizeHttpRequests(
                        (auth) -> auth
                                .requestMatchers("/resources/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                                .requestMatchers("/api/user/login", "/api/user/jwttest").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                                .requestMatchers("/git/**").permitAll()
                                .requestMatchers("/api/auth/refresh").permitAll()
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
                                .requestMatchers("/api/admin/test/admin").hasRole("ADMIN")
                                .requestMatchers("/api/user/test/user").hasRole("USER")
                                .requestMatchers("/api/calendar/**").hasRole("USER")
                                .requestMatchers("/error").permitAll()
                                .anyRequest().authenticated()
                                // TODO 개발용 모든 APi Setup (until User Controller FIN)
                                //.anyRequest().permitAll()
                ).exceptionHandling((exceptionHandling) -> {
                    exceptionHandling.authenticationEntryPoint(entryPoint).accessDeniedHandler(handler);
                })
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
            CustomUsernamePasswordSuccessHandler customUsernamePasswordSuccessHandler,
            CustomUsernamePasswordFailureHandler customUsernamePasswordFailureHandler
    ) {
        CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter = new CustomUsernamePasswordAuthenticationFilter(authenticationManager);
        customUsernamePasswordAuthenticationFilter.setFilterProcessesUrl("/api/user/login");
        customUsernamePasswordAuthenticationFilter.setUsernameParameter("email");
        customUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(customUsernamePasswordSuccessHandler);
        customUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(customUsernamePasswordFailureHandler);
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
