package com.rtw.myrpccore.util;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author rtw
 * @since 2019-04-29
 */
@AllArgsConstructor
@Getter
public enum ResponseCode implements Serializable {
    // Result
    SUCCESS("成功", 000),
    FAIL("失败", 001),
    TIMEOUT("超时", 002);

    private String description;
    private Integer code;
}
