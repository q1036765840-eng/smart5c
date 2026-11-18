package com.nbk.smart5c.pojo.vo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author jiaxb
 * @version 1.0
 * @description
 * @date 2026-05-11 10:46
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class SftpHostVO {

    /**
     * SFTP 服务器地址
     */
    private String host;

    /**
     * SFTP 端口，默认 22
     */
    private int port = 22;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（与私钥二选一）
     */
    private String password;
}
