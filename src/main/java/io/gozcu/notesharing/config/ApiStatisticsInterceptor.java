package io.gozcu.notesharing.config;

import io.gozcu.notesharing.service.ApiStatisticsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class ApiStatisticsInterceptor implements HandlerInterceptor {

    @Autowired
    private ApiStatisticsService apiStatisticsService;

    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        startTime.set(System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // No action needed here
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long responseTime = System.currentTimeMillis() - startTime.get();
        startTime.remove(); // Clean up ThreadLocal to prevent memory leaks

        String endpoint = request.getRequestURI();
        String method = request.getMethod();
        String ipAddress = getClientIp(request);
        Integer statusCode = response.getStatus();
        String userAgent = request.getHeader("User-Agent");

        // Try to extract userId from request attributes or headers
        // In a real application, you would extract this from your authentication system
        Long userId = null;

        // For this example, we'll try to extract from auth header if it exists
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // This is a simplified example - you would need to properly decode your token
            String token = authHeader.substring(7);
            // Extract user ID from token - implementation depends on your token structure
            // For this example, we're using a very simple approach assuming the format in your AuthServiceImpl
            if (token.contains(".")) {
                String[] parts = token.split("\\.");
                if (parts.length > 2) {
                    try {
                        userId = Long.parseLong(parts[2]);
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
            }
        }

        apiStatisticsService.recordApiCall(endpoint, method, ipAddress, statusCode, responseTime, userAgent, userId);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // XFF can contain multiple IPs, the first one is the client
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}