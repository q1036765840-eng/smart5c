package com.nbk.smart5c.client;

import com.nbk.smart5c.pojo.bo.AiAnalysisFolderRespBO;
import com.nbk.smart5c.pojo.bo.AiRequest2BO;
import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest
public class AiClientTest {

    @Autowired
    private AiClient aiClient;

    @Test
    public void testAnalysis2() {
        AiRequest2BO request = new AiRequest2BO();
        request.setFolder_path("/nbk/app/newsmart5c/data/test")
                .setConfig_type("e")
                .set_delete(true);

        log.info("开始测试 aiClient.analysis2()");
        log.info("请求参数: {}", request);

        try {
            long start = System.currentTimeMillis();
            AiAnalysisFolderRespBO response = aiClient.analysis2(request);
            long cost = System.currentTimeMillis() - start;

            log.info("调用成功！耗时: {}ms", cost);
            log.info("响应: {}", response);

            if (response == null) {
                log.error("警告：响应为null");
            }
        } catch (Exception e) {
            log.error("调用失败", e);
        }
    }
}