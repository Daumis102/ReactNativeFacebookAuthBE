package net.trendd.App.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.trendd.App.domain.AppUser;
import net.trendd.App.services.JwtTokenService;
import net.trendd.App.services.UserService;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public JwtTokenFilter(JwtTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Bypass filter for the /oauth/token endpoint since request to it has facebook token, not ours
        if ("/oauth/token".equals(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = verifyAndGetAuthentication(token);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        response.setHeader(HttpHeaders.AUTHORIZATION, jwtTokenService.refreshToken(token));
        filterChain.doFilter(request, response);
    }

    private Authentication verifyAndGetAuthentication(String token) {

        String subject = jwtTokenService.verifyAngGetSubject(token);
        if (subject == null) {
            return null;
        }
        AppUser appUser = userService.getUserById(subject);

        if (appUser == null) {
            return null;
        }

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(appUser.getUserRole().toString());

        User principal = new User(appUser.getId().toString(), "", List.of(authority));
        return new UsernamePasswordAuthenticationToken(principal, token, List.of(authority));
    }
}


