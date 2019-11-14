package cn.com.pcauto.wenda.mq;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;


/**
 * @author zhouruxuan
 * @date 2014年12月3日 上午10:12:32
 */
public class MessageListener {
    private static final Logger log = LoggerFactory.getLogger(MessageListener.class);
    
    private static final String ROUTE_KEY = "routeKey";

    private Map<String, List<MqMessageHandler>> handlers;
    
    public void setHandlers(Map<String, List<MqMessageHandler>> handlers) {
        this.handlers = handlers;
    }
    
    /**
     * 监听器，接收MQ信息的主方法
     *
     * @param obj
     */
    public void onMessage(JSONObject obj) {
        if (obj == null) {
            return;
        }
        
        List<MqMessageHandler> list = handlers.get(obj.getString(ROUTE_KEY));
        if (list == null) {
            log.info("No mq message handler, message: {}", obj);
            return;
        }
        for (MqMessageHandler handler : list) {
            if(handler == null) {
                log.error("this is a null handler try to process msg, message: {}", obj);
                continue;
            }
            try {
                handler.handleJsonMessage(obj);
            } catch (Exception e) {
                log.error("{} process message error, message: {}", handler.getClass().getSimpleName(), obj);
                log.error("Process message failed.", e);
            }
        }
    }
}
