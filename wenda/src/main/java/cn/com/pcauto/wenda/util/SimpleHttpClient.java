package cn.com.pcauto.wenda.util;



import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

public class SimpleHttpClient {
	private static final String EMPTY_STRING = "";
	
	public static String get(String url) {
		if (StringUtils.isBlank(url)) {
			return EMPTY_STRING;
		}
		
		HttpClient httpClient = new DefaultHttpClient();
		
		// online proxy
		HttpHost proxy = new HttpHost("192.168.239.200", 1080);
		
		// dev|test proxy
		// HttpHost proxy = new HttpHost("192.168.11.254", 8080);
		
		httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy)
			.setParameter(CoreProtocolPNames.USER_AGENT, "pcauto-bbs-fuel index task.");

		try {
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				return EntityUtils.toString(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return EMPTY_STRING;
	}
}
