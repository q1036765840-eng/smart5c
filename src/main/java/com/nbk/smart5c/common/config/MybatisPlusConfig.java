package com.nbk.smart5c.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.nbk.smart5c.common.utils.ApiUtils;
import com.nbk.smart5c.common.utils.holder.CurrentUserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@Slf4j
public class MybatisPlusConfig implements MetaObjectHandler {

    /**
     * 配置mybatisPlus的分页拦截器
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mpInterceptor() {
        //定义MP拦截器
        MybatisPlusInterceptor mpInterceptor = new MybatisPlusInterceptor();
        //添加具体的分页拦截器
        mpInterceptor.addInnerInterceptor(new PaginationInnerInterceptor());
//        mpInterceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return mpInterceptor;
    }

    @Autowired
    private ApiUtils apiUtils;

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName("createdTime", LocalDateTime.now(), metaObject);
        String user = CurrentUserHolder.get() == null ? "" : CurrentUserHolder.get().getUserName() + "(" + CurrentUserHolder.get().getAccountNo() + ")";
        this.setFieldValByName("createdUser", user, metaObject);
        this.setFieldValByName("updatedUser", user, metaObject);
        this.setFieldValByName("updatedTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updatedTime", LocalDateTime.now(), metaObject);
        String user = CurrentUserHolder.get() == null ? "" : CurrentUserHolder.get().getUserName() + "(" + CurrentUserHolder.get().getAccountNo() + ")";
        this.setFieldValByName("updatedUser", user, metaObject);
    }
}
