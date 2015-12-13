package com.maximilian_boehm.ema.in;

import javax.mail.internet.MimeUtility;

public class Decoder {

	public static String decode(String s) {
		if(s== null || s.isEmpty())
			return s;
		
		try {
			if(s.startsWith("\"") && s.endsWith("\"") && s.length()>1)
				s = s.substring(1, s.length()-1);
			
			if(s.contains("=?")) 
				s = MimeUtility.decodeText(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return s;
	}
	
}
