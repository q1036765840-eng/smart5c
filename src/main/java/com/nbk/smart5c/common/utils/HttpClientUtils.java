package com.nbk.smart5c.common.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @ClassName: HttpClientUtils
 * @Description: HttpClient工具类(这里用一句话描述这个类的作用)
 */
@Slf4j
public class HttpClientUtils {
    private static final int MAX_TOTAL_CONN = 120;
    private static final int MAX_CONN_PER_HOST = 50;
    private static final int SOCKET_TIMEOUT = 10000;
    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int CONNECTION_MANAGER_TIMEOUT = 10000;


    private static CloseableHttpClient httpclient;
    private static PoolingHttpClientConnectionManager connMrg;
    // 默认字符集
    private static String encoding = "UTF-8";

    /**
     * 设置定时任务清理连接
     */
    private static final ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2);

    static {
        init();
        destroyByJvmExit();
    }

    private static void destroyByJvmExit() {
        scheduledService.execute(() -> {
            try {
                httpclient.close();
            } catch (IOException e) {
                //
            }
        });
        scheduledService.shutdown();
//        Runtime.getRuntime().addShutdownHook(scheduledService);
    }

    private static void init() {
        connMrg = new PoolingHttpClientConnectionManager();
        connMrg.setMaxTotal(MAX_TOTAL_CONN); // 最大连接数
        connMrg.setDefaultMaxPerRoute(MAX_CONN_PER_HOST);//每个路由基础的连接

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)//设置连接超时时间，单位毫秒。
                .setSocketTimeout(SOCKET_TIMEOUT)//请求获取数据的超时时间，单位毫秒
                .setConnectionRequestTimeout(CONNECTION_MANAGER_TIMEOUT)//设置从连接池获取连接超时时间，单位毫秒
                .build();
        httpclient = HttpClients.custom()
                .setConnectionManager(connMrg)
                .setDefaultRequestConfig(defaultRequestConfig)
                .build();
    }

    /**
     * @param url 请求地址
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求)
     */
    public static String sendDelete(String url, String param) {
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpDelete httpDelete = new HttpDelete();
        try {
            // 设置请求地址
            httpDelete.setURI(new URI(url + "?" + param));
            // 发送请求,返回响应对象
            CloseableHttpResponse response = client.execute(httpDelete);
            return parseData(response);

        } catch (Exception e) {
            log.error("发送post请求失败", e);
        } finally {
            httpDelete.releaseConnection();
        }
        return null;
    }

    /**
     * @param url      请求地址
     * @param headers  请求头
     * @param data     请求实体
     * @param encoding 字符集
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求)
     */
    public static String sendPost(String url, Map<String, String> headers, JSONObject data, String encoding) {
        log.info("进入post请求方法..." + encoding);
        log.info("请求入参：headers=" + JSON.toJSONString(headers));
        log.info("请求入参：URL= " + url);
        log.info("请求入参：data=" + JSON.toJSONString(data));
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost();
        try {
            // 设置请求地址
            httpPost.setURI(new URI(url));
            // 设置请求头
            if (headers != null) {
                Header[] allHeader = new BasicHeader[headers.size()];
                int i = 0;
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    allHeader[i] = new BasicHeader(entry.getKey(), entry.getValue());
                    i++;
                }
                httpPost.setHeaders(allHeader);
            }
            // 设置实体
            httpPost.setEntity(new StringEntity(JSON.toJSONString(data), encoding));
            // 发送请求,返回响应对象
            CloseableHttpResponse response = client.execute(httpPost);
            return parseData(response);

        } catch (Exception e) {
            log.error("发送post请求失败", e);
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }

    /**
     * @param url      请求地址
     * @param headers  请求头
     * @param data     请求实体
     * @param encoding 字符集
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求)
     */
    public static String sendPut(String url, Map<String, String> headers, JSONObject data, String encoding) {
        log.info("进入put请求方法..." + encoding);
        log.info("请求入参：headers=" + JSON.toJSONString(headers));
        log.info("请求入参：URL= " + url);
        log.info("请求入参：data=" + JSON.toJSONString(data));
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpPut httpPut = new HttpPut();
        try {
            // 设置请求地址
            httpPut.setURI(new URI(url));
            // 设置请求头
            if (headers != null) {
                Header[] allHeader = new BasicHeader[headers.size()];
                int i = 0;
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    allHeader[i] = new BasicHeader(entry.getKey(), entry.getValue());
                    i++;
                }
                httpPut.setHeaders(allHeader);
            }
            // 设置实体
            httpPut.setEntity(new StringEntity(JSON.toJSONString(data), encoding));
            // 发送请求,返回响应对象
            CloseableHttpResponse response = client.execute(httpPut);
            return parseData(response);
        } catch (Exception e) {
            log.error("发送put请求失败", e);
        } finally {
            httpPut.releaseConnection();
        }
        return null;
    }

    /**
     * @param url      请求地址
     * @param headers  请求头
     * @param data     请求实体
     * @param encoding 字符集
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求)
     */
    public static String sendPostByArr(String url, Map<String, String> headers, String data, String encoding) {
        log.info("进入post请求方法..." + encoding);
        log.info("请求入参：headers=" + JSON.toJSONString(headers));
        log.info("请求入参：URL= " + url);
//        log.info("请求入参：data=" + JSON.toJSONString(data));
        headers.put("content-type", "application/json");
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost();
        try {
            // 设置请求地址
            httpPost.setURI(new URI(url));
            // 设置请求头
            if (headers != null) {
                Header[] allHeader = new BasicHeader[headers.size()];
                int i = 0;
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    allHeader[i] = new BasicHeader(entry.getKey(), entry.getValue());
                    i++;
                }
                httpPost.setHeaders(allHeader);
            }
            // 设置实体
            httpPost.setEntity(new StringEntity(data, encoding));
            // 发送请求,返回响应对象
            CloseableHttpResponse response = client.execute(httpPost);
            return parseData(response);

        } catch (Exception e) {
            log.error("发送post请求失败", e);
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }

    /**
     * @param url  请求地址
     * @param data 请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用json格式 ， 默认使用UTF - 8编码)
     */
    public static String sendPost(String url, JSONObject data) {
        // 设置默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");

        return sendPost(url, headers, data, encoding);
    }

    /**
     * @param url    请求地址
     * @param params 请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用json格式 ， 默认使用UTF - 8编码)
     */
    public static String sendPost(String url, Map<String, Object> params) {
        // 设置默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        // 将map转成json
        JSONObject data = JSONUtil.parseObj(JSON.toJSONString(params));
        return sendPost(url, headers, data, encoding);
    }

    /**
     * @param url     请求地址
     * @param jsonStr 请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用json格式 ， 默认使用UTF - 8编码)
     */
    public static String sendPost(String url, String jsonStr) {
        // 设置默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        // 将map转成json
        JSONObject data = JSONUtil.parseObj(jsonStr);
        return sendPost(url, headers, data, encoding);
    }

    /**
     * @param url     请求地址
     * @param jsonStr 请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用json格式 ， 默认使用UTF - 8编码)
     */
    public static String sendPostByJsonArray(String url, String jsonStr) {
        // 设置默认请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        return sendPostByArr(url, headers, jsonStr, encoding);
    }

    /**
     * @param url     请求地址
     * @param headers 请求头
     * @param data    请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description: TODO(发送post请求 ， 请求数据默认使用UTF - 8编码)
     */
    public static String sendPost(String url, Map<String, String> headers, JSONObject data) {
        return sendPost(url, headers, data, encoding);
    }

    /**
     * @param url     请求地址
     * @param headers 请求头
     * @param params  请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description:(发送post请求，请求数据默认使用UTF-8编码)
     */
    public static String sendPost(String url, Map<String, String> headers, Map<String, String> params) {
        // 将map转成json
        JSONObject data = JSONUtil.parseObj(JSON.toJSONString(params));
        return sendPost(url, headers, data, encoding);
    }

    /**
     * @param url      请求地址
     * @param params   请求参数
     * @param encoding 编码
     * @return String
     * @throws
     * @Title: sendGet
     * @Description: TODO(发送get请求)
     */
    public static String sendGet(String url, Map<String, Object> params, String encoding) {
        log.info("进入get请求方法...");
        log.info("请求入参：URL= " + url);
        log.info("请求入参：params=" + JSON.toJSONString(params));
        // 创建client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpGet
        HttpGet httpGet = new HttpGet();
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            // 封装参数
            if (params != null) {
                for (String key : params.keySet()) {
                    builder.addParameter(key, params.get(key).toString());
                }
            }
            URI uri = builder.build();
            log.info("请求地址：" + uri);
            // 设置请求地址
            httpGet.setURI(uri);
            // 发送请求，返回响应对象
            CloseableHttpResponse response = client.execute(httpGet);
            return parseData(response);
        } catch (Exception e) {
            log.error("发送get请求失败", e);
        } finally {
            httpGet.releaseConnection();
        }
        return null;
    }

    /**
     * @param url    请求地址
     * @param params 请求参数
     * @return String
     * @throws
     * @Title: sendGet
     * @Description: TODO(发送get请求)
     */
    public static String sendGet(String url, Map<String, Object> params) {
        return sendGet(url, params, encoding);
    }

    /**
     * @param url 请求地址
     * @return String
     * @throws
     * @Title: sendGet
     * @Description: TODO(发送get请求)
     */
    public static String sendGet(String url) {
        return sendGet(url, null, encoding);
    }

    /**
     * 解析response
     *
     * @param response
     * @return
     * @throws Exception
     */
    public static String parseData(CloseableHttpResponse response) throws Exception {
        // 获取响应状态
        int status = response.getStatusLine().getStatusCode();
        if (status == HttpStatus.SC_OK) {
            // 获取响应数据
            return EntityUtils.toString(response.getEntity(), encoding);
        } else {
            log.error("响应失败，状态码：" + status);
        }
        return null;
    }

    /**
     * @param url     请求地址
     * @param headers 请求头
     * @param params  请求实体
     * @return String
     * @throws
     * @Title: sendPost
     * @Description:(发送post请求，请求数据默认使用UTF-8编码)
     */
    public static String sendPut(String url, Map<String, String> headers, Map<String, Object> params) {
        // 将map转成json
        JSONObject data = JSONUtil.parseObj(JSON.toJSONString(params));
        return sendPut(url, headers, data, encoding);
    }

    /**
     * 发送Post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String postWithParamsForString(String url, List<NameValuePair> params) {
        // 创建Client
        CloseableHttpClient client = HttpClients.createDefault();
        // 创建HttpPost对象
        HttpPost httpPost = new HttpPost();

        try {
            // 设置请求地址
            httpPost.setURI(new URI(url));
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            CloseableHttpResponse response = client.execute(httpPost);
            return parseData(response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
        return null;
    }

    /**
     * 获取请求的ip
     * @param request
     * @return
     */
    public static String getClientIP(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        // 常见反向代理/转发头，按优先级顺序尝试
        String[] headerNames = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };
        String ip = null;
        for (String header : headerNames) {
            ip = request.getHeader(header);
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                continue;
            }
            // X-Forwarded-For 可能是 "client, proxy1, proxy2" 形式，取第一个非 unknown
            String[] parts = ip.split(",");
            for (String part : parts) {
                String candidate = part.trim();
                if (StringUtils.isNotBlank(candidate) && !"unknown".equalsIgnoreCase(candidate)) {
                    ip = candidate;
                    break;
                }
            }
            break;
        }
        // 所有代理头都为空，回退到 RemoteAddr
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 本地回环地址统一处理
        if ("0:0:0:0:0:0:0:1".equalsIgnoreCase(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        // 诊断日志：当拿不到真实 IP 时打印所有 Header，便于定位是哪一环没透传
        if ("127.0.0.1".equals(ip) || "unknown".equalsIgnoreCase(ip)) {
            StringBuilder headers = new StringBuilder();
            java.util.Enumeration<String> names = request.getHeaderNames();
            while (names != null && names.hasMoreElements()) {
                String n = names.nextElement();
                headers.append(n).append("=").append(request.getHeader(n)).append("; ");
            }
            log.warn("getClientIP 回退到 [{}]，请求头: {}，RemoteAddr={}", ip, headers, request.getRemoteAddr());
        }
        return StringUtils.isNotBlank(ip) ? ip : "未获取ip";
    }
}
