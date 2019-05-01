package com.rtw.myrpccore.medium;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.annotation.Remote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 初始化中间层
 * @author rtw
 * @since 2019-04-20
 */
@Slf4j
@Component
public class InitialMedium implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean.getClass().isAnnotationPresent(Remote.class)) {
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method md : methods) {
                String key = bean.getClass().getInterfaces()[0].getName() + "." + md.getName();
                BeanMethod beanMethod = new BeanMethod();
                beanMethod.setBean(bean);
                beanMethod.setM(md);
                Media.beanMap.put(key, beanMethod);
            }
            log.info("中间层初始化完成，Media.beanMap={}", JSON.toJSONString(Media.beanMap));
        }
        return bean;
    }
}
