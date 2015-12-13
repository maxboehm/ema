package com.maximilian_boehm.ema.in;

import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

import com.maximilian_boehm.ema.App;
import com.maximilian_boehm.ema.in.struct.EMABox;
import com.maximilian_boehm.ema.in.struct.EMAFolder;
import com.maximilian_boehm.ema.in.struct.EMAMail;

public class IMAPConnector {

	public EMABox run() {
		EMABox box = new EMABox();
		try {

			Properties props = new Properties();

			String sType = App.getConfiguration().getType();
			String sPrefix = "mail."+sType+".";
			props.put("mail.store.protocol", sType);
			
			props.put(sPrefix+"starttls.enable", true);
			props.put(sPrefix+"host", App.getConfiguration().getHost());
			props.put(sPrefix+"port", 993);
			props.put(sPrefix+"auth", true);
			props.put(sPrefix+"starttls.enable", true);
			props.put(sPrefix+"host", App.getConfiguration().getHost());
			props.put(sPrefix+"port", 993);
			props.put(sPrefix+"auth", true);
			props.put(sPrefix+"partialfetch", false);
			props.put(sPrefix+"connectionpoolsize", 5);
			props.put(sPrefix+"fetchsize", 2619200);
			
			props.put("mail.debug", false);
			
			Session session = Session.getDefaultInstance(props, new IMAPAuthenticator(App.getConfiguration().getUser(),App.getConfiguration().getPassword()));
			
			Store store = session.getStore(sType);
			store.connect();

			javax.mail.Folder[] folders = store.getDefaultFolder().list("*");
//			javax.mail.Folder[] folders = store.getDefaultFolder().list("INBOX");
			for (javax.mail.Folder folder : folders) {
				if ((folder.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
					Folder jFolder = store.getFolder(folder.getURLName());
					
					// Create new instance by jFolder
					EMAFolder emaFolder = new EMAFolder(jFolder);

					if(!jFolder.getParent().getName().isEmpty())
						box.referenceToParent(emaFolder);
					else
						box.addRootFolder(emaFolder);
					
				    jFolder.open(Folder.READ_ONLY);
				    Message[] messages = jFolder.getMessages(1, jFolder.getMessageCount());
	
				    FetchProfile fp = new FetchProfile();
				    fp.add(FetchProfile.Item.ENVELOPE);
				    fp.add(FetchProfile.Item.CONTENT_INFO);
				    fp.add(FetchProfile.Item.FLAGS);
				    fp.add("X-mailer");
				    
				    // Load messages in a batch
				    jFolder.fetch(messages, fp);
				    for (Message message: messages)
				       emaFolder.addMail(EMAMail.getMailInstance(message, box));
					
					emaFolder.addingMailsDone();
					jFolder.close(true);
				}
			}

			store.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return box;
	}
}

