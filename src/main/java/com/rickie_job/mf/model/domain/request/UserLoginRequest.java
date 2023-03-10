package com.rickie_job.mf.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Classname UserRegisterRequest
 * @Author rickie
 * @Date 2022/11/23 4:24 PM
 */

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = -1952716151031065562L;

    private String userAccount;

    private String userPassword;



}
