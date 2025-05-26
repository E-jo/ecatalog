package com.example.ecatalog

import com.example.ecatalog.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var restAuthenticationEntryPoint: RestAuthenticationEntryPoint

    @Throws(Exception::class)
    protected fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService<UserDetailsService?>(userService).passwordEncoder(encoder())
    }

    @Bean
    fun encoder(): PasswordEncoder =
        BCryptPasswordEncoder(13)

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        println("securityFilterChain called")
        return http
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        HttpMethod.POST, "/actuator/shutdown"
                    ).permitAll()
                    .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                    .requestMatchers(HttpMethod.POST, "/products").hasAuthority("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/users/remove/**").hasAuthority("ADMIN")
                    .anyRequest().authenticated()
            }
            .httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint)
            .and()
            .userDetailsService(userService)
            .csrf().disable()
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .build()
    }
}