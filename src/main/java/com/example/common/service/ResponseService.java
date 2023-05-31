package com.example.common.service;


import com.example.common.response.CommonResult;
import com.example.common.response.ListResult;
import com.example.common.response.SingleResult;

import org.springframework.stereotype.Service;

import java.util.List;

// 여러 종류의 응답을 처리하는 클래스
@Service
public class ResponseService {

//    @Autowired private CommonCodeJpaRepo commonCodeJpaRepo;

    // enum으로 api 요청 결과에 대한 code, message를 정의
    public enum CommonResponse {
        SUCCESS(0, "성공하였습니다."),
        FAIL(-1, "실패하였습니다.");

        int code;
        String msg;

        CommonResponse(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    // 단일건 결과를 처리하는 메소드
    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        setSuccessResult(result);
        return result;
    }
    // 다중건 결과를 처리하는 메소드
    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<>();
        result.setData(list);
        setSuccessResult(result);
        return result;
    }
    // 성공 결과만 처리하는 메소드
    public CommonResult getSuccessResult() {
        CommonResult result = new CommonResult();
        setSuccessResult(result);
        return result;
    }

    // 실패 결과만 처리하는 메소드 - 시스템 에러인 경우만 사용할것!
    public CommonResult getFailResult(int code, String msg) {
        CommonResult result = new CommonResult();
        // result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
//
//    public CommonResult getFailResult(int code) {
//        CommonResult result = new CommonResult();
//
//        CommonCode commonCode =
//                commonCodeJpaRepo
//                        .findByCodeid(code + "")
//                        .orElseGet(
//                                () ->
//                                        CommonCode.builder()
//                                                .codename("정의되지 않은 메시지 입니다.")
//                                                .codeid("-1")
//                                                .codegroup("ERR")
//                                                .build());
//
//        result.setCode(code);
//        result.setMsg(commonCode.getCodename());
//        return result;
//    }


    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(CommonResult result) {
        // result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }
}
