package com.rickie_job.mf.config;

import com.google.gson.Gson;
import com.rickie_job.mf.common.DataHolder;
import com.rickie_job.mf.common.ErrorCode;
import com.rickie_job.mf.constant.AuthConstant;
import com.rickie_job.mf.exception.BusinessException;
import com.rickie_job.mf.model.domain.User;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @Author : Colin Xu
 * @Date : 2023/3/8 21:31
 * @Desc : 认证拦截器类
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 访问控制器方法前执行
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 获取请求头中的token
        String mfToken = null;
        User user = null;

        if (Objects.equals(request.getMethod(), "OPTIONS")) {
            return true;
        }

        // 跳过登录接口
        String requestURI = request.getRequestURI();
        for (String path : AuthConstant.EXCLUDE_PATH) {
            if (requestURI.contains(path)) {
                return true;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AuthConstant.MF_AUTH.equals(cookie.getName())) {
                    mfToken = cookie.getValue();
                }
            }
        }

        if (mfToken != null) {
            Object o = redisTemplate.opsForValue().get(mfToken);
            if (o != null) {
                Gson gson = new Gson();
                user = gson.fromJson(o.toString(), User.class);
                DataHolder.setUserInfo(user);
                DataHolder.setUserToken(mfToken);
            }
        } else {
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
        return true;
    }

    /**
     * 访问控制器方法后执行
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    /**
     * postHandle方法执行完成后执行，一般用于释放资源
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        DataHolder.remove();
    }
}

