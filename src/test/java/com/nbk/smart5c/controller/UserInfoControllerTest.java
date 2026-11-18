package com.nbk.smart5c.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nbk.smart5c.common.utils.ResultUtil;
import com.nbk.smart5c.entity.UserInfo;
import com.nbk.smart5c.exception.ServiceException;
import com.nbk.smart5c.pojo.bo.UserInfoSaveBO;
import com.nbk.smart5c.pojo.bo.UserInfoUpdateBO;
import com.nbk.smart5c.pojo.bo.UserUpdatePwdBO;
import com.nbk.smart5c.pojo.qo.UserInfoQO;
import com.nbk.smart5c.pojo.qo.UserLoginParam;
import com.nbk.smart5c.pojo.vo.UserInfoVO;
import com.nbk.smart5c.service.UserInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserInfoController 单元测试（Mock Service 层）
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings({"unchecked", "rawtypes"})
class UserInfoControllerTest {

    @Mock
    private UserInfoService userInfoService;

    @InjectMocks
    private UserInfoController userInfoController;

    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfo();
    }

    @Test
    void testQueryById() {
        when(userInfoService.queryById("1")).thenReturn(userInfo);

        ResultUtil<UserInfo> result = userInfoController.queryById("1");

        assertEquals(200, result.getCode());
        assertEquals(userInfo, result.getResult());
        verify(userInfoService, times(1)).queryById("1");
    }

    @Test
    void testLogin() {
        UserLoginParam loginParam = new UserLoginParam();
        ResultUtil expected = ResultUtil.OK("登录成功", userInfo);
        when(userInfoService.login(loginParam)).thenReturn(expected);

        ResultUtil result = userInfoController.login(loginParam);

        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).login(loginParam);
    }

    @Test
    void testQueryAndPaging() {
        Page<UserInfo> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(userInfo));
        page.setTotal(1);
        when(userInfoService.queryAndPaging(any(UserInfoQO.class))).thenReturn(page);

        ResultUtil<IPage<UserInfo>> result = userInfoController.queryAndPaging(new UserInfoQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().getTotal());
        verify(userInfoService, times(1)).queryAndPaging(any(UserInfoQO.class));
    }

    @Test
    void testQueryAll() {
        List<UserInfoVO> list = Collections.singletonList(new UserInfoVO());
        when(userInfoService.queryAll(any(UserInfoQO.class))).thenReturn(list);

        ResultUtil<List<UserInfoVO>> result = userInfoController.queryAll(new UserInfoQO());

        assertEquals(200, result.getCode());
        assertEquals(1, result.getResult().size());
        verify(userInfoService, times(1)).queryAll(any(UserInfoQO.class));
    }

    @Test
    void testDeleteById() {
        when(userInfoService.removeById(anyInt())).thenReturn(true);

        ResultUtil<Boolean> result = userInfoController.deleteById(1);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userInfoService, times(1)).removeById(1);
    }

    @Test
    void testDeleteByIds() {
        List<Integer> ids = Collections.singletonList(1);
        when(userInfoService.removeByIds(ids)).thenReturn(true);

        ResultUtil<Boolean> result = userInfoController.deleteByIds(ids);

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userInfoService, times(1)).removeByIds(ids);
    }

    @Test
    void testSave() {
        when(userInfoService.save(any(UserInfoSaveBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = userInfoController.save(new UserInfoSaveBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userInfoService, times(1)).save(any(UserInfoSaveBO.class));
    }

    @Test
    void testUpdateById() {
        when(userInfoService.updateById(any(UserInfoUpdateBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = userInfoController.updateById(new UserInfoUpdateBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userInfoService, times(1)).updateById(any(UserInfoUpdateBO.class));
    }

    @Test
    void testAnalysis() {
        MultipartFile file = mock(MultipartFile.class);
        doNothing().when(userInfoService).analysis(any(MultipartFile.class));

        ResultUtil<Void> result = userInfoController.analysis(file);

        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).analysis(file);
    }

    @Test
    void testExport() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(userInfoService).export(any(UserInfoQO.class), any());

        ResultUtil<Void> result = userInfoController.export(response, new UserInfoQO());

        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).export(any(UserInfoQO.class), eq(response));
    }

    @Test
    void testExportTemplate() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        doNothing().when(userInfoService).exportTemplate(any());

        ResultUtil<Void> result = userInfoController.exportTemplate(response);

        assertEquals(200, result.getCode());
        verify(userInfoService, times(1)).exportTemplate(eq(response));
    }

    @Test
    void testGetPositionList() {
        ResultUtil<List<Map<String, Object>>> result = userInfoController.getPositionList();

        assertEquals(200, result.getCode());
        assertNotNull(result.getResult());
    }

    @Test
    void testUpdatePassword_Success() throws ServiceException {
        when(userInfoService.updatePassword(any(UserUpdatePwdBO.class))).thenReturn(true);

        ResultUtil<Boolean> result = userInfoController.updatePassword(new UserUpdatePwdBO());

        assertEquals(200, result.getCode());
        assertTrue(result.getResult());
        verify(userInfoService, times(1)).updatePassword(any(UserUpdatePwdBO.class));
    }

    @Test
    void testUpdatePassword_ServiceException() throws ServiceException {
        when(userInfoService.updatePassword(any(UserUpdatePwdBO.class)))
                .thenThrow(new ServiceException("原密码错误"));

        ResultUtil<Boolean> result = userInfoController.updatePassword(new UserUpdatePwdBO());

        assertNotEquals(200, result.getCode());
        assertTrue(result.getMessage().contains("修改密码失败"));
        verify(userInfoService, times(1)).updatePassword(any(UserUpdatePwdBO.class));
    }

    @Test
    void testGetUserReqIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");

        ResultUtil<String> result = userInfoController.getPositionList(request);

        assertEquals(200, result.getCode());
        assertEquals("192.168.1.100", result.getResult());
    }
}
