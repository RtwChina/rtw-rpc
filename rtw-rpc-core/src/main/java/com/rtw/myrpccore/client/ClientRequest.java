package com.rtw.myrpccore.client;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author rtw
 * @since 2019-04-14
 */
public class ClientRequest implements Serializable {
    private Long id; //请求唯一ID。
    private Object content; // 传参
    private static final AtomicLong AID = new AtomicLong(1);
    private String command; // 类名 + 方法

    public ClientRequest() {
        this.id = AID.incrementAndGet();
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public static void main(String[] args) {
        ClientRequest clientRequest = new ClientRequest();
        System.out.println(clientRequest.getId());

        ClientRequest clientRequest2 = new ClientRequest();
        System.out.println(clientRequest2.getId());
    }
}
