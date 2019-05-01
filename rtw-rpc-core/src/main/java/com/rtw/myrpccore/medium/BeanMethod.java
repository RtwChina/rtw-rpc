package com.rtw.myrpccore.medium;

import java.lang.reflect.Method;

/**
 * @author rtw
 * @since 2019-04-21
 */
public class BeanMethod {
    private Object bean;
    private Method m;

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getM() {
        return m;
    }

    public void setM(Method m) {
        this.m = m;
    }
}
