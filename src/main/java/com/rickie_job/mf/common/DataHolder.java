package com.rickie_job.mf.common;

import com.rickie_job.mf.model.domain.User;

/**
 * @Classname DataHolder
 * @Author rickie
 * @Date 2023/3/8 8:20 PM
 */
public class DataHolder {

    private final static ThreadLocal<User> userInfo=new ThreadLocal<>();
    private final static ThreadLocal<String> userToken=new ThreadLocal<>();

    public static void setUserInfo(User user) {
        userInfo.set(user);
    }

    public static void setUserToken(String token) {
        userToken.set(token);
    }

    public static User getUserInfo(){
       return userInfo.get();
    }

    public static String getUserToken(){
       return userToken.get();
    }

    public static void remove(){
        userInfo.remove();
        userToken.remove();
    }


}
