package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.Section;
import com.nbk.smart5c.pojo.bo.SectionSaveBO;
import com.nbk.smart5c.pojo.bo.SectionUpdateBO;
import com.nbk.smart5c.pojo.qo.SectionQO;
import com.nbk.smart5c.pojo.vo.DropDownListVO;
import com.nbk.smart5c.pojo.vo.SectionVO;
import com.nbk.smart5c.pojo.vo.TreeVO;
import com.nbk.smart5c.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SectionController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class SectionControllerTest {

    @Mock
    private SectionService sectionService;

    @InjectMocks
    private SectionController sectionController;

    private Section section;

    @BeforeEach
    void setUp() {
        section = new Section();
    }

    @Test
    void testQueryById() {
        when(sectionService.queryById("1")).thenReturn(section);

        ResultUtil<Section> result = sectionController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(section, result.getResult());
        verify(sectionService, times(1)).queryById("1");
    }

    @Test
    void testQueryAndPaging() {
        Page<SectionVO> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new SectionVO()));
        page.setTotal(1);
        when(sectionService.queryAndPaging(any(SectionQO.class))).thenReturn(page);

        ResultUtil<IPage<SectionVO>> result = sectionController.queryAndPaging(new SectionQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(sectionService, times(1)).queryAndPaging(any(SectionQO.class));
    }

    @Test
    void testQueryAll() {
        List<SectionVO> list = Collections.singletonList(new SectionVO());
        when(sectionService.queryAll(any(SectionQO.class))).thenReturn(list);

        ResultUtil<List<SectionVO>> result = sectionController.queryAll(new SectionQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(sectionService, times(1)).queryAll(any(SectionQO.class));
    }

    @Test
    void testDeleteById() {
        when(sectionService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = sectionController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(sectionService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(sectionService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = sectionController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(sectionService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(sectionService.save(any(SectionSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = sectionController.save(new SectionSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(sectionService, times(1)).save(any(SectionSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(sectionService.updateById(any(SectionUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = sectionController.updateById(new SectionUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(sectionService, times(1)).updateById(any(SectionUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(sectionService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = sectionController.analysis(file);

        assertEquals(200, result.getCode());
        verify(sectionService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(sectionService).export(any(SectionQO.class), any());

        ResultUtil<Void> result = sectionController.export(response, new SectionQO());

        assertEquals(200, result.getCode());
        verify(sectionService, times(1)).export(any(SectionQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(sectionService).exportTemplate(any());

        ResultUtil<Void> result = sectionController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(sectionService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetSectionDownList() {
        List<DropDownListVO> list = Collections.singletonList(new DropDownListVO());
        when(sectionService.getSectionDownList()).thenReturn(list);

        ResultUtil<List<DropDownListVO>> result = sectionController.getSectionDownList();

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(sectionService, times(1)).getSectionDownList();
    }

    @Test
    void testGetSectionTree() {
        List<TreeVO> treeList = Collections.singletonList(new TreeVO());
        when(sectionService.getSectionTree()).thenReturn(treeList);

        ResultUtil<List<TreeVO>> result = sectionController.getSectionTree();

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(sectionService, times(1)).getSectionTree();
    }
}
