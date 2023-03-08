package com.rickie_job.mf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rickie_job.mf.common.DataHolder;
import com.rickie_job.mf.common.ErrorCode;
import com.rickie_job.mf.exception.BusinessException;
import com.rickie_job.mf.service.UserService;
import com.rickie_job.mf.model.domain.User;
import com.rickie_job.mf.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.rickie_job.mf.constant.UserConstant.ADMIN_ROLE;
import static com.rickie_job.mf.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
* @author rickie
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2022-11-22 16:56:19
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    //DAO层 从数据库中访问数据并且返回成Java对象
    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 盐值 混淆密码
     */
    private static final String SALT = "rickie";




    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1 校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword,planetCode)){
            //todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        if (planetCode.length() > 5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"星球编号过长");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }

        //密码 和 校验密码相同
        if(!userPassword.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码与校验密码不相同");
        }

        //账户不能重复 放在这里是因为这样可以在验证了userAccount是否含有特殊字符之后 再从数据库拿到 节省一次从数据库那数据的资源
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"该账号已被注册");
        }

        //星球编号不能重复 放在这里是因为这样可以在验证了userAccount是否含有特殊字符之后 再从数据库拿到 节省一次从数据库那数据的资源
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode",userAccount);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"该星球账号已被注册");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"无法成功注册");
        }

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1 校验
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            //todo 修改为自定义异常
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号或者密码为空");
        }
        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }
        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于8");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\s]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号含有特殊字符");
        }

        //2.加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null){
            //其实可以设计 根据输入得到的错误的不同 进行设计 在此觉得没必要设计
            log.info("user login failed, userAccount can not match userPassword");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户不存在");
        }

        //3 用户脱敏
        User safetyUser = getSafetyUser(user);


        //4 记录用户的 登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,user);

        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */

    @Override
    public User getSafetyUser(User originUser){
        if (originUser == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"用户不存在");
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());

        return safetyUser;
    }

    /**
     * 根据标签搜索用户 （内存过滤）
     *
     * @param tagNameList
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        //先查询tag是否存在
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

//        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
//        //拼接 and 查询
//        //like '%java%' and like '%C++%'
//        //like 会直接加上% 所以我们不用额外增加了
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like("tags",tagName);
//        }
//        List<User> userList = userMapper.selectList(queryWrapper);
//        //你调用的参数是每一次遍历的对象 则可以用lambda表达式
//        //把数据放到map根据user.getId(条件) 循环 在转换成list
//        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());

        // 1 先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 2 在内存中判断是否含有要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)){
                return false;
            }
            //序列化
            Set<String> tempTagNameSet= gson.fromJson(tagsStr,new TypeToken<Set<String>>(){}.getType());
            tempTagNameSet = Optional.ofNullable(tempTagNameSet).orElse(new HashSet<>());
            //反序列化
//            gson.toJson();
            for (String tagName : tagNameList) {
                if (!tempTagNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        // 没有查到用户 抛出异常
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //如果是管理员 允许更新任意用户
        //如果不是管理员 只允许更新当前(自己的)用户
        if (!isAdmin(loginUser) && userId != loginUser.getId()){
            // 不是自己的信息 直接抛出异常
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 获取当前用户登录信息
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request == null){
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;

    }


    /**
     * 是否为管理员
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        //仅管理员可以查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 是否为管理员 重载方法
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        //仅管理员可以查询
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }


    /**
     * 用户注销
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        String token = DataHolder.getUserToken();
        redisTemplate.delete(token);
        return 1;
    }

    /**
     * 生成token
     * @return
     */
    @Override
    public String generateToken(User user) {
        // 1 生成token
        String token = UUID.randomUUID().toString();
        // 2 把token存到redis中
        Gson gson = new Gson();
        String userJson = gson.toJson(user);
        redisTemplate.opsForValue().set(token, userJson, 30 * 2, TimeUnit.MINUTES);
        // 3 把token返回给客户端
        return token;
    }
}




