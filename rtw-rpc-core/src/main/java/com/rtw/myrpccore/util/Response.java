package com.rtw.myrpccore.util;

import java.io.Serializable;
import lombok.Data;

/**
 * Server收到请求后，处理过后返回给Client的返回结果
 * @author rtw
 * @since 2019-04-15
 */
@Data
public class Response implements Serializable {
    private Long id;
    private Object result;
    // @see ResponseCode
    private String errorMessage; // 异常信息
    private Integer code;

    public static Response FailResponse(Long id, ResponseCode responseCode, String errorMessage) {
        Response response = new Response();
        response.setId(id);
        response.setCode(responseCode.getCode());
        response.setErrorMessage(errorMessage);
        return response;
    }

    public static Response SuccessResponse(Long id, ResponseCode responseCode, Object result) {
        Response response = new Response();
        response.setId(id);
        response.setCode(responseCode.getCode());
        response.setResult(result);
        return response;
    }

}
