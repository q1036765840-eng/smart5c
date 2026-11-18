package com.nbk.smart5c.filter;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.nbk.smart5c.common.enums.LockStatusEnum;
import com.nbk.smart5c.common.utils.JwtTokenUtil;
import com.nbk.smart5c.common.utils.holder.CurrentUserHolder;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.mapper.UserInfoMapper;
import com.nbk.smart5c.properties.NotAuthUrlProperties;
import com.nbk.smart5c.service.UserInfoService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT登录授权过滤器
 * Created by macro on 2018/4/26.
 */
@Slf4j
@Component
@EnableConfigurationProperties(value = NotAuthUrlProperties.class)
public class AuthenticationFilter implements Filter {
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UserInfoService<UserInfoMapper, UserInfo> userInfoService;
    @Autowired
    private NotAuthUrlProperties notAuthUrlProperties;
    @Autowired
    private C5RawFileSyncAuth c5RawFileSyncAuth;


    /**
     * 方法实现说明:不需要授权的路径
     *
     * @param currentUrl 当前请求路径
     * @author:smlz
     * @return:
     * @exception:
     * @date:2019/12/26 13:49
     */
    private boolean shouldSkip(String currentUrl) {
        //路径匹配器(简介SpringMvc拦截器的匹配器)
        //比如/oauth/** 可以匹配/oauth/token    /oauth/check_token等
        PathMatcher pathMatcher = new AntPathMatcher();
        for (String skipPath : notAuthUrlProperties.getShouldSkipUrls()) {
            if (pathMatcher.match(skipPath, currentUrl)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        log.info("currentUrl: {}",request.getRequestURI());
        try {
            if (c5RawFileSyncAuth.isFileSyncAccess(request)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                doNormalAuthFilter(servletRequest, servletResponse, filterChain);
            }
        } finally {
            // 请求线程会被复用，必须清理，避免当前用户串到后续请求
            CurrentUserHolder.clear();
        }
    }

    private void doNormalAuthFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //预检 OPTIONS 请求直接放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter writer = null;
        String currentUrl = request.getRequestURI();
        Map<String, Object> result = new HashMap<>();
        result.put("code", HttpServletResponse.SC_UNAUTHORIZED);
        if (!shouldSkip(currentUrl)) {
            //第一步:解析出我们Authorization的请求头  value为: “bearer XXXXXXXXXXXXXX”
            String authHeader = request.getHeader(tokenHeader);
            if (StringUtils.isEmpty(authHeader)) {
                authHeader = request.getHeader("authorization");
            }
            //第二步:判断Authorization的请求头是否为空
            if (StringUtils.isEmpty(authHeader)) {
                log.error("需要认证的url,请求头为空");
                log.info("fail_url================{}", currentUrl);
                result.put("message", "请求头中的token为空");
                writer = response.getWriter();
                writer.write(JSONUtil.toJsonStr(result));
                writer.close();
                return;
            }
            if (authHeader.startsWith(this.tokenHead)) {
                String authToken = authHeader.substring(this.tokenHead.length() + 1);// The part after "Bearer "
                Claims claims = null;
                try {
                    claims = JwtTokenUtil.getClaimsBody(authToken);
                } catch (Exception e) {
                    log.error("TOKEN过期：{}", e.getLocalizedMessage());
                    //                    throw new ServletException("TOKEN不合法");
                    result.put("message", "TOKEN不合法");
                    writer = response.getWriter();
                    writer.write(JSONUtil.toJsonStr(result));
                    writer.close();
                    return;
                }
                if (null == claims) {
                    log.error("TOKEN非法");
                    //                    throw new ServletException("TOKEN已过期");
                    result.put("message", "TOKEN非法");
                    writer = response.getWriter();
                    writer.write(JSONUtil.toJsonStr(result));
                    writer.close();
                    return;
                }
                if (JwtTokenUtil.tokenIsExpired(claims)) {
                    result.put("message", "TOKEN过期");
                    writer = response.getWriter();
                    writer.write(JSONUtil.toJsonStr(result));
                    writer.close();
                    return;
                }
                String accountNo = (String) claims.get("accountNo");
                QueryWrapper<UserInfo> userInfoWrapper = new QueryWrapper<>();
                userInfoWrapper.eq("account_no", accountNo);
                UserInfo userInfo = userInfoService.getOne(userInfoWrapper);
                // 用户不存在或已锁定时拒绝访问
                if (null == userInfo || LockStatusEnum.LOCKED.getCode().equals(userInfo.getLockStatus())) {
                    log.warn("用户不存在或已被锁定: {}", accountNo);
                    result.put("message", "TOKEN非法或用户状态不可用");
                    writer = response.getWriter();
                    writer.write(JSONUtil.toJsonStr(result));
                    writer.close();
                    return;
                }
                //过期时间小于等于约定时间，需要刷新TOKEN
                Long userId = Long.valueOf(claims.get("userId").toString());
                if (JwtTokenUtil.tokenNeedRefresh(claims)) {
                    try {
                        String token = userInfoService.refreshToken(userId);
                        response.setHeader(tokenHeader, token);
                    } catch (Exception e) {
                        log.error("TOKEN刷新失败:{}", e.getLocalizedMessage(), e);
                    }
                }
                CurrentUserHolder.set(userInfo);
            } else {
                result.put("message", "TOKEN非法");
                writer = response.getWriter();
                writer.write(JSONUtil.toJsonStr(result));
                writer.close();
                return;
            }
        }
        log.debug("currentUrl: {}", currentUrl);
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
