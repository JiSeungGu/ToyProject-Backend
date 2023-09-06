package com.example.common.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonResult {
    // 응답 코드
    private int code;

    // 응답 메시지
    private String msg;

}
