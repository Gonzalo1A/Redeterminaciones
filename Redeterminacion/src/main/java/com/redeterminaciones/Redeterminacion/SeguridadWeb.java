package com.redeterminaciones.Redeterminacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SeguridadWeb {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/css/**", "/js/**", "/img/*", "/**")
                .permitAll()
        ).formLogin((t) -> {
            t.loginPage("/usuario/login")
                    .loginProcessingUrl("/logincheck")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/")
                    .permitAll();
        }).logout(logout -> logout
                .logoutSuccessUrl("/").permitAll()
        ).csrf().disable();

        return http.build();
    }
//     .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Activar CSRF
//        .and()
//        .headers().frameOptions().sameOrigin() // Necesario si est�s utilizando Spring Security junto con Spring Data JPA
//        .and()
//        .httpBasic();

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

//    @Bean
//    UserDetailsManager inMemoryUserDetailsManager() {
//        var user1 = User.withUsername("user").password("{noop}password").roles("USER").build();
//        var user2 = User.withUsername("admin").password("{noop}password").roles("USER", "ADMIN").build();
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
}
