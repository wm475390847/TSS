package com.chaohu.robot;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/3/12 11:37 上午
 */
public class MarkdownRobot extends AbstractRobot {

    private final String text;
    private final String[] mobiles;
    private final String[] userIds;
    private final boolean isAtAll;

    public MarkdownRobot(Builder builder) {
        super(builder);
        this.text = builder.text;
        this.mobiles = builder.mobiles;
        this.userIds = builder.userIds;
        this.isAtAll = builder.isAtAll;
    }

    @Override
    protected JSONObject message() {
        JSONObject object = new JSONObject();
        object.put("msgtype", "markdown");
        JSONObject markdown = new JSONObject();
        markdown.put("title", getKeyword());
        markdown.put("text", text);
        JSONObject at = new JSONObject();
        at.put("atMobiles", mobiles);
        at.put("atUserIds", userIds);
        at.put("isAtAll", isAtAll);
        object.put("at", at);
        object.put("markdown", markdown);
        return object;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder {
        private String text;
        private String[] mobiles;
        private String[] userIds;
        private boolean isAtAll;

        @Override
        public IRobot build() {
            return new MarkdownRobot(this);
        }
    }
}
