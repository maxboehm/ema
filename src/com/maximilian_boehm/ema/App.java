package com.maximilian_boehm.ema;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.maximilian_boehm.ema.in.IMAPConnector;
import com.maximilian_boehm.ema.in.struct.EMABox;
import com.maximilian_boehm.ema.out.HTMLGenerator;

public class App {
	
	private static EMAConfiguration conf = new EMAConfiguration();
	
	public static EMAConfiguration getConfiguration(){
		return conf;
	}

	public static void main(String[] args) {
		try {
			// READ AND LOAD PROPERTIES FROM FILE
			Properties prop = new Properties();
			try (InputStream is = new FileInputStream("config.properties");){
				prop.load(is);
				conf.setHost(prop.getProperty("host"));
				conf.setUser(prop.getProperty("user"));
				conf.setPassword(prop.getProperty("password"));
				conf.setBaseDir(new File(prop.getProperty("dir")));
				conf.setType(prop.getProperty("type"));
			}
			// START THE APP
			new App().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void start() throws Exception{
		// ENSURE FILE HANDLING
		init();
		// DOWNLOAD EMAILS
		EMABox box = new IMAPConnector().run();
		// GENERATE OUTPUT
		new HTMLGenerator().generate(box);
	}
	
	private void init() throws Exception{
		File fBase = App.getConfiguration().getBaseDir();
		if(!fBase.exists()) fBase.mkdir();
		if(!fBase.exists()) throw new Exception("File could not be created "+fBase.getAbsolutePath());
		
		File fFilesDir = new File(fBase.getAbsolutePath()+"/files");
		fFilesDir.mkdir();
		App.getConfiguration().setFilesDir(fFilesDir);
		
		File fMailsDir = new File(fBase.getAbsolutePath()+"/mails");
		fMailsDir.mkdir();
		App.getConfiguration().setMailsDir(fMailsDir);
			
	}

}
