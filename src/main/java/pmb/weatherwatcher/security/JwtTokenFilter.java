package pmb.weatherwatcher.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter checking presence of valid jwt token in header request.
 */
public class JwtTokenFilter
        extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenFilter.class);

    public static final String AUTHORIZATION_PREFIX = "Bearer ";
    private JwtTokenProvider jwtTokenProvider;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LOGGER.debug("Requesting: {}", getFullURL(request));
        Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION)).filter(t -> t.startsWith(AUTHORIZATION_PREFIX))
                .map(t -> StringUtils.substringAfter(t, AUTHORIZATION_PREFIX)).filter(jwtTokenProvider::isValid)
                .ifPresent(token -> SecurityContextHolder.getContext().setAuthentication(jwtTokenProvider.getAuthentication(token)));
        chain.doFilter(request, response);
    }

    private static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

}
