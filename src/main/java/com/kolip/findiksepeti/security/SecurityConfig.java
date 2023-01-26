package com.kolip.findiksepeti.security;

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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity(debug = true)
public class SecurityConfig {
    Logger logger = LoggerFactory.getLogger(SecurityConfig.class.getName());

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.authorizeHttpRequests(requestMatcher -> requestMatcher.anyRequest().permitAll())
//                .cors(Customizer.withDefaults())
//                .csrf(csrfConfig -> csrfConfig.ignoringRequestMatchers("/users"));

//        http.securityMatcher("/products");
        http.authorizeHttpRequests(requestReq -> requestReq.requestMatchers("/admin").hasRole("ADMIN"));
        http.authorizeHttpRequests(requestMatcherReq -> requestMatcherReq.requestMatchers(HttpMethod.POST, "/users")
                        .permitAll())
                .authorizeHttpRequests(requestMatchReq -> requestMatchReq.requestMatchers("/v1/csrf").permitAll())
                .authorizeHttpRequests(authorizeReq -> authorizeReq.anyRequest()
                        .authenticated()).requestCache(RequestCacheConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionManagementConfig -> sessionManagementConfig.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .userDetailsService(new CustomUserDetailsService())
//                .authorizeHttpRequests(authorize -> authorize.requestMatchers(HttpMethod.OPTIONS).permitAll())
//                        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .cors(Customizer.withDefaults())
                .csrf(csrfConfig -> {
                    csrfConfig.ignoringRequestMatchers("/users");
//                    csrfConfig.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    csrfConfig.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler());
                })
                .oauth2Login(oauth2Config -> oauth2Config.defaultSuccessUrl("http://localhost:3000/")
//                        .failureUrl("http://localhost:3000/login")
                        .successHandler((request, response, authentication) -> {
                            logger.info("on success handler on oauth2Login :: " + authentication.toString());
                            RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                            redirectStrategy.sendRedirect(request, response, "http://localhost:3000/");
                        })
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig.userAuthoritiesMapper(new OpenIdRoleMapper())))

                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint());
        return http.build();

    }


    private UserDetailsService customUserDetailsService() {
// The builder will ensure the passwords are encoded before saving in memory
        UserDetails user = User.builder()
                .username("user")
                .password("{noop}password")
                .roles("USER")
                .build();
        UserDetails admin = User.builder()
                .username("ugur.kolip@gmail.com")
                .password("{noop}password")
                .roles("USER", "ADMIN")
                .build();

        return null;
    }
//
//    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
//        return (authorities) -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//            authorities.forEach(authority -> {
//                if (OidcUserAuthority.class.isInstance(authority)) {
//                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
//
//                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
//                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
//
//
//                    // Map the claims found in idToken and/or userInfo
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                } else if (OAuth2UserAuthority.class.isInstance(authority)) {
//                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
//
//                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
//
//                    // Map the attributes found in userAttributes
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                }
//            });
//
//            return mappedAuthorities;
//        };
//    }
//    private GrantedAuthoritiesMapper userAuthoritiesMapper() {
//        return (authorities) -> {
//            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//            authorities.forEach(authority -> {
//                if (OidcUserAuthority.class.isInstance(authority)) {
//                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
//
//                    OidcIdToken idToken = oidcUserAuthority.getIdToken();
//                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
//
//
//                    // Map the claims found in idToken and/or userInfo
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                } else if (OAuth2UserAuthority.class.isInstance(authority)) {
//                    OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
//
//                    Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();
//
//                    // Map the attributes found in userAttributes
//                    // to one or more GrantedAuthority's and add it to mappedAuthorities
//
//                }
//            });
//
//            return mappedAuthorities;
//        };
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("OPTIONS", "GET", "POST", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        configuration.setAllowCredentials(true);
        return source;
    }


}
