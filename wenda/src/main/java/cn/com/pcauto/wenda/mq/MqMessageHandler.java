package cn.com.pcauto.wenda.mq;

import com.alibaba.fastjson.JSONObject;

/**
 * @author zhouruxuan
 * @date 2014年12月3日 上午10:20:32
 */
public interface MqMessageHandler {
    /**
     * 任务消息处理
     * @param object
     */
    public void handleJsonMessage(JSONObject object);

}
