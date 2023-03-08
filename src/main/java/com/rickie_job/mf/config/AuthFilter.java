package com.rickie_job.mf.config;

/**
 * @Classname AuthFilter
 * @Author rickie
 * @Date 2023/3/8 8:13 PM
 */

import com.rickie_job.mf.common.DataHolder;
import com.rickie_job.mf.model.domain.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@WebFilter(filterName = "AuthFilter",
        urlPatterns = "/*"
)
public class AuthFilter implements Filter {
    /**
     * 过滤器初始化
     * explain:在容器中创建当前过滤器的时候自动调用
     *
     * @param filterConfig
     */

    private static final String MF_AUTH_NAME = "MF_AUTH";

    @Override
    public void init(FilterConfig filterConfig){
        System.out.println("初始化过滤器!");
    }

    /**
     * 过滤器过滤操作
     * explain:过滤的具体操作
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        System.out.println("请求地址:"+request.getRequestURI());

//        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

//        RedisT

        Cookie[] cookies = request.getCookies();
        String mfAuth = "";
        if (cookies != null && cookies.length > 0){
            Optional<Cookie> optionalCookie = Arrays.stream(cookies).filter(c -> MF_AUTH_NAME.equalsIgnoreCase(c.getName())).findFirst();
            if (optionalCookie.isPresent()) mfAuth = optionalCookie.get().getValue();
        }
        if (StringUtils.isNotBlank(mfAuth)){
            // todo change me once redis is ready
            // todo get user info from redis
            User fakeUser = new User();
            DataHolder.setUserInfo(fakeUser);
        }else {
            return ;
        }

        filterChain.doFilter(servletRequest,servletResponse);
//        DataHolder.remove();
    }

    /**
     * 过滤器销毁
     * explain:在容器中销毁当前过滤器的时候自动调用
     */
    @Override
    public void destroy() {
        System.out.println("销毁过滤器!");
    }
}