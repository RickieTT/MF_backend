package com.rickie_job.mf.script;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;

import java.util.List;

/**
 * 导入Excel数据
 * @Classname ImportExcel
 * @Author rickie
 * @Date 2023/1/2 11:48 AM
 */
public class ImportExcel {

    public static void main(String[] args) {
        // since: 3.0.0-beta1
        String fileName = "/Users/rickie/Matching_friends/MF-backend/src/main/resources/testExcel.xlsx";
//        readByListener(fileName);
        synchronousRead(fileName);

    }

    /**
     * 监听器读取
     * @param fileName
     */
    public static void readByListener(String fileName){
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        // 这里每次会读取100条数据 然后返回过来 直接调用使用数据就行
        EasyExcel.read(fileName, XingQiuTableUserInfo.class, new TableListener()).sheet().doRead();
    }

    /**
     * 同步读取
     * @param fileName
     * 同步的返回，不推荐使用，如果数据量大会把数据放到内存里面
     */
    public static void synchronousRead(String fileName) {
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 同步读取会自动finish
        List<XingQiuTableUserInfo> totalDataList
                = EasyExcel.read(fileName).head(XingQiuTableUserInfo.class).sheet().doReadSync();
        for (XingQiuTableUserInfo xingQiuTableUserInfo : totalDataList) {
            System.out.println(xingQiuTableUserInfo);
        }
    }

}
