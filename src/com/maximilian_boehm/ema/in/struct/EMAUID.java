package com.maximilian_boehm.ema.in.struct;

import java.util.UUID;

public class EMAUID {
	
	private String UID = UUID.randomUUID().toString().replaceAll("-", "");

	public String getUID(){
		return UID;
	}
	
	public void setUID(String s){
		UID = s;
	}
}
