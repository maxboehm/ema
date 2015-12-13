package com.maximilian_boehm.ema.in.struct;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import com.maximilian_boehm.ema.App;
import com.maximilian_boehm.ema.in.Decoder;
import com.sun.mail.gimap.GmailMessage;
import com.sun.mail.imap.IMAPBodyPart;
import com.sun.mail.util.DecodingException;


public class EMAMail extends EMAUID{
	
	public String getLink2MailFile(){
		return "mails/"+getUID()+".html";
	}

	public static EMAMail getMailInstance(Message msg, EMABox box) throws Exception {
		EMAMail mail = new EMAMail();
		
		if(msg instanceof GmailMessage){
			GmailMessage gMsg = (GmailMessage)msg;
			mail.setUID(String.valueOf(gMsg.getMsgId()));
			
			if(box.getMail(mail.getUID())!=null){
				return box.getMail(mail.getUID());
			}
		}
		
		mail.setMsg(msg);
		mail.handleContent(msg.getContent());
		mail.setSubject(Decoder.decode(msg.getSubject()));
		if(msg.getFrom()!=null){
			InternetAddress address = (InternetAddress)msg.getFrom()[0];
			mail.setFrom(new EMAParticipant(address));
		}
		mail.setReceivedDate(msg.getReceivedDate());

		if(msg.getAllRecipients()!=null){
			for (int i = 0; i < msg.getAllRecipients().length; i++){
				InternetAddress address = (InternetAddress)msg.getAllRecipients()[i];
				mail.addRecipients(new EMAParticipant(address));
			}
		}
		
		box.addMail(mail.getUID(), mail);
		return mail;
	}
	
	private List<EMAParticipant> listRecipients = new ArrayList<EMAParticipant>();
	private EMAParticipant from;
	private String subject;
	private String text;
	private Date ReceivedDate;
	private Message msg = null;
	private List<File> listAttachments = new ArrayList<File>();

	
	public EMAParticipant getFrom() {
		return from;
	}

	public void setFrom(EMAParticipant part) {
		this.from = part;
	}
	
	public String getAttachementSize(){
		long lSize = 0;
		for(File f:getAttachments())
			lSize += f.length();
		
	    return new DecimalFormat("#,##0.#").format(lSize/Math.pow(1024, 2)) + " kb";
	}
	
	public List<File> getAttachments(){
		return listAttachments;
	}

	public List<EMAParticipant> getRecipients() {
		return listRecipients;
	}
	public void addRecipients(EMAParticipant recipient) {
		listRecipients.add(recipient);
	}
	public Date getReceivedDate() {
		return ReceivedDate;
	}
	public void setReceivedDate(Date receivedDate) {
		ReceivedDate = receivedDate;
	}
	public Message getMsg() {
		return msg;
	}
	public void setMsg(Message msg) {
		this.msg = msg;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public void addText(String text) {
		if(getText() == null) setText(text);
		else setText(getText()+text);
	}




	private void handleContent(Object obj) throws Exception {
		if(obj instanceof String){
			setText((String)obj);
		} else {
			Multipart multipart = (Multipart)obj;
			for (int j = 0; j < multipart.getCount(); j++) {
				BodyPart bodyPart = multipart.getBodyPart(j);
				String disposition = bodyPart.getDisposition();
				// BodyPart.ATTACHMENT doesn't work for  gmail
				if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT") || disposition.equalsIgnoreCase("inline"))) {
					IMAPBodyPart imapPart = (IMAPBodyPart)bodyPart;
					
					if(imapPart.getContentType().toLowerCase().startsWith("text/plain") || imapPart.getContentType().toLowerCase().startsWith("text/html")){
						addText(imapPart.getContent().toString());
						continue;
					}
						
					String sCompleteFile = imapPart.getFileName();
					
					if(sCompleteFile==null)
						sCompleteFile = "file";
					
					sCompleteFile = Decoder.decode(sCompleteFile);

					String sFileName = sCompleteFile;
					String sFileExtension = "unknown";
					
					int nPositionLastDot = sCompleteFile.lastIndexOf(".");
					if(nPositionLastDot!=-1){
						sFileName = sCompleteFile.substring(0, nPositionLastDot);
						sFileExtension = sCompleteFile.substring(nPositionLastDot, sCompleteFile.length());
					}
					
					sFileName = sFileName.replaceAll("[^a-zA-Z0-9.-]", "_");


					File fFile = new File(App.getConfiguration().getFilesDir().getAbsolutePath()+"/"+sFileName+"_"+getUID()+sFileExtension);
					
					System.out.print("Download '"+sFileName+sFileExtension+"'.");
					long lMilli = System.currentTimeMillis();
					try {
						if(!fFile.exists())
							imapPart.saveFile(fFile);
					} catch (DecodingException e) {
						e.printStackTrace();
					}
					System.out.println(" ... Done in "+(double)(System.currentTimeMillis()-lMilli)/1000+"s, Size: "+((double)fFile.length())/1000000d);
					listAttachments.add(fFile);
				} else{
					if(bodyPart.getContent() instanceof String){
						addText((String)bodyPart.getContent());
					}
					else if(bodyPart.getContent() instanceof MimeMultipart){
						MimeMultipart mmp = (MimeMultipart)bodyPart.getContent();
						handleContent(mmp);
					}else 
						System.out.println("Not yet properly handled: "+bodyPart.getContent().getClass());
				}
				
			}

		}
	}
}
