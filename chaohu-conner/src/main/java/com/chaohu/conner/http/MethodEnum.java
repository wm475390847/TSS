package com.chaohu.conner.http;

import com.chaohu.conner.exception.HttpException;
import com.chaohu.conner.http.connector.*;
import lombok.Getter;
import okhttp3.Response;

import java.util.Arrays;
import java.util.Optional;

/**
 * 请求枚举
 * <P>请求映射相应的请求命令类
 *
 * @author wangmin
 * @date 2022/5/17 13:05
 */
public enum MethodEnum {

    /**
     * post请求
     */
    POST("POST", new PostConnector()),

    /**
     * get请求
     */
    GET("GET", new GetConnector()),

    /**
     * delete请求
     */
    DELETE("DELETE", new DeleteConnector()),

    /**
     * put请求
     */
    PUT("PUT", new PutConnector()),

    /**
     * multipart请求
     */
    MULTIPART("MULTIPART", new MultipartConnector()),
    ;

    MethodEnum(String methodType, IConnector<Response> connector) {
        this.methodType = methodType;
        this.connector = connector;
    }

    @Getter
    private final String methodType;

    @Getter
    private final IConnector<Response> connector;

    /**
     * 通过请求方法类型查询对应的枚举值
     *
     * @param methodType 请求类型
     * @return 对应的MethodEnum
     */
    public static MethodEnum findEnumByType(String methodType) {
        Optional.ofNullable(methodType).orElseThrow(() -> new HttpException("methodType 不能为空"));
        Optional<MethodEnum> any = Arrays.stream(MethodEnum.values())
                .filter(e -> e.getMethodType().equals(methodType)).findAny();
        Optional.of(any).filter(Optional::isPresent).orElseThrow(() -> new HttpException("类型不存在"));
        return any.get();
    }
}
