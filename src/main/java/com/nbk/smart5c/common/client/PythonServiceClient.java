package com.nbk.smart5c.common.client;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class PythonServiceClient {
    private List<String> pythonServices = Arrays.asList(
            "http://python1:5000",
            "http://python2:5000"
    );

    private Random random = new Random();

    public String callPythonService() {
        // 随机选择一台服务
        String baseUrl = pythonServices.get(random.nextInt(pythonServices.size()));

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(baseUrl + "/api/endpoint", String.class);
    }
}