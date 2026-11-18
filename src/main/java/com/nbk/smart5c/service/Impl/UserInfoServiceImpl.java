package com.nbk.smart5c.service.Impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nbk.smart5c.common.annotations.AroundLog;
import com.nbk.smart5c.common.enums.LockStatusEnum;
import com.nbk.smart5c.common.utils.*;
import com.nbk.smart5c.entity.*;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.mapper.*;
import com.nbk.smart5c.pojo.bo.UserInfoSaveBO;
import com.nbk.smart5c.pojo.bo.UserInfoUpdateBO;
import com.nbk.smart5c.pojo.bo.UserUpdatePwdBO;
import com.nbk.smart5c.pojo.qo.UserInfoQO;
import com.nbk.smart5c.pojo.qo.UserLoginParam;
import com.nbk.smart5c.pojo.vo.UserInfoVO;
import com.nbk.smart5c.pojo.vo.UserRoleInfoVO;
import com.nbk.smart5c.service.DepartmentService;
import com.nbk.smart5c.service.MenuService;
import com.nbk.smart5c.service.UserInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiaxuebin@nuobikan.com
 * @apiNote 用户信息表业务逻辑层
 * @since 2026-01-27 15:25:38
 */
@Service
@Primary
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService<UserInfoMapper, UserInfo> {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleInfoMapper roleInfoMapper;

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Autowired
    private MenuService<MenuMapper, Menu> menuService;

    @Autowired
    private DepartmentService<DepartmentMapper, Department> departmentService;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private ApiUtils apiUtils;

    private final static String FILE_NAME = "nbk_user_info(用户信息表)";

    private final static List<String> TITLE = Arrays.asList(
            "id(主键)",
            "user_name(用户名)",
            "password(密码)",
            "account_no(账号)",
            "telephone(电话)",
            "avatar(头像链接)",
            "department_id(部门id)",
            "login_times(登录错误次数（最大次数）)",
            "email(邮箱地址)",
            "lock_status(锁定状态（0：未锁定，1：锁定）)",
            "created_user(创建人)",
            "created_time(创建时间)",
            "updated_user(修改人)",
            "updated_time(更新时间)"
    );

    /**
     * 新增数据
     *
     * @param info 新增数据信息
     */
    @Override
    public Boolean save(UserInfoSaveBO info) {
        UserInfo entity = new UserInfo();
        BeanUtils.copyProperties(info, entity);
        entity.setPassword(MD5.md5PlusSalt("000000"));
        super.save(entity);
        UserRole userRole = new UserRole();
        RoleInfo roleInfo = roleInfoMapper.getRoleByCode("COMMON_USER");
        userRole.setUserId(entity.getId()).setRoleId(roleInfo.getId());
        return userRoleMapper.insert(userRole) > 0;
    }

    /**
     * 根据id查询数据
     *
     * @param id 数据id
     */
    @Override
    public UserInfo queryById(String id) {
        return super.getById(id);
    }

    /**
     * 根据条件分页查询数据
     *
     * @param query 包含分页的查询条件
     */
    @Override
    public IPage<UserInfo> queryAndPaging(UserInfoQO query) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(query.getUserName()), "user_name", query.getUserName());
        queryWrapper.eq(StringUtils.isNotEmpty(query.getAccountNo()), "account_no", query.getAccountNo());
        queryWrapper.eq(StringUtils.isNotEmpty(query.getTelephone()), "telephone", query.getTelephone());
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(query.getLockStatus()), UserInfo::getLockStatus, query.getLockStatus());
        queryWrapper.lambda().eq(ObjectUtil.isNotEmpty(query.getDepartmentId()), UserInfo::getDepartmentId, query.getDepartmentId());
        Page<UserInfo> userInfoPage = super.page(new Page<>(query.getPageNo(), query.getPageSize()), queryWrapper);
        List<UserInfo> userInfoList = userInfoPage.getRecords();
        if (CollUtil.isNotEmpty(userInfoList)) {
            userInfoList.forEach(u -> {
                Department department = departmentService.getById(u.getDepartmentId());
                u.setDepartmentName(null == department ? "" : department.getDepartmentName());
                List<UserRoleInfoVO> userRoleList = userRoleMapper.getUserRoleInfo(u.getId());
                u.setRoleName(CollUtil.isEmpty(userRoleList) ? "" : userRoleList.get(0).getRoleName());
                u.setRoleId(CollUtil.isEmpty(userRoleList) ? null : userRoleList.get(0).getRoleId());
            });
        }
        return userInfoPage;
    }

    /**
     * 根据条件查询所有的数据
     *
     * @param query 查询条件
     * @return 满足条件的所有数据
     */
    @Override
    public List<UserInfoVO> queryAll(UserInfoQO query) {
        return super.baseMapper.queryAll(query);
    }

    /**
     * 根据id修改数据
     *
     * @param info 修改数据信息
     */
    @Override
    public Boolean updateById(UserInfoUpdateBO info) {
        UserInfo entity = new UserInfo();
        BeanUtils.copyProperties(info, entity);
        if (null != info.getRoleId()) {
            userRoleMapper.deleteByUserId(info.getId());
            UserRole userRole = new UserRole();
            userRole.setRoleId(info.getRoleId()).setUserId(info.getId());
            userRoleMapper.insert(userRole);
        }
        return super.updateById(entity);
    }

    /**
     * 数据导出
     *
     * @param query 查询条件
     */
    @Override
    public void export(UserInfoQO query, HttpServletResponse response) {
        // 查询用户数据
        List<UserInfoVO> result = baseMapper.queryAll(query);
        List<List<String>> data = new ArrayList<>(result.size());
        for (UserInfoVO item : result) {
            data.add(Arrays.asList(
                    item.getId() + "",
                    item.getUserName(),
                    item.getPassword(),
                    item.getAccountNo(),
                    item.getTelephone(),
                    item.getAvatar(),
                    item.getDepartmentId() + "",
                    item.getLoginTimes() + "",
                    item.getEmail(),
                    item.getLockStatus() + "",
                    item.getCreatedUser(),
                    Objects.isNull(item.getCreatedTime()) ? "" : item.getCreatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    item.getUpdatedUser(),
                    Objects.isNull(item.getUpdatedTime()) ? "" : item.getUpdatedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            ));
        }
    }

    /**
     * 模板导出
     */
    @Override
    public void exportTemplate(HttpServletResponse response) {

    }

    /**
     * 数据导入
     */
    @Override
    public void analysis(MultipartFile file) {

    }

    @Override
    @AroundLog
    public ResultUtil login(UserLoginParam loginParam) {
        if (StringUtils.isEmpty(loginParam.getUsername())) {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "用户登录账号不能为空!");
        }
        if (StringUtils.isEmpty(loginParam.getPassword())) {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "用户登录密码不能为空!");
        }
        LambdaQueryWrapper<UserInfo> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(UserInfo::getAccountNo, loginParam.getUsername()).or().eq(UserInfo::getTelephone, loginParam.getUsername());
        UserInfo userInfo = userInfoMapper.selectOne(userWrapper);
        if (ObjectUtil.isNull(userInfo)) {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "用户账号或密码不正确!");
        }
        if (LockStatusEnum.LOCKED.getCode().equals(userInfo.getLockStatus())) {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "用户账号已被锁定，请联系管理员解锁!");
        }
        if (!MD5.md5PlusSalt(loginParam.getPassword()).equals(userInfo.getPassword())) {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "用户账号或密码不正确!");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userInfo.getId());
        claims.put("userName", userInfo.getUserName());
        claims.put("accountNo", userInfo.getAccountNo());
        claims.put("telephone", userInfo.getTelephone());
        claims.put("deptId", userInfo.getDepartmentId());
        List<UserRoleInfoVO> userRoles = userRoleMapper.getUserRoleInfo(userInfo.getId());
        List<String> roleCodeList;
        if (CollUtil.isNotEmpty(userRoles)) {
            roleCodeList = userRoles.stream().map(UserRoleInfoVO::getRoleCode).collect(Collectors.toList());
        } else {
            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(), "您还未分配角色，请联系管理员分配好角色后再登录");
        }
        claims.put("roleCode", roleCodeList.get(0));
        Boolean isAdmin = roleCodeList.get(0).equals("SUPER_ADMIN") || roleCodeList.get(0).equals("ADMIN");
        claims.put("isAdmin", isAdmin);
        String token = JwtTokenUtil.getToken(claims);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("token", tokenHead + " " + token);
        tokenMap.put("roleList", userRoles);
