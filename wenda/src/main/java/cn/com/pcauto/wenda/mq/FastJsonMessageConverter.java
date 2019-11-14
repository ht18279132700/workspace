package cn.com.pcauto.wenda.mq;

import com.alibaba.fastjson.JSON;

import com.alibaba.fastjson.JSONObject;
import java.io.UnsupportedEncodingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.AbstractMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;

/**
 * @author zhouruxuan
 * @date 2014年12月3日 上午10:20:32
 */
public class FastJsonMessageConverter extends AbstractMessageConverter {

    private static Log log = LogFactory.getLog(FastJsonMessageConverter.class);
    public static final String DEFAULT_CHARSET = "UTF-8";
    private volatile String defaultCharset = DEFAULT_CHARSET;

    public FastJsonMessageConverter() {
        super();
        //init();
    }

    public void setDefaultCharset(String defaultCharset) {
        this.defaultCharset = (defaultCharset != null) ? defaultCharset
                : DEFAULT_CHARSET;
    }

    @Override
    public JSONObject fromMessage(Message message)
            throws MessageConversionException {
        String json = "";
        try {
            json = new String(message.getBody(), defaultCharset);
        } catch (UnsupportedEncodingException e) {
            log.error("Convert to jason failed.", e);
            log.error(message.toString());
        }
        try {
            JSONObject object = JSON.parseObject(json);
            object.put("routeKey", message.getMessageProperties().getReceivedRoutingKey());
            return object;
        } catch (RuntimeException rte) {
            return null;
        }
    }

    @Override
    protected Message createMessage(Object objectToConvert,
            MessageProperties messageProperties)
            throws MessageConversionException {
        byte[] bytes = null;
        try {
            String jsonString = JSON.toJSONString(objectToConvert);
            bytes = jsonString.getBytes(this.defaultCharset);
        } catch (UnsupportedEncodingException e) {
            throw new MessageConversionException(
                    "Failed to convert Message content", e);
        }
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        messageProperties.setContentEncoding(this.defaultCharset);
        if (bytes != null) {
            messageProperties.setContentLength(bytes.length);
        }
        return new Message(bytes, messageProperties);

    }
}