package com.rickie_job.mf.script;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class XingQiuTableUserInfo {

    /**
     * 星球编号
     */
    @ExcelProperty("成员编号")
    private String planetCode;

    /**
     * 用户昵称
     */
    @ExcelProperty("成员昵称")
    private String username;


}