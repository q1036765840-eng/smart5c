package com.nbk.smart5c.client;

import com.dtflys.forest.annotation.*;
import com.nbk.smart5c.pojo.bo.*;

/**
 * @desc ai请求参数
 */
@BaseRequest(
        baseURL = "http://127.0.0.1:5000",
        timeout = 300000,
        connectTimeout = 10000,
        readTimeout = 300000
)
public interface AiClient {

    @Post("/api/analysis/image/valid")
    @Retry(maxRetryCount = "2")
    ImageValidRespBO imageValid(@JSONBody AiRequestBO aiRequestBO);

    @Post("/api/analysis/infer")
    @Retry(maxRetryCount = "2")
    AiAnalysisRespBO analysis(@JSONBody AiRequestBO aiRequestBO);

    @Post("/api/analysis/image/valid2")
    @Retry(maxRetryCount = "0")
    void imageValid2(@JSONBody AiRequest2BO aiRequestBO);

    @Post("/api/analysis/infer2")
    @Retry(maxRetryCount = "1")
    AiAnalysisFolderRespBO analysis2(@JSONBody AiRequest2BO aiRequestBO);

    @Post(value = "/api/analysis/image/progress")
    @Retry(maxRetryCount = "2")
    ImageValidateProgressBO imageValidProgress(@JSONBody AiRequest2BO aiRequestBO);
}
