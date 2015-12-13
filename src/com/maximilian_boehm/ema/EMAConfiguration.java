package com.maximilian_boehm.ema;

import java.io.File;

public class EMAConfiguration {
	
	private File BaseDir = null;
	private File FilesDir = null;
	private File MailsDir = null;
	private String Host;
	private String User;
	private String Password;
	private String Type;
	

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		Type = type;
	}

	public String getHost() {
		return Host;
	}

	public void setHost(String host) {
		Host = host;
	}

	public String getUser() {
		return User;
	}

	public void setUser(String user) {
		User = user;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public File getBaseDir() {
		return BaseDir;
	}

	public void setBaseDir(File basePath) {
		BaseDir = basePath;
	}

	public File getFilesDir() {
		return FilesDir;
	}

	public void setFilesDir(File filesDir) {
		FilesDir = filesDir;
	}

	public File getMailsDir() {
		return MailsDir;
	}

	public void setMailsDir(File mailsDir) {
		MailsDir = mailsDir;
	}

	public String toHTML() {
		String s = "Dir: "+getBaseDir()+"<br>";
		s += "Host: "+getHost()+"<br>";
		s += "User: "+getUser()+"<br>";
		s += "Type: "+getType()+"<br>";
		return s;
	}
	
	

}
