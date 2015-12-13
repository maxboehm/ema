package com.maximilian_boehm.ema.in;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class IMAPAuthenticator extends Authenticator {
     private String user;
     private String pw;
     
     public IMAPAuthenticator (String username, String password){
        super();
        this.user = username;
        this.pw = password;
     }
     
    public PasswordAuthentication getPasswordAuthentication(){
       return new PasswordAuthentication(user, pw);
    }
}