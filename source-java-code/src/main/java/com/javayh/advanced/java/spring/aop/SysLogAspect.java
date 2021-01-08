package com.javayh.advanced.java.spring.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author haiyang
 */
@Slf4j
@Aspect
@Configuration
@Order(-1)
public class SysLogAspect {

    private Map<String, AtomicLong> limiter = new ConcurrentHashMap<>(256);

    private final HttpServletRequest request;

    public SysLogAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Around("@annotation(sysLog)")
    public Object getLog(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        Object proceed = null;
        long time = System.currentTimeMillis();
        try {
            proceed = joinPoint.proceed();
            time = System.currentTimeMillis() - time;
            return proceed;
        } catch (Throwable throwable) {
            throw throwable;
        } finally {
            // 方法执行后
            postOperation(joinPoint, time);

        }
    }

    /**
     * <p>
     * 方法执行完之后的操作
     * </p>
     *
     * @param joinPoint
     * @param time
     * @return void
     * @version 1.0.0
     * @author hai ji
     * @since 2020/8/11
     */
    private void postOperation(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //类名
        String className = methodSignature.getDeclaringTypeName() + "." + methodSignature.getName();
        //将类名加 ip 作为 key
        String limitKey = className + getIpAddr();
        SysLog annotation = methodSignature.getMethod().getAnnotation(SysLog.class);
        String value = annotation.value();
        long limit = annotation.limit();
        inputCheckLimit(limit);
        //首先去缓存根据key 取value
        if (limiter.get(limitKey) != null) {
            AtomicLong atomicLong1 = limiter.get(limitKey);
            long li = atomicLong1.get();
            if (li > 0) {
                log.info("当前访问访问次数 : {}", atomicLong1);
                atomicLong1.decrementAndGet();
                limiter.put(limitKey, atomicLong1);
                log.info("剩余访问访问次数 : {}", atomicLong1);
            } else {
                limiter(li, limitKey);
            }
        } else {
            AtomicLong atomicLong = new AtomicLong(limit);
            log.info("当前访问访问次数 : {}", atomicLong.get());
            //减少范文次数
            atomicLong.decrementAndGet();
            limiter.put(limitKey, atomicLong);
            log.info("剩余访问访问次数 : {}", atomicLong.get());
        }
        log.info(value + "执行耗时: {} s", time);
    }

    protected void inputCheckLimit(long limit) {
        if (limit < 0) {
            throw new RuntimeException("Current limiting times must be greater than 0");
        }
    }

    protected void limiter(Long lo, String limitKey) {
        if (lo <= 0) {
            //根据key删除
            limiter.remove(limitKey);
            log.error(getIpAddr() + "  The visit is too frequent. Please try again later");
            throw new RuntimeException("The visit is too frequent. Please try again later");
        }
    }

    private String getIpAddr() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if ("0:0:0:0:0:0:0:1".equalsIgnoreCase(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

}
