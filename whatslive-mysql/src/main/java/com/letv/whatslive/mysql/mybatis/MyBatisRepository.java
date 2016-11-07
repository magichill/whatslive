package com.letv.whatslive.mysql.mybatis;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Title: 标识MyBatis的DAO的注解
 * Desc: 标识MyBatis的DAO,{@link org.mybatis.spring.mapper.MapperScannerConfigurer}根据该注解进行的扫描。
 * User: crespo
 * Company: www.letv.com
 * Date: 13-7-17 上午12:58
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MyBatisRepository {

}
