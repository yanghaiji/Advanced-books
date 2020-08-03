package com.javayh.advanced.spring.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author haiyang
 */
@Slf4j
@Aspect
@Configuration
@Order(-1)
public class SysLogAspect {

    @Around("@annotation(sysLog)")
    public Object getLog(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Object proceed = null;
        long time = System.currentTimeMillis();
        try {
            proceed = joinPoint.proceed();
            time = System.currentTimeMillis() - time;
            return proceed;
        }
        catch (Throwable throwable) {
            throw throwable;
        }
        finally {
            // 方法执行后
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            SysLog annotation = methodSignature.getMethod().getAnnotation(SysLog.class);
            String value = null;
            if (annotation != null) {
                value = annotation.value();
            }
            log.info(value+"执行耗时: {} s",time);
        }
    }



}