//        QueryWrapper<RoleMenu> permissionInfoQueryWrapper = new QueryWrapper<>();
//        permissionInfoQueryWrapper.lambda().eq(RoleMenu::getRoleId,roleIdList.get(0));
//        List<RoleMenu> permissionInfoList = roleMenuMapper.selectList(permissionInfoQueryWrapper);
//        if (!roleCodeList.contains("ADMIN") && CollUtil.isEmpty(permissionInfoList)) {
//            return ResultUtil.error(ResultCode.LOGIN_ERROR.getCode(),"还未分配菜单权限，请联系管理员进行分配");
//        } else {
//            List<Long> menuIdList;
//            if(roleCodeList.contains("ADMIN") && CollUtil.isEmpty(permissionInfoList)){
//                menuIdList = menuService.getTopMenuId();
//            }else{
//                menuIdList = permissionInfoList.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
//            }
//            List<Menu> menuInfoList = menuService.getMenuInfoTree(menuIdList);
//            tokenMap.put("menuInfoList", menuInfoList);
//        }
        tokenMap.put("userInfo", userInfo);
        userInfo.setLoginTimes(0).setLastLogin(LocalDateTime.now());
        userInfoMapper.updateById(userInfo);
        return ResultUtil.OK("登录成功", tokenMap);
    }

    @Override
    public String refreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        UserInfo userInfo = super.getById(userId);
        claims.put("userId", userInfo.getId());
        claims.put("userName", userInfo.getUserName());
        claims.put("accountNo", userInfo.getAccountNo());
        claims.put("telephone", userInfo.getTelephone());
        claims.put("deptId", userInfo.getDepartmentId());
        List<UserRoleInfoVO> userRoles = userRoleMapper.getUserRoleInfo(userInfo.getId());
        String roleCode = "COMMON_USER";
        if (CollUtil.isNotEmpty(userRoles)) {
            roleCode = userRoles.get(0).getRoleCode();
        }
        claims.put("roleCode", roleCode);
        return tokenHead + " " + JwtTokenUtil.getToken(claims);
    }

    @Override
    public Boolean updatePassword(UserUpdatePwdBO userUpdatePwdBO) throws ServiceException {
        if (ObjectUtil.isEmpty(userUpdatePwdBO.getUserId())) {
            throw new ServiceException("用户id不能为空!");
        }
        if (!apiUtils.currentUserId().equals(userUpdatePwdBO.getUserId())) {
            throw new ServiceException("修改密码用户与登录用户不是同一个人!");
        }
        UserInfo userInfo = userInfoMapper.selectById(userUpdatePwdBO.getUserId());
        if (null == userInfo) {
            throw new ServiceException("未找到对应用户!");
        }
        String encryptOldPwd = userUpdatePwdBO.getOldPassword();
        if (!MD5.md5PlusSalt(encryptOldPwd).equals(userInfo.getPassword())) {
            throw new ServiceException("旧密码不正确!");
        }
        if (StringUtils.isEmpty(userUpdatePwdBO.getNewPassword())) {
            throw new ServiceException("新密码不能为空!");
        }
        if (StringUtils.isEmpty(userUpdatePwdBO.getAffirmPassword())) {
            throw new ServiceException("确认密码不能为空!");
        }
        if (!userUpdatePwdBO.getNewPassword().equals(userUpdatePwdBO.getAffirmPassword())) {
            throw new ServiceException("确认密码不一致!");
        }
        String encryptNewPwd = userUpdatePwdBO.getNewPassword();
        userInfo.setPassword(MD5.md5PlusSalt(encryptNewPwd));
        userInfoMapper.updateById(userInfo);
        return true;
    }
}
