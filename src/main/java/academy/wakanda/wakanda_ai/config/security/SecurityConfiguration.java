package academy.wakanda.wakanda_ai.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public List<RequestMatcher> publicRequestMatchers() {
        return List.of(
                new AntPathRequestMatcher(HttpMethod.POST.name(), "/autenticacao/login"),
                new AntPathRequestMatcher(HttpMethod.POST.name(), "/autenticacao/cadastro"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/painel-dados/login"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/gameficacao/home"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/gameficacao/catalogo/missoes"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/gameficacao/missoes/*"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/gameficacao/ProgressoWakanders"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/swagger-ui/**"),
                new AntPathRequestMatcher(HttpMethod.GET.name(), "/v3/api-docs/**")
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, SecurityFilter securityFilter)
            throws Exception {

        String[] devPaths = {
                "/autenticacao/token-teste",
                "/gameficacao/**",
                "/trilhas/**",
                "/jornadas/**",
                "/missoes/**",
                "/classes/**",
                "/tipos-missao/**",
                "/wakander/busca-wakanders",
                "/wakander/busca-wakanders/*",
                "/wakander/estatistica-wakanders",

        };

        String[] liderancaPaths = {
                "/wakander/atualiza-dados-asaas",
                "/wakander/atualiza-status-cadastro",
                "/wakander/*/inicia-onboarding-manual",
                "/wakander/*/fiador/atualizacao-link"
        };

        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/autenticacao/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/autenticacao/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/painel-dados/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/gameficacao/home", "/gameficacao/catalogo/missoes", "/gameficacao/missoes/*", "/gameficacao/ProgressoWakanders", "/gameficacao/progresso").permitAll()
                        .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/painel-dados/dashboard").authenticated()
//                        .requestMatchers("/gameficacao/**").authenticated()
                        .requestMatchers(devPaths).hasRole("DEV")
                        .requestMatchers(liderancaPaths).hasRole("LIDERANCA")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
