package academy.wakanda.wakanda_ai.config.security;

import academy.wakanda.wakanda_ai.handler.ErrorApiResponse;
import academy.wakanda.wakanda_ai.autenticacao.infra.UsuarioAdmSpringDataJpaRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UsuarioAdmSpringDataJpaRepository usuarioAdmSpringDataJpaRepository;
    private final List<RequestMatcher> publicRequestMatchers;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if (token != null) {
            try {
                var username = tokenService.validateToken(token);
                var userDetails = usuarioAdmSpringDataJpaRepository.findByUsername(username)
                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

                var authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json; charset=UTF-8");
                var errorResponse = ErrorApiResponse.builder()
                        .message("Usuário não encontrado ou token inválido.")
                        .description("Faça login novamente ou busque ajuda do suporte.")
                        .build();
                var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                var json = mapper.writeValueAsString(errorResponse);
                response.getWriter().write(json);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        boolean isPublic = publicRequestMatchers.stream().anyMatch(matcher -> matcher.matches(request));
        boolean hasAuth = request.getHeader("Authorization") != null;
        return isPublic && !hasAuth;
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }

}
