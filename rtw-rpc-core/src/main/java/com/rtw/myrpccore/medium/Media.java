package com.rtw.myrpccore.medium;

import com.alibaba.fastjson.JSON;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import com.rtw.myrpccore.server.ServerRequest;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * 中间层
 * @author rtw
 * @since 2019-04-21
 */
@Slf4j
public class Media {
    // Remote的beanMap
    public static Map<String, BeanMethod> beanMap = new ConcurrentHashMap<>();

    private static Media media = new Media();

    private Media() {
    }

    // 恶汉单例模式
    public static Media newInstance() {
        return media;
    }

    // 反射处理业务代码
    public Object process(ServerRequest serverRequest) {
        Object result = null;
        try {
            String commend = serverRequest.getCommand();

            BeanMethod beanMethod = Media.beanMap.get(commend);
            if (beanMethod == null) {
                return null;
            }

            Object bean = beanMethod.getBean();
            Method method = beanMethod.getM();
            Class paramType = method.getParameterTypes()[0];

            Object content = serverRequest.getContent();
            Object paramObj ;
            if (paramType.isAssignableFrom(List.class)) {
                // 主要是为了获取泛型
                ParameterizedTypeImpl type = (ParameterizedTypeImpl)method.getGenericParameterTypes()[0];
                Type[] typeActualTypeArguments = type.getActualTypeArguments();
                 paramObj = JSON.parseArray(content.toString(), (Class)typeActualTypeArguments[0]);
            } else {
                paramObj = JSON.parseObject(content.toString(), paramType);
            }

            result = method.invoke(bean, paramObj);
        } catch (IllegalAccessException e) {
            log.error("参数异常：", e);
        } catch (InvocationTargetException e) {
            log.error("参数异常：", e);
        }
        return result;
    }
}
