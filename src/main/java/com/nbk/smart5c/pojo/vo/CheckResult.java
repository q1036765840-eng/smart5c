package com.nbk.smart5c.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Data
@ToString
public class CheckResult {
    private String ip;
    private int port;
    private Boolean isOpen;
    private String error;
}