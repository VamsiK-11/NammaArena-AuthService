package com.nammaarena.auth.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.nammaarena.auth.service.*.*(..))")
    public void serviceLayer() {}

    @Before("serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        log.info("Entering method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterReturning("serviceLayer()")
    public void logMethodExit(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().toShortString());
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        log.error("Exception in method: {} - {}", joinPoint.getSignature().toShortString(), exception.getMessage());
    }

    @Around("execution(* com.nammaarena.auth.service.AuthService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            log.info("Method {} executed in {} ms", joinPoint.getSignature().toShortString(), (endTime - startTime));
            return result;
        } catch (Throwable throwable) {
            long endTime = System.currentTimeMillis();
            log.error("Method {} failed after {} ms", joinPoint.getSignature().toShortString(), (endTime - startTime));
            throw throwable;
        }
    }
}