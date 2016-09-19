package mail;

import java.util.Properties;   
public class MailSenderInfo {   
      
    // port   
    private String mailServerHost;   
    private String mailServerPort = "25";   
      
    // from   
    private String fromAddress;   
      
    // to   
    private String toAddress;   
      
    // user&password   
    private String userName;   
    private String password;   
      
    // validate   
    private boolean validate = false;   
      
    // subject   
    private String subject;   
      
    // content   
    private String content;   
      
    // attach   
    private String[] attachFileNames;     
    /**  
      * property  
      */   
    public Properties getProperties(){   
      Properties p = new Properties();   
      p.put("mail.smtp.host", this.mailServerHost);   
      p.put("mail.smtp.port", this.mailServerPort);   
      p.put("mail.smtp.auth", validate ? "true" : "false");   
      return p;   
    }   
    
    public String getMailServerHost() {   
      return mailServerHost;   
    }  
    
    public void setMailServerHost(String mailServerHost) {   
      this.mailServerHost = mailServerHost;   
    }  
    public String getMailServerPort() {   
      return mailServerPort;   
    }  
    public void setMailServerPort(String mailServerPort) {   
      this.mailServerPort = mailServerPort;   
    } 
    
    public boolean isValidate() {   
      return validate;   
    }  
    
    public void setValidate(boolean validate) {   
      this.validate = validate;   
    }  
    public String[] getAttachFileNames() {   
      return attachFileNames;   
    }  
    public void setAttachFileNames(String[] fileNames) {   
      this.attachFileNames = fileNames;   
    }  
    public String getFromAddress() {   
      return fromAddress;   
    }   
    public void setFromAddress(String fromAddress) {   
      this.fromAddress = fromAddress;   
    }  
    public String getPassword() {   
      return password;   
    }  
    public void setPassword(String password) {   
      this.password = password;   
    }  
    public String getToAddress() {   
      return toAddress;   
    }   
    public void setToAddress(String toAddress) {   
      this.toAddress = toAddress;   
    }   
    public String getUserName() {   
      return userName;   
    }  
    public void setUserName(String userName) {   
      this.userName = userName;   
    }  
    public String getSubject() {   
      return subject;   
    }  
    public void setSubject(String subject) {   
      this.subject = subject;   
    }  
    public String getContent() {   
      return content;   
    }  
    public void setContent(String textContent) {   
      this.content = textContent;   
    }   
} 