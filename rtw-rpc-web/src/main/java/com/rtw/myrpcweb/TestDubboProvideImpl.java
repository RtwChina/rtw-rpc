package com.rtw.myrpcweb;

import com.alibaba.fastjson.JSON;
import com.rtw.myrpccore.annotation.Remote;
import com.rtw.myrpcprovideapi.TestDubboProvide;
import com.rtw.myrpcprovideapi.model.User;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

/**
 * @author rtw
 * @since 2019-04-29
 */
@Controller
@Slf4j
@Remote
public class TestDubboProvideImpl implements TestDubboProvide {

    @Override
    public String getUserName(User user) {
        log.info("privide,user={}", JSON.toJSONString(user));
        return LocalDateTime.now().toString();
    }
}
