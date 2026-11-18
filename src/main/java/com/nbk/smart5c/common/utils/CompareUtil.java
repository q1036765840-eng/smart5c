package com.nbk.smart5c.common.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;

public class CompareUtil {

    // 中式的名称排序
    public static Comparator<T> chineseCharComparator(Function<T, String> comparePropertyGetter) {
        return (objA, objB) -> compareTo(comparePropertyGetter.apply(objA), comparePropertyGetter.apply(objB));
    }

    private static final Collator CHINESE_COLLATOR = Collator.getInstance(Locale.CHINA);

    public static int compareTo(String o1, String o2) {
        // 处理null和空值
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        int len1 = o1.length();
        int len2 = o2.length();
        int i = 0, j = 0;

        while (i < len1 && j < len2) {
            char c1 = o1.charAt(i);
            char c2 = o2.charAt(j);

            // 如果都是数字，提取并比较整个数字
            if (Character.isDigit(c1) && Character.isDigit(c2)) {
                // 跳过前导零
                int start1 = i, start2 = j;

                // 提取数字
                long num1 = extractNumber(o1, i);
                long num2 = extractNumber(o2, j);

                if (num1 != num2) {
                    return Long.compare(num1, num2);
                }

                // 如果数字相等，跳过已处理的数字
                i = start1;
                j = start2;
                while (i < len1 && Character.isDigit(o1.charAt(i))) i++;
                while (j < len2 && Character.isDigit(o2.charAt(j))) j++;
                continue;
            }

            // 非数字字符，使用中文比较器
            int cmp = CHINESE_COLLATOR.compare(String.valueOf(c1), String.valueOf(c2));
            if (cmp != 0) {
                return cmp;
            }

            i++;
            j++;
        }

        // 比较长度
        return Integer.compare(len1 - i, len2 - j);
    }

    private static long extractNumber(String str, int start) {
        long num = 0;
        int i = start;
        int len = str.length();

        while (i < len && Character.isDigit(str.charAt(i))) {
            num = num * 10 + (str.charAt(i) - '0');
            i++;
        }

        return num;
    }

    /**
     * 数字字符串转数字数组
     * 适配 1.25.3.5 这种情况 ，同时如果不不包含小数点【整数情况】
     *
     * @return
     */
    public static String[] numberStrToNumberArray(String numberStr) {
        // 按小数点分割字符串数组
        String[] numberArray = numberStr.split("\\.");
        // 长度为0说明没有小数点，则整个字符串作为第一个元素
        if (numberArray.length == 0) {
            numberArray = new String[]{numberStr};
        }
        return numberArray;

    }

    /**
     * 比较两个数字数组
     *
     * @param numberArray1
     * @param numberArray2
     * @return
     */
    public static int compareNumberArray(String[] numberArray1, String[] numberArray2) {
        int type = numberArray1.length > numberArray2.length ? 1 : numberArray1.length == numberArray2.length ? 2 : 3;
        int length1 = numberArray1.length;
        int length2 = numberArray2.length;
        int length = type == 3 ? numberArray1.length : numberArray2.length;

        for (int i = 0; i < numberArray1.length; i++) {
            if (numberArray2.length < i + 1) { // 此时数字数组2比1短，直接返回
                return 1;
            }
            int num = (Integer.MAX_VALUE + "").length();
            if (numberArray1[i].length() > num || numberArray2[i].length() > num) {
                return 0;
            }
            int compareResult = Integer.valueOf(numberArray1[i]).compareTo(Integer.valueOf(numberArray2[i]));
            if (compareResult != 0) {
                return compareResult;
            }
        }
        for (int i = 0; i < length; i++) {
            int compareResult = Integer.valueOf(numberArray1[i]).compareTo(Integer.valueOf(numberArray2[i]));
            if (compareResult != 0) {
                return compareResult;
            }
        }
        // 说明数组1比数组2短，返回小于
        return -2;
    }


    public static Integer comparePoleNum(String p1, String p2) {
        if (StringUtils.isBlank(p1) || StringUtils.isBlank(p2)) {
            return 0;
        }
        if (Character.isDigit(p1.charAt(0)) && Character.isDigit(p1.charAt(0))) {
            return Integer.parseInt(p1) - Integer.parseInt(p2);
        }
        if (Character.isDigit(p1.charAt(0)) && !Character.isDigit(p1.charAt(0))) {
            return Integer.parseInt(p1) - Integer.parseInt(p2.substring(1));
        }
        if (!Character.isDigit(p1.charAt(0)) && Character.isDigit(p1.charAt(0))) {
            return Integer.parseInt(p1.substring(1)) - Integer.parseInt(p2);
        }
        if (!Character.isDigit(p1.charAt(0)) && !Character.isDigit(p1.charAt(0))) {
            return Integer.parseInt(p1.substring(1)) - Integer.parseInt(p2.substring(1));
        }
        return 0;
    }
}
