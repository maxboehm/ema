package com.maximilian_boehm.ema.out;


import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.DocumentType;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Div;
import com.hp.gagawa.java.elements.H1;
import com.hp.gagawa.java.elements.H2;
import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.P;
import com.hp.gagawa.java.elements.Script;
import com.hp.gagawa.java.elements.Style;
import com.hp.gagawa.java.elements.Table;
import com.hp.gagawa.java.elements.Tbody;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Text;
import com.hp.gagawa.java.elements.Tfoot;
import com.hp.gagawa.java.elements.Th;
import com.hp.gagawa.java.elements.Thead;
import com.hp.gagawa.java.elements.Title;
import com.hp.gagawa.java.elements.Tr;
import com.hp.gagawa.java.elements.Ul;
import com.maximilian_boehm.ema.App;
import com.maximilian_boehm.ema.in.struct.EMABox;
import com.maximilian_boehm.ema.in.struct.EMAFolder;
import com.maximilian_boehm.ema.in.struct.EMAMail;
import com.maximilian_boehm.ema.in.struct.EMAParticipant;


/**
 * File-Structure
 * 
 * index.html
 * mails/
 *    UID.html
 *    UID_1.html
 * files/
 *    UID_COUNTER_FILENAME.html
 *    UID1_COUNTER_FILENAME.html
 *    UID2_COUNTER_FILENAME.html
 * 	 
 * 
 * @author Max
 *
 */

public class HTMLGenerator {
	
	public void generate(EMABox box) throws Exception{
		Document doc = getBaseDocument("E-Mail Archive");

		// CREATE INDEX.HTML
		// LINK TO
		// A) ALL FILES
		// B) FOLDER HIERARCHY
		
		
		Div div = new Div().setId("wrapper");
		
		div.appendChild(new H1().appendText("E-Mail Archiver"));
		div.appendChild(new H2().appendText("Created on "+new GregorianCalendar()));
		div.appendChild(new P().appendText(App.getConfiguration().toHTML()));
		
		Ul list = new Ul();
		String sLinkAll = handleFolder("All Mails", "ALL", box.getAllMails());
		list.appendChild(new Li().appendChild(new A().setHref(sLinkAll).appendText("All Mails")));
		for(EMAFolder folder:box.getRootFolder())
			createTree(folder, list);
		div.appendChild(list);
		
		doc.body.appendChild(div);
		doc.body.appendChild(new Div().setId("content"));
		
		String sPath = App.getConfiguration().getBaseDir().getPath()+"/index.html";
		String sResult = doc.write();
		Files.write(Paths.get(sPath), sResult.getBytes("UTF-16"));
		
	}
	
	private void createTree(EMAFolder folder, Ul list) throws Exception{
		String sLink = handleFolder(folder.getName(), folder.getUID(), folder.getMail());
		
		Li li = new Li();
		li.appendChild(new A().setHref(sLink).appendText(folder.getName()));
		
		if(!folder.getSubFolder().isEmpty()){
			Ul subList = new Ul();
			for(EMAFolder subFolder:folder.getSubFolder()){
				createTree(subFolder, subList);
			}
			li.appendChild(subList);
		}
		
		list.appendChild(li);
		
	}
	
	private String handleFolder(String sFolderName, String sFolderUID, List<EMAMail> listMail) throws Exception {
		
		Document doc = getBaseDocument(sFolderName);

		Div div = new Div().setId("wrapper");
		div.appendChild(new H1().appendChild(new A().setHref("index.html").appendText("&laquo;")).appendText(sFolderName));

		
		// LIST MAILS BY FOLDER
		Table tbl = new Table().setId("example").setCSSClass("display").setCellspacing("0").setWidth("100%");
		
		Thead th = new Thead();
		Tr ThTr = new Tr();
		ThTr.appendChild(new Th().appendText("From"));
		ThTr.appendChild(new Th().appendText("Date"));
		ThTr.appendChild(new Th().appendText("Subject"));
		ThTr.appendChild(new Th().appendText("Attachement"));
		ThTr.appendChild(new Th().appendText("Read Mail"));
		
		th.appendChild(ThTr);
		tbl.appendChild(th);

		Tfoot tf = new Tfoot();
		Tr TfTr = new Tr();
		TfTr.appendChild(new Th().appendText("From"));
		TfTr.appendChild(new Th().appendText("Date"));
		TfTr.appendChild(new Th().appendText("Subject"));
		TfTr.appendChild(new Th().appendText("Attachement"));
		TfTr.appendChild(new Th().appendText("Read Mail"));
		
		tf.appendChild(TfTr);
		tbl.appendChild(tf);
		
		Tbody tb = new Tbody();
		
		// System.out.println(folder.getFolder().getName()+" "+folder.getMail().size());
		for(EMAMail mail:listMail){
			Tr trMail = new Tr();
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
			
			String sDate = mail.getReceivedDate()==null
					? "UNBEKANNT"
					: df.format(mail.getReceivedDate());
			
			
			trMail.appendChild(new Td().appendText(mail.getFrom().getLink()));
			trMail.appendChild(new Td().appendText(sDate));
			trMail.appendChild(new Td().appendText(mail.getSubject()));
			trMail.appendChild(new Td().appendText(mail.getAttachementSize()));
			A link = new A().setHref("mails/"+mail.getUID()+".html").setCSSClass("fancybox").appendText("READ MAIL");
			link.setAttribute("data-fancybox-type", "iframe");
			trMail.appendChild(new Td().appendChild(link));
			createMailFile(mail);
			
			tbl.appendChild(trMail);
		}
		tbl.appendChild(tb);
		div.appendChild(tbl);
		doc.body.appendChild(div);
		
		String sFileName = sFolderName+"_"+sFolderUID+".html";
		String sPath = App.getConfiguration().getBaseDir().getPath()+"/"+sFileName;
		String sResult = doc.write();
		Files.write(Paths.get(sPath), sResult.getBytes("UTF-16"));

		return sFileName;
	}
	
