package cn.com.pcauto.wenda.util.monitor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitorFilter implements Filter {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void destroy() {
		logger.info("MonitorFilter destroied.");
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		
        String sample = config.getInitParameter("sample");
        if (sample != null) {
        	String[] items = sample.split(" ");
        	for (int i = 0; i < items.length; i++) {
        		Monitor.addSample(items[i]);
        	}
        }
        
        logger.info("MonitorFilter inited.");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
        RequestHolder holder = new RequestHolder(request);
        Monitor.open(holder);
        try {

            chain.doFilter(request, response);

        } catch (ServletException se) {
        	holder.error();
        	logger.error(se.getMessage(), se);
        	throw se;
        } catch (IOException ioe) {
        	holder.error();
        	logger.error(ioe.getMessage(), ioe);
        	throw ioe;
        } catch (RuntimeException re) {
        	holder.error();
        	logger.error(re.getMessage(), re);
        	throw re;
        } catch (Error err) {
        	holder.error();
        	logger.error(err.getMessage(), err);
        	throw err;
        } finally {
        	Monitor.close(holder);
        }
	}
}
