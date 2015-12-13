package com.maximilian_boehm.ema.in.struct;

import javax.mail.internet.InternetAddress;

import com.hp.gagawa.java.elements.A;
import com.maximilian_boehm.ema.in.Decoder;

public class EMAParticipant {
	
	private String Name;
	private String Mail;
	
	public EMAParticipant(InternetAddress address) {
		setName(address.getPersonal());
		setMail(address.getAddress());
	}

	public String getLink(){
		A link = new A().setHref("mailto:"+getMail());
		if(getName()!=null)
			link.appendText(getName());
		else
			link.appendText(getMail());
		
		return link.write();
	}
	
	public String getName() {
		if(Name==null)
			return getMail();
		return Name;
	}
	public void setName(String name){
		Name = Decoder.decode(name);
	}
	public String getMail() {
		return Mail;
	}
	public void setMail(String mail) {
		Mail = Decoder.decode(mail);
	}

}
