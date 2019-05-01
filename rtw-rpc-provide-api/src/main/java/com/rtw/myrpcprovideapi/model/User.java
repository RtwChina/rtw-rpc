package com.rtw.myrpcprovideapi.model;

import lombok.Data;

/**
 * @author rtw
 * @since 2019-04-29
 */
@Data
public class User {
    private Integer age;
    private String name;

    public User(Integer age, String name) {
        this.age = age;
        this.name = name;
    }
}
