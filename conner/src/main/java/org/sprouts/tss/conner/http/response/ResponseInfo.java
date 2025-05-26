package org.sprouts.tss.conner.http.response;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回值对象
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
@Data
public class ResponseInfo implements Serializable {

    @JSONField(name = "code")
    private int code;

    @JSONField(name = "data")
    private Object data;

    @JSONField(name = "result")
    private Object result;

    @JSONField(name = "requestId")
    private String requestId;

    @JSONField(name = "success")
    private Boolean success;

    @JSONField(name = "errcode")
    private Integer errCode;

    @JSONField(name = "errmsg")
    private String errMsg;

    @JSONField(name = "msg")
    private String msg;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "total")
    private Long total;

    /**
     * 获取JSONObject类型的响应体
     *
     * @return 响应体
     */
    public JSONObject getJsonData() {
        return (JSONObject) this.data;
    }


    public JSONObject getJsonResult() {
        return (JSONObject) this.result;
    }

    /**
     * 获取JSONArray类型的响应体
     *
     * @return 响应体
     */
    public JSONArray getArrayData() {
        return (JSONArray) this.data;
    }

    public String getStrData() {
        return this.data.toString();
    }
}
