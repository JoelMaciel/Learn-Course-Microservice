package com.joel.learn.course.api.configs.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Log4j2
public class AuthenticationJwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String jwtString = getTokenHeader(httpServletRequest);

            if (jwtString != null && jwtProvider.validateJwt(jwtString)) {
                String userId = jwtProvider.getSubjectJwt(jwtString);

                String rolesString = jwtProvider.getClaimNameJwt(jwtString, "roles");

                UserDetails userDetails = UserDetailsImpl.build(UUID.fromString(userId), rolesString);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set User Authentication: {} ", e);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getTokenHeader(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        return StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")
                ? headerAuth.substring(7) : null;
    }
}
