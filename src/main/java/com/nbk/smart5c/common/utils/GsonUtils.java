package com.nbk.smart5c.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * json转换工具类
 *
 */
public class GsonUtils {
    private static Gson gson = null;

    private GsonUtils() {
    }

    static {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    // 转换Float、Double类型字段需要设置，不设置生产出现过问题
                    .serializeSpecialFloatingPointValues()
                    .create();
        }
    }

    /**
     * 将对象转换成json格式字符串
     *
     * @param object
     * @return
     */
    public static String objToStr(Object object) {
        return Optional.ofNullable(object)
                .map(obj -> gson.toJson(obj))
                .orElse(null);
    }

    /**
     * 将json字符串转换成对象
     *
     * @param str
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T StrToObj(String str, Class<T> clazz) {
        return gson.fromJson(str, clazz);
    }

    /**
     * json字符串转成List集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> strToList(String json, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (StringUtils.isNotBlank(json)) {
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for (final JsonElement element : array) {
                list.add(gson.fromJson(element, clazz));
            }
        }
        return list;
    }

    /**
     * json字符串转成List集合
     *
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> strToListWithTypeToken(String json, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        if (StringUtils.isNotBlank(json)) {
            list = gson.fromJson(json, new TypeToken<List<T>>() {
            }.getType());
        }
        return list;
    }


    /**
     * json字符串转成List包含Map集合
     *
     * @param json
     * @param <T>
     * @return
     */
    public static <T> List<Map<String, T>> strToListMap(String json) {
        List<Map<String, T>> list = new ArrayList<>();
        if (StringUtils.isNotBlank(json)) {
            list = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
            }.getType());
        }
        return list;
    }

    /**
     * json字符串转成map
     *
     * @param json
     * @param <T>
     * @return
     */
    public static <T> Map<String, T> strToMap(String json) {
        Map<String, T> map = new HashMap<>();
        if (StringUtils.isNotBlank(json)) {
            map = gson.fromJson(json, new TypeToken<List<Map<String, T>>>() {
            }.getType());
        }
        return map;
    }
}