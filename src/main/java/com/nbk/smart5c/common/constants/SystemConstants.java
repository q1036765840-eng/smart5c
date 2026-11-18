package com.nbk.smart5c.common.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用常量
 *
 * @author jxb
 */
public class SystemConstants {
    /**
     * 文件类型：视频
     */
    public static final Integer FILE_VIDEO = 0;
    /**
     * 文件类型：图片
     */
    public static final Integer FILE_IMAGE = 1;

    /**
     * 文件类型：文件夹
     */
    public static final Integer FILE_FOLDER = 2;

    /**
     * 文件类型：文件夹
     */
    public static final String DEFECT_STORAGE = "defectstorage";

    /**
     * 文件类型：其它
     */
    public static final Integer FILE_OTHER = 3;

    public static final Map<String, String> LINE_LOCATION_CONFIG = new HashMap<String, String>() {{
        put("重庆北动车所-重庆北动车所", "a");
        put("重庆北动车所-重庆北洗车线", "a");
        put("郑万线-云万区间", "a");
        put("沪昆线-新晃-大龙", "a");
        put("动走123线-重庆西动车所", "a");
        put("出入线-万州北", "a");
        put("沪昆客专-大宗坪", "a");
        put("成都动车所-库存1线", "a");
        put("贵广客专-从江站", "a");
        put("成昆线-峨眉", "a");
        put("贵阳北动车所-贵阳北动车一所", "a");
        put("渝怀线-秀山", "a");
        put("兰渝线-广羊区间", "a");
        put("铜玉客专-铜玉局界", "a");
        put("水柏客联线-水柏客联线", "a");
        put("成宜线-威自区间", "a");
        put("成宜线-宜宾西停车场站区", "a");
        put("宝成线-冉家河-广元", "a");
        put("沪昆线-且午-凤凰山", "a");
        put("黔桂线-麻尾局界站区", "a");
        put("渝昆高速铁路-重庆西-九龙坡", "a");
        put("水柏货联线-水柏货联线", "a");
        put("川南城际线-泸州站存车场", "a");
        put("川南城际线-内江东-白马北", "a");
    }};
}

