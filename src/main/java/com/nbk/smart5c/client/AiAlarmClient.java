package com.nbk.smart5c.client;

import cn.hutool.json.JSONObject;
import com.dtflys.forest.annotation.*;
import com.nbk.smart5c.pojo.bo.*;
import com.nbk.smart5c.pojo.bo.AiAlarmBO;

/**
 * @desc ai请求参数
 */
@BaseRequest(
        baseURL = "http://192.168.5.67:8888",
        timeout = 300000,
        connectTimeout = 10000,
        readTimeout = 300000
)
public interface AiAlarmClient {

    @Post(value = "/alarm")
    @Retry(maxRetryCount = "2")
    JSONObject aiAlarm(@JSONBody AiAlarmBO aiRequestBO);

}
