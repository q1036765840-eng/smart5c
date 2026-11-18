package com.nbk.smart5c.common.utils;

import com.google.gson.JsonObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;


public class HttpUtil {

    public static final CloseableHttpClient httpclient = HttpClients.createDefault();

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @return
     */
    public static String sendGet(String url) {
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpget);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送HttpGet带参请求
     *
     * @param url
     * @param header
     * @return
     */
    public static String sendGet(String url, Map<String, String> header) {
        HttpGet httpGet = new HttpGet(url);
        //设置头部
        for (Map.Entry entry : header.entrySet()) {

            httpGet.setHeader(entry.getKey().toString(), entry.getValue().toString());
        }
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httpGet);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        String result = null;
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity);
            }
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送不带参数的HttpPost请求
     *
     * @param url
     * @return
     */
    public static String sendPost(String url) {
        HttpPost httppost = new HttpPost(url);
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity = response.getEntity();
        String result = null;
        try {
            result = EntityUtils.toString(entity);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String sendPost(String url, Map<String, Object> map, Map<String, String> headers) {
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("Content-Type", "application/json");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            httppost.setHeader(header.getKey(), header.getValue());

        }
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry entry : map.entrySet()) {
            jsonObject.addProperty(entry.getKey().toString(), Objects.isNull(entry.getValue()) ? null : entry.getValue().toString());
        }   // 以上循环操作是将 Map对象转化成 JsonObject对象
        StringEntity entity = new StringEntity(jsonObject.toString(), Consts.UTF_8);
        httppost.setEntity(entity);     //设置请求体
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();
        String Result = null;
        try {
            Result = EntityUtils.toString(entity1);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return Result;
    }

    /**
     * 发送FormData格式的数据
     *
     * @param url
     * @param data
     * @param headers
     * @return
     */
    public static String sendPostFormData(String url, Map<String, Object> data, Map<String, String> headers) {
        HttpPost httppost = new HttpPost(url);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            httppost.setHeader(header.getKey(), header.getValue());

        }
        // 构建请求体
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        for (Map.Entry<String, Object> datum : data.entrySet()) {
            Object value = datum.getValue();
            if (value instanceof String) {
                builder.addTextBody(datum.getKey(), (String) value, ContentType.TEXT_PLAIN);
            }
            if (value instanceof File) {
                File file = (File) value;
                builder.addBinaryBody(datum.getKey(), file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
            }
        }
        //设置请求体
        httppost.setEntity(builder.build());
        CloseableHttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity entity1 = response.getEntity();
        String Result = null;
        try {
            Result = EntityUtils.toString(entity1);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
        return Result;
    }
}