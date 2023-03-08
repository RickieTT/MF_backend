package com.rickie_job.mf.constant;

/**
 * @Author : Colin Xu
 * @Date : 2023/3/8 21:40
 * @Desc :
 */
public class AuthConstant {

    public static final String MF_AUTH = "MF_AUTH";

    public static final Long USER_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7L;

    public static final String[] EXCLUDE_PATH = {"/user/login", "/user/register"};
}
