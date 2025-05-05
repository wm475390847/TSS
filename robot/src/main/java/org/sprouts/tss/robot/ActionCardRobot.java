package org.sprouts.tss.robot;

import com.alibaba.fastjson.JSONObject;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author wangmin
 * @date 2022/3/12 11:37 上午
 */
public class ActionCardRobot extends AbstractRobot {

    private final String text;
    private final String btnOrientation;
    private final String singleTitle;
    private final String singUrl;

    public ActionCardRobot(Builder builder) {
        super(builder);
        this.text = builder.text;
        this.btnOrientation = builder.btnOrientation;
        this.singleTitle = builder.singleTitle;
        this.singUrl = builder.singUrl;
    }

    @Override
    protected JSONObject message() {
        JSONObject object = new JSONObject();
        object.put("msgtype", "actionCard");
        JSONObject actionCard = new JSONObject();
        actionCard.put("title", getKeyword());
        actionCard.put("text", text);
        actionCard.put("btnOrientation", btnOrientation);
        actionCard.put("singleTitle", singleTitle);
        actionCard.put("singleUrl", singUrl);
        object.put("actionCard", actionCard);
        return object;
    }

    @Setter
    @Accessors(chain = true, fluent = true)
    public static class Builder extends BaseBuilder {
        private String text;
        private String btnOrientation = "0";
        private String singleTitle;
        private String singUrl;

        @Override
        public IRobot build() {
            return new ActionCardRobot(this);
        }
    }
}
