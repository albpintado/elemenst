package com.albpintado.elemenst.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/*
Filter to authenticate the user reading the credentials from
the request.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    AuthCredentials authCredentials = new AuthCredentials();

    try {
      authCredentials = new ObjectMapper().readValue(request.getReader(), AuthCredentials.class);
    } catch (IOException e) {
      e.getMessage();
    }

    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
            authCredentials.getUserName(),
            authCredentials.getPassword(),
            Collections.emptyList()
    );

    return getAuthenticationManager().authenticate(usernamePasswordAuthenticationToken);
  }

  /*
  Function to return the Bearer token in case that the user
  can be authenticated with its data.
   */
  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
    IUserDetails userDetails = (IUserDetails) authResult.getPrincipal();
    String token = TokenManager.createToken(userDetails.getUsername());

    response.addHeader("Authorization", "Bearer " + token);
    response.getWriter().flush();
  }
}
