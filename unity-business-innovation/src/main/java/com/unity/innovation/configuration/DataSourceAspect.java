
package com.unity.innovation.configuration;

//import com.unity.common.base.config.datasource.BaseDataSourceAspect;
import com.unity.common.base.config.datasource.BaseDataSourceAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


/**
 * 读写分离切面，动态切换数据源。
 *
 */
@Component
@Aspect
public class DataSourceAspect extends BaseDataSourceAspect {

    @Override
    @Pointcut("execution(* com.unity.innovation.service.*.*(..))")
    public void allService() {
    }

}
