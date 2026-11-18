package com.nbk.smart5c.common.utils;

import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ObjectCopyTool {
    /**
     * 两对象之间的拷贝(在目标对象中存在的所有set方法，如果在源对象中存在对应的get方法，且源对象的get方法的返回值不为null,就进行拷贝)
     * 仅拷贝方法名及方法返回类型完全一样的属性值
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public static void copyPropertiesNotForce(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Method[] targetMethods = targetClass.getMethods();
        Method[] sourceMethods = sourceClass.getMethods();

        Map<String, Method> methodMap = new HashMap<String, Method>();
        for (Method method : sourceMethods) {
            String methodName = method.getName();
            if (!methodName.equals("getClass") && methodName.startsWith("get") && method
                    .getParameterTypes().length == 0) {
                methodMap.put(methodName, method);
            }
        }
        for (Method method : targetMethods) {
            String methodName = method.getName();
            if (!methodName.equals("getClass") && methodName.startsWith("get") && method
                    .getParameterTypes().length == 0) {
                if (methodMap.containsKey(methodName)) {
                    Method sourceMethod = methodMap.get(methodName);
                    try {
                        if (method.getReturnType().equals(
                                sourceMethod.getReturnType())) {
                            Object returnObj = sourceMethod.invoke(source, (Object[]) null);
                            if (returnObj == null) {
                                continue;
                            }
                            String field = methodName.substring(3);
                            String setMethodName = "set" + field;
                            Method setMethod = targetClass.getMethod(
                                    setMethodName, method
                                            .getReturnType());
                            setMethod.invoke(target, returnObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 两对象之间的拷贝(在目标对象中存在的所有set方法，如果在源对象中存在对应的get方法，且源对象的get方法的返回值不为null和空,就进行拷贝)
     * 仅拷贝方法名及方法返回类型完全一样的属性值
     *
     * @param target 目标对象
     * @param source 源对象
     */
    public static void copyPropertiesNotNullandEmpty(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        Method[] targetMethods = targetClass.getMethods();
        Method[] sourceMethods = sourceClass.getMethods();

        Map<String, Method> methodMap = new HashMap<>();
        for (Method method : sourceMethods) {
            String methodName = method.getName();
            if (!methodName.equals("getClass") && methodName.startsWith("get") && method
                    .getParameterTypes().length == 0) {
                methodMap.put(methodName, method);
            }
        }
        for (Method method : targetMethods) {
            String methodName = method.getName();
            if (!methodName.equals("getClass") && methodName.startsWith("get") && method
                    .getParameterTypes().length == 0) {
                if (methodMap.containsKey(methodName)) {
                    Method sourceMethod = methodMap.get(methodName);
                    try {
                        if (method.getReturnType().equals(
                                sourceMethod.getReturnType())) {
                            Object returnObj = sourceMethod.invoke(source, (Object[]) null);
                            if (ObjectUtil.isEmpty(returnObj)) {
                                continue;
                            }
                            String field = methodName.substring(3);
                            String setMethodName = "set" + field;
                            Method setMethod = targetClass.getMethod(
                                    setMethodName, method
                                            .getReturnType());
                            setMethod.invoke(target, returnObj);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 字符串数组转浮点浮点数组
     *
     * @param strArr 字符串数组
     * @return 浮点数组
     */
    public static Float[] convertToFloatArray(String[] strArr) {
        Float[] floatArr = new Float[strArr.length];
        int count = 0;
        for (String str : strArr) {
            str = str.replaceAll("℃", "");
            floatArr[count] = Float.parseFloat(str);
            count++;
        }
        return floatArr;
    }
}
