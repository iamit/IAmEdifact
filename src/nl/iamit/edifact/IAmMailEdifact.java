package nl.iamit.edifact;

import java.io.File;
import java.io.IOException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.activation.MailcapCommandMap;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.mail.IAmMailMessageSettings;
import nl.iamit.util.FileFunctions;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

public class IAmMailEdifact {
	private static Logger log = Logger.getLogger(IAmMailEdifact.class.getName());
	
	public static final String MIME_TYPE_APPLICTAION_EDIFACT="application/EDIFACT";
	
	private static String tempPath=null;
	
	public static void setTempPath(String _tempPath){
		tempPath=_tempPath;
		if(_tempPath.endsWith(System.getProperty("file.separator"))){
			tempPath=_tempPath.substring(0,_tempPath.length()-1);
		}
	}
	
	public static String getTempPath() throws InvalidProcedureCallException{
		if(tempPath==null){
			throw new InvalidProcedureCallException("No temp path ste yet, do this first");
		}
		return tempPath;
	}
	
	public static MimeMessage createMessage(Session session,
			IAmMailMessageSettings mailSettings, boolean debug) throws InvalidProcedureCallException, IOException, MessagingException, InvalidArgumentException, AccessControlException, ParseException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException {
		addMailCapLineForEdifact();
		log.fine("Createing edifact mimemessage");
		MimeMessage msg = new MimeMessage(session);
		
		String fileAttach = mailSettings.getAttachedFileNames().get(0);
		if (!FileFunctions.fileExistRefresh(fileAttach)) {
			log.severe("File: " + fileAttach
					+ " not found: caqnnot send without this attachment.");
			throw new InvalidProcedureCallException(
					"Cannot send edifact message without attachment: file not found");
		}
		EdifactDataSource source = new EdifactDataSource(new File(fileAttach));

		EdifactMimePart edifactMimePart=new EdifactMimePart(source); 
		
		msg.setContent(edifactMimePart,source.getContentType());
		msg.saveChanges();
		return msg;
	}
	
	/**
	 * Checks whether the mimetype application/EDIFACT exist
	 * otherwhise adds it
	 */
	public static void addMailCapLineForEdifact(){
		MailcapCommandMap mccm= (MailcapCommandMap) MailcapCommandMap.getDefaultCommandMap();
		boolean found=false;
		for(String 	mimeType:mccm.getMimeTypes()){
			if(mimeType.equals(MIME_TYPE_APPLICTAION_EDIFACT)){
				found=true;
				break;
			}
		}
		if(!found){
			String mailCapLine=MIME_TYPE_APPLICTAION_EDIFACT+";; x-java-content-handler=nl.iamit.edifact.handlers.ApplicationEdifact";
			mccm.addMailcap(mailCapLine);
		}


	}

}
