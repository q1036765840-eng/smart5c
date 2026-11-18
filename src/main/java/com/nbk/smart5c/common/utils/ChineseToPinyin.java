package com.nbk.smart5c.common.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class ChineseToPinyin {
    public static String toPinyin(String chinese) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        StringBuilder sb = new StringBuilder();
        char[] chars = chinese.toCharArray();
        for (char ch : chars) {
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch > 128) {
                try {
                    String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch, format);
                    if (pinyinArray != null) {
                        sb.append(pinyinArray[0]);
                    } else {
                        sb.append(ch);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String chinese = "烟雾事件";
        String pinyin = toPinyin(chinese);
        System.out.println(pinyin); // zhongwen
    }
}

