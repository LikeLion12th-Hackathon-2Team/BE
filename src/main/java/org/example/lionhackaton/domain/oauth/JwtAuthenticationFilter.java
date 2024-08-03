package org.example.lionhackaton.domain.oauth;

import java.io.IOException;
import java.security.SignatureException;
import java.util.ArrayList;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		try {
			String token = resolveToken(request);
			if (token != null && jwtTokenProvider.validateToken(token)) {
				String userId = jwtTokenProvider.extractSubject(token);
				CustomUserDetails userDetails = new CustomUserDetails(Long.valueOf(userId), userId,
					new ArrayList<>()); // 빈 권한 목록 사용
				SecurityContextHolder.getContext()
					.setAuthentication(new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities()));
			}
		} catch (ExpiredJwtException e) {
			// 토큰이 만료되었을 때 처리
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
			return;
		} catch (UnsupportedJwtException e) {
			// 지원되지 않는 JWT 처리
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported JWT token");
			return;
		} catch (MalformedJwtException e) {
			// 잘못된 JWT 형식 처리
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Malformed JWT token");
			return;
		} catch (IllegalArgumentException e) {
			// 잘못된 JWT 또는 기타 오류 처리
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
			return;
		}

		// 다음 필터 또는 서블릿 호출
		filterChain.doFilter(request, response);
	}


	private String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
