package com.rickie_job.mf.script;

import com.alibaba.excel.EasyExcel;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Classname ImportXingQiuUser
 * @Author rickie
 * @Date 2023/1/3 11:02 PM
 */
public class ImportXingQiuUser {
    public static void main(String[] args) {
        String fileName = "/Users/rickie/Matching_friends/MF-backend/src/main/resources/prodExcel.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> userInfoList
                = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        System.out.println("总数 = " + userInfoList.size());
        Map<String, List<XingQiuTableUserInfo>> listMap =
                userInfoList.stream()
                        .filter(userInfo -> StringUtils.isNotEmpty(userInfo.getUsername()))
                        .collect(Collectors.groupingBy(XingQiuTableUserInfo::getUsername));
        System.out.println("不重复昵称 = " + listMap.keySet().size());
    }
}
