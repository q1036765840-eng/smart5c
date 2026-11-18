package com.nbk.smart5c.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nbk.smart5c.common.utils.holder.CurrentUserHolder;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.mapper.UserInfoMapper;
import com.nbk.smart5c.mapper.UserRoleMapper;
import com.nbk.smart5c.pojo.vo.UserRoleInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 库尔勒 5C 原始数据同步功能对应的用户认证特殊处理
 *
 * 当系统部署到库尔勒时，不用登录系统。将从库尔勒桌面程序跳转5C页面
 *
 * @author 贾学斌
 * @email jiaxuebin@nuobikan.com
 * @since 2026/3/10
 */
@Slf4j
@Service
public class C5RawFileSyncAuth {

    static final String SUPER_ADMIN_ACCOUNT = "superadmin";
    static final String C5_FILE_SYNC_ACCESS_FLAG = "__is_5c_file_sync_access__";

    @Autowired
    private UserInfoMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Value("${file-sync.enable:false}")
    private boolean enable5cFileSync;

    // 不提供默认值：配置缺失时以空白校验拦截，避免弱默认 token 静默放行
    @Value("${file-sync.access-token:}")
    private String permitAccessToken;

    // 正常访问时(非5C文件同步模式），在http header 中传递登录用户Token的key
    @Value("${jwt.tokenHeader:Authorization}")
    private String authTokenKey;

    // “5C原始文件同步”模式下，默认的访问用户，将延迟加载
    private UserInfo permitAccessUser;

    private UserRoleInfoVO permitAccessRole;

    // 当前请求是否为“5C原始文件同步”模式下的接口请求
    public boolean isFileSyncAccess(HttpServletRequest request) {
        // 1. 前置检查
        if (!enable5cFileSync) {
            return false;
        }
        if (StringUtils.isBlank(permitAccessToken)) {
            log.error("已启用“”，但未配置对应的访问token, 将应用正常的用户token验证");
            return false;
        }

        // 2. 针对当前请求对象，是否已经判断过了
        Boolean fileSyncAccessFlag = (Boolean) request.getAttribute(C5_FILE_SYNC_ACCESS_FLAG);
        if (fileSyncAccessFlag != null && fileSyncAccessFlag) {
            return true;
        }

        // 3. 仅从 http header 中提取“5C原始文件同步”功能的访问特征token值
        //    不再支持 URL 参数传递（pat），避免 token 泄露到访问日志与浏览器历史
        String accessToken = request.getHeader(authTokenKey);

        return StringUtils.isNotBlank(accessToken) && this.checkTokenAndSetUser(request, accessToken);
    }

    // 校验token，并视情况
    private boolean checkTokenAndSetUser(HttpServletRequest request, String accessToken) {
        if (!Objects.equals(accessToken, permitAccessToken)) {
            return false;
        }

        // 由于ApiUtils是通过当前 HttpRequest 来获取当前登录用户的，
        // 这里也将"当前访问"相关参数设置到 request 对象中，不使用 ThreadLocal 来持有
        UserInfo sa = getSuperAdminUser();
        if (sa == null) {
            log.error("系统内置的 {} 超级管理账号未初始化到数据库， “5C文件同步”模式的匿名访问特性无法激活",  SUPER_ADMIN_ACCOUNT);
            return false;
        }
        request.setAttribute(C5_FILE_SYNC_ACCESS_FLAG, Boolean.TRUE);
        // 与正常登录一致：审计字段填充、缺陷审核等依赖 CurrentUserHolder 获取操作人
        CurrentUserHolder.set(sa);
        return true;
    }

    private UserInfo getSuperAdminUser() {
        if (this.permitAccessUser != null) {
            return permitAccessUser;
        }

        synchronized (this) {
            if (this.permitAccessUser != null) {
                return permitAccessUser;
            }
            // 使用系统内置的初始超级管理账号
            LambdaQueryWrapper<UserInfo> query = new LambdaQueryWrapper<>();
            query.eq(UserInfo::getAccountNo, SUPER_ADMIN_ACCOUNT);
            this.permitAccessUser = userMapper.selectOne(query);

            // 用户角色查询，逻辑与登录接口一致
            if (this.permitAccessUser != null) {
                List<UserRoleInfoVO> userRoles = userRoleMapper.getUserRoleInfo(permitAccessUser.getId());
                if (CollectionUtils.isNotEmpty(userRoles)) {
                    this.permitAccessRole = userRoles.get(0);
                }
            }
        }

        return this.permitAccessUser;
    }

    public UserInfo getPermitAccessUser() {
        return this.permitAccessUser;
    }

    public String getPermitAccessRoleCode() {
        return permitAccessRole == null ? null : permitAccessRole.getRoleCode();
    }

}
