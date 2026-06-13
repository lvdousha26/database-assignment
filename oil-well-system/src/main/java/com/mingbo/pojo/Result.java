package com.oilwell.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Result {
    private String code;
    private String msg;
    private Object data;

    public static Result success(Object data) {
        Result result = success();
        result.setData(data);
        return result;
    }

    public static Result success() {
        Result result = new Result();
        result.setCode("1");
        result.setMsg("success");
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.setCode("-1");
        result.setMsg(msg);
        return result;
    }
}
