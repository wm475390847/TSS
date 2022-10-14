package com.chaohu.robot;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/3/12 11:37 上午
 */
public class LinkRobot extends AbstractRobot {

    private final String text;
    private final String picUrl;
    private final String messageUrl;

    public LinkRobot(Builder builder) {
        super(builder);
        this.messageUrl = builder.messageUrl;
        this.picUrl = builder.picUrl;
        this.text = builder.text;
    }

    @Override
    protected JSONObject message() {
        JSONObject object = new JSONObject();
        object.put("msgtype", "link");
        JSONObject link = new JSONObject();
        link.put("text", text);
        link.put("title", getKeyword());
        link.put("picUrl", picUrl);
        link.put("messageUrl", messageUrl);
        object.put("link", link);
        return object;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder {
        private String text;
        private String picUrl;
        private String messageUrl;

        @Override
        public IRobot build() {
            return new LinkRobot(this);
        }
    }
}
