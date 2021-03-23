package com.hesho.reservation.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class OtpAttemptService {

    private final Logger log = LoggerFactory.getLogger(OtpAttemptService.class);

    private final HttpServletRequest request;

    @Value("${custom.max-otp-attempts}")
    private int MAX_ATTEMPT;

    private LoadingCache<String, Integer> attemptsCache;

    public OtpAttemptService(HttpServletRequest request) {
        super();
        this.request = request;
        attemptsCache = CacheBuilder.newBuilder().
            expireAfterWrite(1, TimeUnit.DAYS).build(new CacheLoader<String, Integer>() {
            public Integer load(String key) {
                return 0;
            }
        });
    }

    public void otpSucceeded() {
        log.debug("Success otp attempt from IP {}", getClientIP());
        attemptsCache.invalidate(getClientIP());
    }

    public void otpFailed() {
        log.debug("Failed otp attempt from IP {}", getClientIP());
        int attempts = 0;
        try {
            attempts = attemptsCache.get(getClientIP());
        } catch (ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(getClientIP(), attempts);
    }

    public boolean isBlocked(String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (ExecutionException e) {
            return false;
        }
    }

    public String getClientIP() {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
