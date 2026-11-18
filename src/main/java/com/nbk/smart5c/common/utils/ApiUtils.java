package com.nbk.smart5c.common.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import com.nbk.smart5c.common.enums.ClientTypeEnum;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.filter.C5RawFileSyncAuth;
import io.jsonwebtoken.Claims;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * API工具类
 *
 * @author Caratacus
 */
@SuppressWarnings("ALL")
@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ApiUtils {
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Value("${file-sync.access-token}")
    private String outAccessToken;

    // 5C 原始文件同步的匿名访问模式（C5RawFileSyncAuth 会校验占位 token 并绑定默认用户）
    @Lazy
    @Autowired
    private C5RawFileSyncAuth c5RawFileSyncAuth;

    /**
     * 当前请求是否为“5C原始文件同步”的匿名访问
     */
    private UserInfo currentFileSyncUser() {
        HttpServletRequest request = ApplicationUtils.getRequest();
        if (null == request || !c5RawFileSyncAuth.isFileSyncAccess(request)) {
            return null;
        }
        return c5RawFileSyncAuth.getPermitAccessUser();
    }

    /**
     * 目前部门id
     */
    public Integer currentDepartmentId() {
        Integer departmentId = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
            UserInfo fileSyncUser = currentFileSyncUser();
            if (null != fileSyncUser) {
                return null == fileSyncUser.getDepartmentId() ? null : fileSyncUser.getDepartmentId().intValue();
            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            departmentId = (Integer) claims.get("departmentId");
        } catch (ServiceException e) {
            log.error("TOKEN解析出错:{}", e.getMessage(), e);
        }
        return departmentId;
    }

    /**
     * 目前用户id
     */
    public Long currentUserId() {
        Long userId = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
//            UserInfo fileSyncUser = currentFileSyncUser();
//            if (null != fileSyncUser) {
//                return fileSyncUser.getId();
//            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            if(token.equals(outAccessToken)){
                return -1L;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            userId = Long.parseLong(claims.get("userId").toString());
        } catch (ServiceException e) {
            log.error("TOKEN解析出错:{}", e.getMessage(), e);
        }
        return userId;
    }


    /**
     * 目前用户角色编码
     */
    public String currentUserRole() {
        String roleCode = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
            UserInfo fileSyncUser = currentFileSyncUser();
            if (null != fileSyncUser) {
                return c5RawFileSyncAuth.getPermitAccessRoleCode();
            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            roleCode = (String) claims.get("roleCode");
        } catch (ServiceException e) {
            log.error("TOKEN解析出错:{}", e.getMessage(), e);
        }
        return roleCode;
    }

    /**
     * 获取当前用户账号
     */
    public String currentToken() {
        HttpServletRequest request = ApplicationUtils.getRequest();
        if (null == request) {
            return null;
        }
        String token = request.getHeader("Authorization");
        return token;
    }

    /**
     * 获取当前用户账号
     */
    public String currentAccount() {
        String accountNo = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
            UserInfo fileSyncUser = currentFileSyncUser();
            if (null != fileSyncUser) {
                return fileSyncUser.getUserName() + "(" + fileSyncUser.getAccountNo() + ")";
            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            accountNo = claims.get("userName") + "(" + claims.get("accountNo") + ")";
        } catch (ServiceException e) {
            log.error("TOKEN解析出错:{}", e.getMessage(), e);
        }
        return accountNo;
    }

    /**
     * 获取当前用户账号
     */
    public String currentAccountNo() {
        String accountNo = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
            UserInfo fileSyncUser = currentFileSyncUser();
            if (null != fileSyncUser) {
                return fileSyncUser.getAccountNo();
            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            accountNo = claims.get("accountNo").toString();
        } catch (ServiceException e) {
            log.error("TOKEN解析出错:{}", e.getMessage(), e);
        }
        return accountNo;
    }

    /**
     * 获取当前用户账号
     */
    public String currentPassword() throws ServiceException {
        HttpServletRequest request = ApplicationUtils.getRequest();
        String token = request.getHeader("Authorization");
        if (CharSequenceUtil.isBlank(token)) {
            return null;
        }
        token = token.replaceFirst(tokenHead, "");
        Claims claims = JwtTokenUtil.getClaimsBody(token);
        if (null == claims || JwtTokenUtil.tokenIsExpired(claims)) {
            throw new ServiceException("Token已过期，请重新登录");
        }
        String password = (String) claims.get("password");
        return password;
    }

    /**
     * 获取当前用户账号
     */
    public Boolean isAdmin() throws ServiceException {
        HttpServletRequest request = ApplicationUtils.getRequest();
        if (null == request) {
            return null;
        }
        UserInfo fileSyncUser = currentFileSyncUser();
        if (null != fileSyncUser) {
            String roleCode = c5RawFileSyncAuth.getPermitAccessRoleCode();
            return "SUPER_ADMIN".equals(roleCode) || "ADMIN".equals(roleCode);
        }
        String token = request.getHeader("Authorization");
        if (CharSequenceUtil.isBlank(token)) {
            return null;
        }
        token = token.replaceFirst(tokenHead, "");
        Claims claims = JwtTokenUtil.getClaimsBody(token);
        if (null == claims || JwtTokenUtil.tokenIsExpired(claims)) {
            throw new ServiceException("Token已过期，请重新登录");
        }
        return null == claims.get("isAdmin") ? Boolean.FALSE : Boolean.valueOf(claims.get("isAdmin").toString());
    }

    /**
     * 目前用户部门/车间
     */
    public Long currentDeptId() {
        Long deptId = null;
        try {
            HttpServletRequest request = ApplicationUtils.getRequest();
            if (null == request) {
                return null;
            }
            UserInfo fileSyncUser = currentFileSyncUser();
            if (null != fileSyncUser) {
                return fileSyncUser.getDepartmentId();
            }
            String token = request.getHeader("Authorization");
            if (CharSequenceUtil.isBlank(token)) {
                return null;
            }
            token = token.replaceFirst(tokenHead, "");
            Claims claims = JwtTokenUtil.getClaimsBody(token);
            if (null == claims) {
                throw new ServiceException("Token非法");
            }
            deptId = null == claims.get("deptId") ? 0 : Long.parseLong(claims.get("deptId").toString());
        } catch (ServiceException e) {
            log.error("Token解析出错:{}", e.getLocalizedMessage());
        }
        return deptId;
    }
}