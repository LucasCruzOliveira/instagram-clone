package br.edu.ifpb.instagram.security;

import br.edu.ifpb.instagram.service.impl.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtUtils jwtUtils;
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationFilter filter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        jwtUtils = spy(new JwtUtils());
        userDetailsService = mock(UserDetailsServiceImpl.class);
        filter = new JwtAuthenticationFilter(jwtUtils, userDetailsService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldContinueFilterWhenAuthorizationHeaderIsMissing() throws Exception {

        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {

        String token = "valid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        doReturn("lucas").when(jwtUtils).getUsernameFromToken(token);
        doReturn(true).when(jwtUtils).validateToken(token);

        UserDetails userDetails =
                User.withUsername("lucas")
                        .password("123")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("lucas"))
                .thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        verify(userDetailsService).loadUserByUsername("lucas");
        verify(filterChain).doFilter(request, response);
    }
}
