package com.rickie_job.mf.common;

/**
 * @Classname ResultUtils
 * @Author rickie
 * @Date 2022/11/30 2:42 PM
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
//        return new BaseResponse<>(errorCode.getCode(),null,errorCode.getMessage(), errorCode.getDescrption());
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message, String description){
        return new BaseResponse(errorCode.getCode(), null, message, description);
    }

    /**
     * 失败
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String description){
        return new BaseResponse(errorCode.getCode(), errorCode.getMessage(), description);
    }

    /**
     * 失败
     * @param code
     * @param message
     * @param description
     * @return
     */
    public static BaseResponse error(int code, String message, String description){
        return new BaseResponse(code, null,message , description);
    }
}
