package com.javayh.advanced.java.config;

import com.javayh.advanced.java.limiter.AutoIdempotent;
import com.javayh.advanced.java.limiter.LimiterTokenService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-03 6:04 PM
 */
@Configuration
public class AutoIdempotentInterceptor implements HandlerInterceptor {
    @Resource
    private LimiterTokenService limiterTokenService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        //被ApiIdempotment标记的扫描
        AutoIdempotent methodAnnotation = method.getAnnotation(AutoIdempotent.class);
        if (methodAnnotation != null) {
            try {
                long time = methodAnnotation.time();
                String token = request.getParameter("token");
                // 幂等性校验, 校验通过则放行, 校验失败则抛出异常, 并通过统一异常处理返回友好提示
                return limiterTokenService.checkToken(token, time);

            } catch (Exception ex) {
                throw ex;
            }
        }
        //必须返回true,否则会被拦截一切请求
        return true;
    }
}
