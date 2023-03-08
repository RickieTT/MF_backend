package com.rickie_job.mf.common;

import com.rickie_job.mf.model.domain.User;

/**
 * @Classname DataHolder
 * @Author rickie
 * @Date 2023/3/8 8:20 PM
 */
public class DataHolder {

    private final static ThreadLocal<User> userInfo=new ThreadLocal<>();

    public static void setUserInfo(User user) {
        userInfo.set(user);
    }

    public static User getUserInfo(){
       return userInfo.get();
    }

    public static void remove(){
        userInfo.remove();
    }


}
