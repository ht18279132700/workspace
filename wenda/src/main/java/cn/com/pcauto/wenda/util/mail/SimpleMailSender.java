package cn.com.pcauto.wenda.util.mail;

import java.io.File;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SimpleMailSender {

	/**
	 * 以普通文本格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件的信息
	 * @return
	 */
	public static boolean sendTextMail(MailSenderInfo mailInfo) {
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getFromAddress(),
					mailInfo.getPassword());
		}
		Session sendMailSession = Session.getInstance(pro, authenticator);
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			Address from = new InternetAddress(mailInfo.getNickName());
			mailMessage.setFrom(from);
			String[] mailToAddress = mailInfo.getToAddress();
			int len = mailToAddress.length;
			Address to[] = new InternetAddress[len];
			for (int i = 0; i < len; i++) {
				to[i] = new InternetAddress(mailToAddress[i]);
			}
			// TO表示主要接收人，CC表示抄送人，BCC表示秘密抄送人。
			mailMessage.setRecipients(Message.RecipientType.TO, to);
			mailMessage.setSubject(mailInfo.getSubject());
			mailMessage.setSentDate(new Date());
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent);
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		System.out.println("sendEmail>>>>>>>>>>>>>start");
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getFromAddress(),
					mailInfo.getPassword());
		}
		Session sendMailSession = Session.getInstance(pro, authenticator);
		try {
			Message mailMessage = new MimeMessage(sendMailSession);
			Address from = new InternetAddress(MimeUtility.encodeText(mailInfo
					.getNickName()) + " <" + mailInfo.getFromAddress() + ">");
			mailMessage.setFrom(from);
			String[] mailToAddress = mailInfo.getToAddress();
			int len = mailToAddress.length;
			Address to[] = new InternetAddress[len];
			for (int i = 0; i < len; i++) {
				to[i] = new InternetAddress(mailToAddress[i]);
			}
			mailMessage.setRecipients(Message.RecipientType.TO, to);
			mailMessage.setSubject(mailInfo.getSubject());
			mailMessage.setSentDate(new Date());

			Multipart mainPart = new MimeMultipart();
			BodyPart html = new MimeBodyPart();
			html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
			mainPart.addBodyPart(html);

			Vector<File> file = mailInfo.getFile();
			String fileName = mailInfo.getFileName();
			Enumeration<File> efile = file.elements();
			while (efile.hasMoreElements()) {
				MimeBodyPart mdpFile = new MimeBodyPart();
				fileName = efile.nextElement().toString();
				FileDataSource fds = new FileDataSource(fileName);
				mdpFile.setDataHandler(new DataHandler(fds));
				String fileName1 = new String(fds.getName().getBytes(),
						"ISO-8859-1");
				mdpFile.setFileName(fileName1);
				mainPart.addBodyPart(mdpFile);
			}
			file.removeAllElements();
			mailMessage.setContent(mainPart);
			Transport.send(mailMessage);
			System.out.println("sendEmail>>>>>>>>>>>>endTime:"+System.currentTimeMillis());
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public static MailSenderInfo getSenderInfo(String mailServerHost,
			String mailServerPort, boolean isValidate, String mailPassWord,
			String fromAddress, String[] toAddress, String subject,
			String content, String nickName) {
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(mailServerHost);
		mailInfo.setMailServerPort(mailServerPort);
		mailInfo.setValidate(isValidate);
		mailInfo.setPassword(mailPassWord);
		mailInfo.setFromAddress(fromAddress);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		mailInfo.setNickName(nickName);
		return mailInfo;
	}
}