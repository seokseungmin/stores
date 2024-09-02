package com.springboot.stores.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.stores.common.util.JWTUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("STORE-TOKEN"); // 헤더 이름 변경
        String jwtToken = null;
        String userEmail = null;

        if (authHeader != null) {
            jwtToken = authHeader;
            try {
                userEmail = JWTUtils.getEmailFromToken(jwtToken); // 토큰에서 사용자 이메일 추출로 변경
                System.out.println("UserEmail extracted from token: " + userEmail); // 로그 추가
            } catch (JWTVerificationException e) {
                // 토큰이 유효하지 않을 경우 예외 처리
                //throw new JWTVerificationException("Invalid JWT token");
                // 400 응답 생성
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setContentType("application/json");

                // ResponseResult 구조를 직접 생성
                ObjectMapper objectMapper = new ObjectMapper();
                response.getWriter().write(objectMapper.writeValueAsString(createErrorResponse("Token information is incorrect.", HttpStatus.BAD_REQUEST.getReasonPhrase())));
                return;
            }
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("User details loaded: " + userDetails.getUsername() + ", Authorities: " + userDetails.getAuthorities()); // 로그 추가

            if (JWTUtils.validateToken(jwtToken, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    // Error response 생성 메소드
    private Map<String, Object> createErrorResponse(String message, String status) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, Object> header = new HashMap<>();

        header.put("result", false);
        header.put("resultCode", HttpStatus.BAD_REQUEST.value());
        header.put("message", message);
        header.put("status", status);

        errorResponse.put("header", header);
        errorResponse.put("body", null);

        return errorResponse;
    }
}

