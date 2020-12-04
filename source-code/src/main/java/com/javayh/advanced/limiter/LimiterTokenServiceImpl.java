package com.javayh.advanced.limiter;

import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author Dylan
 * @version 1.0.0
 * @since 2020-12-03 5:24 PM
 */
@Service
public class LimiterTokenServiceImpl implements LimiterTokenService{

    /**
     * 生成token
     */
    @Override
    public String createLimiterToken() {
        // 生成token
        String token = UUID.randomUUID().toString();
        addTokenTime(token);
        return token;
    }

    /**
     * 校验当前token是否被锁定 true: 锁定  | false: 未锁定
     * @param token
     * @return
     */
    @Override
    public boolean checkToken(String token ,Long time) {
        Long l = System.currentTimeMillis();
        Long cache = LocalCacheMap.getCache(token);
        if(Objects.nonNull(cache)){
            if (l - cache > time) {
                return true;
            }
            // 删除
            LocalCacheMap.remove(token);
            return false;
        }
        LocalCacheMap.remove(token);
        return false;
    }

    /**
     * 添加时间
     * 这一步是为模拟过期时间
     * @param token key
     */
    @Override
    public void addTokenTime(String token) {
        // 放入第一次的请求时间
        long millis = System.currentTimeMillis();
        LocalCacheMap.setCache(token,millis);
    }
}
