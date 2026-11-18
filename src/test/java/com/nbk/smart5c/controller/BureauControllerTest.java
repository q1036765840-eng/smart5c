package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Bureau;
import com.nbk.smart5c.pojo.bo.BureauSaveBO;
import com.nbk.smart5c.pojo.bo.BureauUpdateBO;
import com.nbk.smart5c.pojo.qo.BureauQO;
import com.nbk.smart5c.pojo.vo.BureauVO;
import com.nbk.smart5c.service.BureauService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BureauController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class BureauControllerTest {

    @Mock
    private BureauService bureauService;

    @InjectMocks
    private BureauController bureauController;

    private Bureau bureau;

    @BeforeEach
    void setUp() {
        bureau = new Bureau();
    }

    @Test
    void testQueryById() {
        when(bureauService.queryById("1")).thenReturn(bureau);

        ResultUtil<Bureau> result = bureauController.queryById("1");

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(bureau, result.getResult());
        verify(bureauService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<BureauVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new BureauVO()));
        page.setTotal(1);
        when(bureauService.queryAndPaging(any(BureauQO.class))).thenReturn(page);

        ResultUtil<IPage<BureauVO>> result = bureauController.queryAndPaging(new BureauQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(bureauService, times(1)).queryAndPaging(any(BureauQO.class));
    }

    @Test
    void testQueryAll() {
        List<BureauVO> list = Collections.singletonList(new BureauVO());
        when(bureauService.queryAll(any(BureauQO.class))).thenReturn(list);

        ResultUtil<List<BureauVO>> result = bureauController.queryAll(new BureauQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(bureauService, times(1)).queryAll(any(BureauQO.class));
    }

    @Test
    void testDeleteById() {
        when(bureauService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = bureauController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(bureauService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(bureauService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = bureauController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(bureauService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(bureauService.save(any(BureauSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = bureauController.save(new BureauSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(bureauService, times(1)).save(any(BureauSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(bureauService.updateById(any(BureauUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = bureauController.updateById(new BureauUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(bureauService, times(1)).updateById(any(BureauUpdateBO.class));
    }
}
