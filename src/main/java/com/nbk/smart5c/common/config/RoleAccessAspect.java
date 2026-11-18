package com.nbk.smart5c.common.config;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RoleAccessAspect {

//    @Before("@annotation(roleAccess)")
//    public void before(JoinPoint joinPoint, RoleAccess roleAccess) throws AccessDeniedException {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.isAuthenticated()){
//            String currentRole = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
//            if(!Arrays.asList(roleAccess.roles()).contains(currentRole)){
//                throw new AccessDeniedException("Access Denied");
//            }
//        }
//    }
}
