package com.rtw.myrpccore.server;

import java.io.Serializable;

/**
 * @author rtw
 * @since 2019-04-15
 */
public class ServerRequest implements Serializable {
    private Long id;
    private Object content;  // 传参
    private String command; // 类 + 方法名

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
