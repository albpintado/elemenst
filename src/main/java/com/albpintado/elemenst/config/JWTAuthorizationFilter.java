package com.albpintado.elemenst.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
Filter to authorize the user getting the Bearer token
from the request header and returning the string token.
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String bearerToken = request.getHeader("Authorization");

    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      String token = bearerToken.replace("Bearer ", "");
      UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = TokenManager.getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }
    filterChain.doFilter(request, response);
  }
}
