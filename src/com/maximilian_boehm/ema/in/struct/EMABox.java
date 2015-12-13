package com.maximilian_boehm.ema.in.struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

public class EMABox {

	public List<EMAFolder> listRootFolder = new ArrayList<EMAFolder>();
	private Map<String, EMAMail> mapMails = new HashMap<String, EMAMail>();
	
	public void addRootFolder(EMAFolder folder){
		listRootFolder.add(folder);
	}
	public List<EMAFolder> getRootFolder(){
		return listRootFolder;
	}
	
	public void addMail(String sKey, EMAMail mail){
		mapMails.put(sKey, mail);
	}
	
	public EMAMail getMail(String sKey){
		return mapMails.get(sKey);
	}
	
	public List<EMAMail> getAllMails(){
		return new ArrayList<EMAMail>(mapMails.values());
	}
	
	public boolean referenceToParent(EMAFolder children) throws MessagingException{
		return referenceRecursiv(getRootFolder(), children);
	}
	private boolean referenceRecursiv(List<EMAFolder> listParent, EMAFolder children) throws MessagingException {
		for(EMAFolder possibleParent:listParent){
			
			if(possibleParent.getFolder().getURLName().equals(children.getFolder().getParent().getURLName())){
				possibleParent.addSubFolder(children);
				return true;
			} else {
				if(!possibleParent.getSubFolder().isEmpty())
					if(referenceRecursiv(possibleParent.getSubFolder(), children))
						return true;
			}
		}
		return false;
		
	}
	
}
