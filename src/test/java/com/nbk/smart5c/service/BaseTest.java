package com.nbk.smart5c.service;

import cn.hutool.json.JSONUtil;
import com.nbk.smart5c.common.utils.HttpClientUtils;
import com.nbk.smart5c.pojo.bo.AiAnalysisRespBO;
import com.nbk.smart5c.pojo.bo.DefectMarkBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BaseTest {
    @Test
    public void test1() {
        Map<String, Object> param = new HashMap<>();
        param.put("image", "/media/root/工作/项目文档/5C/成都段/缺陷数据/0210010_00081_0037_00.jpg");
        String result = HttpClientUtils.sendPost("http://192.168.5.209:5000/api/analysis/infer", param);
        AiAnalysisRespBO bean = JSONUtil.toBean(result, AiAnalysisRespBO.class);
        System.out.println(bean.toString());
    }

    @Test
    public void test2() {
//        Map<String,Object> param = new HashMap<>();
//        param.put("image","/media/root/工作/项目文档/5C/成都段/缺陷数据/0210010_00081_0037_00.jpg");
//        long l1 = System.currentTimeMillis();
//        String result = HttpClientUtils.sendPost("http://192.168.5.209:5000/api/analysis/image/valid", param);
//        System.out.println(
//                result);
//        long l2 = System.currentTimeMillis() - l1;
//        System.out.println(l2);
        String f = "/a/b/上海段/c";
        String a = "[{\"name\":\"上海段\"，}]";
        System.out.println(f.substring(f.lastIndexOf("上海段")));
    }

    @Test
    public void test4() {
        String a = "[{\"coord\":\"677,93,809,230\",\"defectType\":\"异物\"}]";
        System.out.println(JSONUtil.toList(a, DefectMarkBO.class));
    }

    @Test
    public void test3() {
        label1:
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 10; j++) {
                if (j == 3) {
                    break label1;
                }
                if (i == 5 && j == 5) {
                    break label1; // 跳出外层循环
                }
                System.out.println(i + ", " + j);
            }
        }
    }
}