	private void createMailFile(EMAMail mail) throws Exception{
		Document doc = getBaseDocument(mail.getFrom().getName()+": "+mail.getSubject());
		
		Div div = new Div().setCSSClass("mail-container");
		
		Table tbl = new Table();
		
		if(mail.getSubject()!= null)
			tbl.appendChild(new Tr().appendChild(new Td().appendText("Subject")).appendChild(new Td().appendText(mail.getSubject())));
		
		tbl.appendChild(new Tr().appendChild(new Td().appendText("From")).appendChild(new Td().appendText(mail.getFrom().getLink())));
			
		if(mail.getReceivedDate()!=null){
			SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy, k:m");
			tbl.appendChild(new Tr().appendChild(new Td().appendText("Date")).appendChild(new Td().appendText(df.format(mail.getReceivedDate()))));
		}
		
		List<File> listFiles = mail.getAttachments();
		if(!listFiles.isEmpty()){
			String sLinks = "";
			for(File f:listFiles){
				if(!sLinks.isEmpty()) sLinks += ", ";
				sLinks += new A().setHref("../files/"+f.getName()).appendText(f.getName().replace(mail.getUID(), "")).write();
			}
			
			tbl.appendChild(new Tr().appendChild(new Td().appendText("Attachement")).appendChild(new Td().appendText(sLinks)));
		}
		
		List<EMAParticipant> listRecipients = mail.getRecipients();
		if(!listRecipients.isEmpty()){
			String sLinks = "";
			for(EMAParticipant participant:listRecipients){
				if(!sLinks.isEmpty()) sLinks += ", ";
				sLinks += participant.getLink();
			}
			
			tbl.appendChild(new Tr().appendChild(new Td().appendText("Recipients")).appendChild(new Td().appendText(sLinks)));
		}
		
		div.appendChild(tbl);
		
		Div content = new Div().setCSSClass("content content-mail");
		String sContent = mail.getText();
		if(sContent!=null){
			if(!sContent.startsWith("<html"))
				sContent = sContent.replaceAll("(\\r\\n|\\n)", "<br />");
			content.appendChild(new Text(sContent));
			div.appendChild(content);
		}
		
		doc.body.appendChild(div);
		
		String sPath = App.getConfiguration().getMailsDir().getPath()+"/"+mail.getUID()+".html";
		String sResult = doc.write();
		Files.write(Paths.get(sPath), sResult.getBytes("UTF-16"));
	}
	
	private Document getBaseDocument(String sTitle) throws Exception{
		Document doc = new Document(DocumentType.HTMLStrict);
		doc.head.appendChild(new Title().appendText(sTitle));
		doc.head.appendChild(new Script("text/javascript").appendChild(new Text(getText("jquery.js"))));
		doc.head.appendChild(new Script("text/javascript").appendChild(new Text(getText("jquery.fancybox.pack.js"))));
		doc.head.appendChild(new Script("text/javascript").appendChild(new Text(getText("datatable.js"))));
		doc.head.appendChild(new Script("text/javascript").appendChild(new Text(getText("script.js"))));
		doc.head.appendChild(new Style("text/css").appendChild(new Text(getText("style.css"))));
		doc.head.appendChild(new Style("text/css").appendChild(new Text(getText("jquery.fancybox.css"))));
		doc.head.appendChild(new Style("text/css").appendChild(new Text(getText("datatable.css"))));
		return doc;
	}
	
	private String getText(String sFileName) throws Exception{
		try (InputStream is = HTMLGenerator.class.getResourceAsStream(sFileName)){
			return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
		}
	}

}
