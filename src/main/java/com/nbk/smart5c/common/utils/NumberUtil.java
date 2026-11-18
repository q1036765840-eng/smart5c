package com.nbk.smart5c.common.utils;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Objects;

public class NumberUtil {

    /**
     * 获得以k开头的公里标
     *
     * @param km
     * @return
     */
    public static Double getKmDouble(String km) {
        String s = km.replaceFirst("K", "");
        if (s.contains("+")) {
            String[] split = s.split("\\+");
            if (split[1].length() < 3) {
                split[1] = StringUtils.leftPad(split[1], 3, "0");
            }
            s = split[0] + "." + split[1];
            BigDecimal k = BigDecimal.valueOf(Double.parseDouble(s));
            return k.doubleValue();
        } else {
            if (s.contains(".")) {
                BigDecimal k = BigDecimal.valueOf(Double.parseDouble(s));
                BigDecimal divide = k.divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP);
                return divide.doubleValue();
            } else {
                if (s.length() <= 3) {
                    String padded = StringUtils.leftPad(s, 3, "0");
                    return Double.parseDouble("0." + padded); // 长度不足3位或空值直接返回
                }

                int splitIndex = s.length() - 3;
                return Double.parseDouble(s.substring(0, splitIndex) + "." + s.substring(splitIndex));
            }

        }
    }

    /**
     * 获取俩公里标之间的里程
     *
     * @param num1
     * @param num2
     * @return
     */
    public static Double getMileage(String num1, String num2) {
        String s1 = num1.replaceFirst("K", "").replaceFirst("k", "").replace("+", "");
        String s2 = num2.replaceFirst("K", "").replaceFirst("k", "").replace("+", "");
        int length1 = s1.length();
        int length2 = s2.length();
        String n1 = StringUtils.leftPad("", length1, "0");
        String n2 = StringUtils.leftPad("", length2, "0");
        if (n1.equals(s1)) {
            s1 = "0";
        }
        if (n2.equals(s2)) {
            s2 = "0";
        }
        BigDecimal k1 = BigDecimal.valueOf(Double.parseDouble(s1));
        BigDecimal k2 = BigDecimal.valueOf(Double.parseDouble(s2));

        return k1.subtract(k2).divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP).abs().doubleValue();
    }


    /**
     * 将excel里面的数据转化成double
     *
     * @return
     */
    public static Double getDoubleKm(String km) {
        if (StringUtils.isBlank(km)) {
            return Double.parseDouble("0.00");
        }
        km = km.replaceFirst("k", "").replaceFirst("K", "").replaceAll(",", "").replaceAll("，", "");
        if (km.contains("+")) {
            String[] split = km.split("\\+");
            if (split[1].length() < 3) {
                split[1] = StringUtils.leftPad(split[1], 3, "0");
            }
            String s = split[1];
            String string = s.split("\\.")[1];
            String replace = split[0] + "." + string;
            return Double.parseDouble(replace);
        } else {
            if (km.contains(".")) {
                BigDecimal k = BigDecimal.valueOf(Double.parseDouble(km));
                return k.doubleValue();
            } else {
                if (km.length() <= 3) {
                    String padded = StringUtils.leftPad(km, 3, "0");
                    return Double.parseDouble("0." + padded); // 长度不足3位或空值直接返回
                }

                int splitIndex = km.length() - 3;
                return Double.parseDouble(km.substring(0, splitIndex) + "." + km.substring(splitIndex));
            }
        }
    }


    public static String makeKmStr(Double km) {
        if (Objects.isNull(km)) {
            return "K0+0";
        }
        String[] split = km.toString().split("\\.");
        Double i = Double.parseDouble("0." + split[1]);
        Double r = i * 1000;
        return "K" + split[0] + "+" + r.toString().split("\\.")[0];
    }


    public static Double getTwoNumberDivide(Integer number1, Integer number2, Integer num) {
        if (Objects.isNull(number1) || Objects.isNull(number2) || number2 == 0 || number1 == 0) {
            return Double.parseDouble("0");
        }
        BigDecimal bigDecimal1 = new BigDecimal(number1);
        BigDecimal bigDecimal2 = new BigDecimal(number2);
        BigDecimal divide = bigDecimal1.divide(bigDecimal2, num, RoundingMode.DOWN);
        return divide.doubleValue();
    }

    public static String getTwoNumberDivideToPercent(Integer number1, Integer number2) {
        if (Objects.isNull(number1) || Objects.isNull(number2) || number2 == 0 || number1 == 0) {
            return getPercentFormat(Double.parseDouble("0"), 2);
        }
        BigDecimal bigDecimal1 = new BigDecimal(number1);
        BigDecimal bigDecimal2 = new BigDecimal(number2);
        BigDecimal divide = bigDecimal1.divide(bigDecimal2, 6, RoundingMode.HALF_UP);
        return getPercentFormat(divide.doubleValue(), 2);
    }

    public static String getPercentFormat(Double data, Integer FractionDigits) {
        if (Objects.isNull(data)) {
            return "0%";
        }
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMinimumFractionDigits(FractionDigits);// 小数点后保留几位
        return nf.format(data);
    }

    public static Double getTwoNumberDivide(Long number1, Long number2, Integer num) {
        if (Objects.isNull(number1) || Objects.isNull(number2) || number2 == 0 || number1 == 0) {
            return Double.parseDouble("0");
        }
        BigDecimal bigDecimal1 = new BigDecimal(number1);
        BigDecimal bigDecimal2 = new BigDecimal(number2);
        BigDecimal divide = bigDecimal1.divide(bigDecimal2, num, RoundingMode.HALF_UP);
        return divide.doubleValue();
    }


    public static Integer stringToInteger(String number) {
        if (StringUtils.isBlank(number)) {
            return 0;
        }
        number = number.replaceAll(",", "").replaceAll("，", "");
        if (number.contains(".")) {
            number = number.split("\\.")[0];
        }
        return Integer.parseInt(number);
    }

    public static Long stringToLong(String number) {
        if (StringUtils.isBlank(number)) {
            return IdWorker.getId();
        }
        if (number.contains("_")) {
            String imageName = number.substring(number.lastIndexOf("_") + 1, number.lastIndexOf("."));
            if (imageName.matches("\\d+")) {
                return Long.parseLong(imageName);
            }
        }
        number = number.replaceAll(",", "").replaceAll("，", "");
        if (number.contains(".")) {
            number = number.split("\\.")[0];
        }
        if (number.matches("\\d+")) {
            return Long.parseLong(number);
        }
        return IdWorker.getId();
    }

    public static void main(String[] args) {
        System.out.println(stringToLong("局部_2016-02-12-04-15-19_915_00000113.jpg"));
    }

}
