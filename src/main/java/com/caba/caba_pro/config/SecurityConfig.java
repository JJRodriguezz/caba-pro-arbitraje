/**
 * Archivo: SecurityConfig.java Autores: Isabella.Idarraga Fecha última modificación: [04.09.2025]
 * Descripción: Configuración de seguridad para la aplicación Proyecto: CABA Pro - Sistema de
 * Gestión Integral de Arbitraje
 */
package com.caba.caba_pro.config;

// 3. Spring Framework
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  // 2. Variables de instancia
  private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

  // 3. Constructores
  public SecurityConfig(CustomAuthenticationSuccessHandler authenticationSuccessHandler) {
    this.authenticationSuccessHandler = authenticationSuccessHandler;
  }

  // 4. Métodos públicos

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(
            authorize ->
                authorize
                    // Rutas públicas
                    .requestMatchers("/h2-console/**")
                    .permitAll()
                    .requestMatchers(
                        "/login", "/registro", "/css/**", "/js/**", "/images/**", "/h2-console")
                    .permitAll()

                    // Rutas administrativas - solo ROLE_ADMIN
                    .requestMatchers("/admin/**")
                    .hasRole("ADMIN")

                    // Rutas de árbitro - solo ROLE_ARBITRO
                    .requestMatchers("/arbitro/**")
                    .hasRole("ARBITRO")

                    // Cualquier otra request requiere autenticación
                    .anyRequest()
                    .authenticated())
        .formLogin(
            form ->
                form.loginPage("/login")
                    .successHandler(authenticationSuccessHandler) // Usar handler personalizado
                    .failureUrl("/login?error=true")
                    .permitAll())
        .logout(
            logout ->
                logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll())
        .sessionManagement(session -> session.maximumSessions(1).maxSessionsPreventsLogin(false))

        // Deshabilitar CSRF solo en desarrollo
        .csrf(csrf -> csrf.disable())

        // Permitir frames para H2 (solo desarrollo)
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

    return http.build();
  }
}
