package cn.com.pcauto.wenda.util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
 /**
  * 发邮件的身份验证器
  * @author zhoutianhuai
  *
  * 2015年3月17日 下午4:14:56
  */
public class MyAuthenticator extends Authenticator{   
  String userName=null;   
  String password=null;   
      
  public MyAuthenticator(){   
  	
  }   
  public MyAuthenticator(String username, String password) {    
      this.userName = username;    
      this.password = password;    
  }  
  protected PasswordAuthentication getPasswordAuthentication(){   
      return new PasswordAuthentication(userName, password);   
  }   
}   