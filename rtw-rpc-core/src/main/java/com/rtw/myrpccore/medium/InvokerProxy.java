package com.rtw.myrpccore.medium;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.rtw.myrpccore.annotation.RemoteInvoking;
import com.rtw.myrpccore.client.ClientRequest;
import com.rtw.myrpccore.client.NettyClient;
import com.rtw.myrpccore.util.Response;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.stereotype.Component;


/**
 * 对类属性为 @RemoteInvoking 的进行代理
 * @author rtw
 * @since 2019-04-24
 */
@Component
public class InvokerProxy implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Field[] fields = bean.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(RemoteInvoking.class)) {
                field.setAccessible(true);
                final Map<Method,Class> methodClassMap = new HashMap<>();
                this.putMethodClass(methodClassMap, field);
                Enhancer enhancer = new Enhancer();
                enhancer.setInterfaces(new Class[]{field.getType()});
                enhancer.setCallback(new MethodInterceptor() {
                    @Override
                    public Object intercept(Object instance, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                        // 采用netty通过网络服务 调用服务器
                        // 1. 组装客户端请求查询对象参数
                        ClientRequest clientRequest = new ClientRequest();
                        clientRequest.setCommand(methodClassMap.get(method).getName() + "." + method.getName());  // 方法名
                        clientRequest.setContent(args[0]);  // 参数，暂时只适配第一个参数
                        // 2. 发送TCP请求.异步发送
                        Response response = NettyClient.newInstance().send(clientRequest);
                        // 3. 同步等待TCP返回结果，并return
                        return response.getResult();
                    }
                });
                try {
                    // 设置代理对象为bean的属性
                    field.set(bean, enhancer.create());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    /**
     * 对属性的所有方法 和 属性接口类型放入到一个map中
     * @param methodClassMap
     * @param filed
     */
    private void putMethodClass(Map<Method, Class> methodClassMap, Field filed) {
        Method[] methods = filed.getType().getDeclaredMethods();
        for (Method method : methods) {
            methodClassMap.put(method, filed.getType());
        }
    }
}
