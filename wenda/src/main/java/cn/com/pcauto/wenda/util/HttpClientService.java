/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.pcauto.wenda.util;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
/**
 *
 * @author pc
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author pc
 */
public class HttpClientService {
    
	public static byte[] getImgFromUrl(String urlstr,String host,String port)
    {
        int num = urlstr.indexOf('/',8);
        int extnum = urlstr.lastIndexOf('.');
        String u = urlstr.substring(0,num);
        byte[] b = null;
        BufferedImage image = null;
        ByteArrayOutputStream out = null;
        URLConnection connection = null;
        try{
        	System.setProperty("http.proxyHost", host);  
        	System.setProperty("http.proxyPort", port);  
            URL url = new URL(urlstr);
            connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("referer", u);       //通过这个http头的伪装来反盗链
            image = ImageIO.read(connection.getInputStream());
            out = new ByteArrayOutputStream();  
            ImageIO.write(image, "gif", out);  
            b = out.toByteArray();  
        }       
        catch(Exception e)
        {
            System.out.print(e.getMessage().toString());
            b = null;
        }
        return b;
    }
}
