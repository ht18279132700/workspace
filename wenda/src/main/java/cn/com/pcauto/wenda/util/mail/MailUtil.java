package cn.com.pcauto.wenda.util.mail;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;

public class MailUtil {
	
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	public static final String SENDER = "support@imofan.com";
	
    public static final Session SESSION;
    
    static{
        Properties prop = new Properties();
        prop.put("mail.host", "192.168.238.144");
        prop.put("mail.username", SENDER);
        prop.put("mail.password", "!@#asd123");
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.localhost", "mga.imofan.com");
        SESSION =  Session.getInstance(prop, null);
    }
    
    public static void sendEmail(String subject, String content, String... to) throws MessagingException{
		MimeMessage message = new MimeMessage(SESSION);
		MimeMessageHelper helper = new MimeMessageHelper(message, DEFAULT_CHARSET);
		helper.setFrom(SENDER);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, true);
		Transport.send(message);
    }
    
}
