package com.rickie_job.mf.service;

import com.rickie_job.mf.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.rickie_job.mf.constant.UserConstant.ADMIN_ROLE;
import static com.rickie_job.mf.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author rickie
 * @description 针对表【user(用户)】的数据库操作Service 是来编写用户的服务的
 * 方法可能被复用 或者其他服务调用的时候 写在service中
 * @createDate 2022-11-22 16:56:19
 */
public interface UserService extends IService<User> {


    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @prarm planetCode 星球编号
     * @return 新用户id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登陆
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param request
     * @return 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     */
    List<User> searchUsersByTags(List<String> tagNameList);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    /**
     * 获取当前登录用户信息
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员 重载
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

}
