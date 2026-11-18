package com.nbk.smart5c.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

public class ListPagingUtil {

    /**
     * 任意集合的分页
     *
     * @param list
     * @param pageNo
     * @param pageSize
     * @param <T>
     * @return
     */
    public static <T> Page<T> pageList(List<T> list, Integer pageNo, Integer pageSize) {
        List<T> ts = new ArrayList<>();
        Page<T> objectPage = new Page<>();
        objectPage.setSize(pageSize);
        objectPage.setTotal(list.size());
        objectPage.setCurrent(pageNo);
        if (list.isEmpty()) {
            objectPage.setRecords(ts);
            objectPage.setPages(0L);
            return objectPage;
        }
        int pages = list.size() / pageSize;
        if (list.size() % pageSize > 0) {
            pages = pages + 1;
        }
        int fromIndex = (pageNo - 1) * pageSize;
        if (fromIndex >= list.size() || fromIndex < 0) {
            objectPage.setRecords(ts);
            objectPage.setPages(pages);
            return objectPage;
        }

        int toIndex = Math.min(fromIndex + pageSize, list.size());
        List<T> ts1 = list.subList(fromIndex, toIndex);
        objectPage.setRecords(ts1);
        objectPage.setPages(pages);
        return objectPage;
    }
}