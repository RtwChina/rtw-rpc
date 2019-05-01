package com.rtw.myrpccore.start.param;

import lombok.Data;

/**
 * 启动入参
 * @author rtw
 * @since 2019-04-29
 */
@Data
public class StartUpParam {
    private String ip;
    private int port;

    public StartUpParam(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public StartUpParam() {
    }
}
