package com.maximilian_boehm.ema.in.struct;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.mail.Folder;

public class EMAFolder extends EMAUID{
	
	public EMAFolder(Folder f) {
		setFolder(f);
		setName(f.getFullName());
	}
	public void addingMailsDone(){
		Collections.reverse(listMail);
	}
	
	public String name;
	private Folder folder;
	private List<EMAMail> listMail = new ArrayList<EMAMail>();
	private List<EMAFolder> subFolder = new ArrayList<EMAFolder>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addMail(EMAMail mail){
		listMail.add(mail);
	}
	public List<EMAMail> getMail() {
		return listMail;
	}
	
	public List<EMAFolder> getSubFolder() {
		return subFolder;
	}
	public void addSubFolder(EMAFolder folder) {
		subFolder.add(folder);
	}
	public Folder getFolder() {
		return folder;
	}
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
}
