package cn.com.pcauto.wenda.jstl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.gelivable.web.EnvUtils;

import cn.pconline.r.client.RClient;

/**
 *  读取指定url的内容
 */
public class RClientTag extends SimpleTagSupport {

    private String url;

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void doTag() throws IOException, JspException {
        RClient rClient = EnvUtils.getEnv().getBean(RClient.class);
        String result = rClient.get(url, null, 1, java.util.concurrent.TimeUnit.SECONDS);
        this.getJspContext().getOut().write(result);
    }
}
