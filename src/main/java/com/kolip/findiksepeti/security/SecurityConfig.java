package com.kolip.findiksepeti.security;

import com.kolip.findiksepeti.user.CustomOAuth2ResultConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity()
public class SecurityConfig {
    Logger logger = LoggerFactory.getLogger(SecurityConfig.class.getName());

    private CustomUserDetailsService customUserDetailsService;
    private CustomOAuth2ResultConverter oAuth2ResultConverter;
    //    private OpenIdRoleMapper openIdRoleMapper;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          CustomOAuth2ResultConverter oAuth2ResultConverter) {
        this.customUserDetailsService = customUserDetailsService;
        this.oAuth2ResultConverter = oAuth2ResultConverter;
        //        this.openIdRoleMapper = openIdRoleMapper;
    }
    //
    //    @Bean
    //    public OpenIdRoleMapper getOpenIdRoleMapper(UserService userService) {
    //        OpenIdRoleMapper openIdRoleMapper = new OpenIdRoleMapper(userService);
    //        return openIdRoleMapper;
    //    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //        http.authorizeHttpRequests(requestMatcher -> requestMatcher.anyRequest().permitAll())
        //                .cors(Customizer.withDefaults())
        //                .csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers("/users"));

        //        http.securityMatcher("/products");
        http.authorizeHttpRequests(requestReq -> requestReq.requestMatchers("/admin").hasRole("ADMIN"))
                .authorizeHttpRequests(requestReq -> requestReq.requestMatchers(HttpMethod.PUT, "/users")
                        .hasAnyRole("USER", "PRE_USER"));
        http.authorizeHttpRequests(requestReq -> requestReq.requestMatchers(HttpMethod.GET, "/cart").permitAll());
        http.authorizeHttpRequests(requestReq -> requestReq.requestMatchers(HttpMethod.POST, "/cart").permitAll());
        http.authorizeHttpRequests(requestReq -> requestReq.requestMatchers(HttpMethod.DELETE, "/cart").permitAll());
        http.authorizeHttpRequests(requestMatchRegistry -> requestMatchRegistry.requestMatchers("/order"));
        http.authorizeHttpRequests(
                requestReq -> requestReq.requestMatchers(HttpMethod.GET, "/products/**").permitAll());
        http.authorizeHttpRequests(
                requestReq -> requestReq.requestMatchers(HttpMethod.POST, "/products").hasAnyRole("ADMIN"));
        http.authorizeHttpRequests(
                requestReq -> requestReq.requestMatchers(HttpMethod.PUT, "/products").hasAnyRole("ADMIN"));
        http.authorizeHttpRequests(
                requestReq -> requestReq.requestMatchers(HttpMethod.DELETE, "/products/*").hasAnyRole("ADMIN"));
        http.authorizeHttpRequests(
                        requestMatcherReq -> requestMatcherReq.requestMatchers(HttpMethod.POST, "/users").permitAll())
                .authorizeHttpRequests(requestMatchReq -> requestMatchReq.requestMatchers("/v1/csrf").permitAll())
                .authorizeHttpRequests(requestMatchReq -> requestMatchReq.requestMatchers("/images/**").permitAll())
                .authorizeHttpRequests(authorizeReq -> authorizeReq.anyRequest().authenticated())
                .requestCache(RequestCacheConfigurer::disable).httpBasic(Customizer.withDefaults()).sessionManagement(
                        sessionManagementConfig -> sessionManagementConfig.sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED)).userDetailsService(customUserDetailsService)
                //                .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.OPTIONS).permitAll())
                //                        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .cors(Customizer.withDefaults()).csrf(csrfConfig -> {
                    csrfConfig.ignoringRequestMatchers("/users");
                    //                    csrfConfig.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    csrfConfig.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
                }).oauth2Login(oauth2Config -> oauth2Config.defaultSuccessUrl("http://localhost:3000/")
                        //                        .failureUrl("http://localhost:3000/login")
                        .successHandler((request, response, authentication) -> {
                            logger.info("on success handler on oauth2Login :: " + authentication.toString());
                            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                            redirectStrategy.sendRedirect(request, response, "http://localhost:3000/");
                        }))

                //                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                //                                .userAuthoritiesMapper(openIdRoleMapper)))
                .exceptionHandling(exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(
                        new Http403ForbiddenEntryPoint())).logout(logoutConfigurer -> {
                    logoutConfigurer.logoutSuccessHandler(((request, response, authentication) -> {
                        //TODO(ugur) no need to redirect
                        response.setStatus(HttpServletResponse.SC_OK);
                    }));
                });

        SecurityFilterChain securityFilterChain = http.build();

        setOauth2LoginAuthenticationResultConverter(securityFilterChain);
        return securityFilterChain;
    }

    private void setOauth2LoginAuthenticationResultConverter(SecurityFilterChain securityFilterChain) {
        securityFilterChain.getFilters().forEach(filter -> {
            if (filter instanceof OAuth2LoginAuthenticationFilter) {
                ((OAuth2LoginAuthenticationFilter) filter).setAuthenticationResultConverter(oAuth2ResultConverter);
                return;
            }
        });
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "GET", "POST", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        configuration.setAllowCredentials(true);
        return source;
    }
}
