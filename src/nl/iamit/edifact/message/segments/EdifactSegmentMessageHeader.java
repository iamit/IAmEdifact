package nl.iamit.edifact.message.segments;

import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.parsers.EdifactDataElementTranslator;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.ParseException;

public class EdifactSegmentMessageHeader implements EdifactSegment{

	//e.g. 
	//UNH+SRC01110600602+INVOIC:D:02A:UN:DF0811'
	//UNH+SRC12092809809+CLOCKT:003:007:EF'
	private EdifactDataElementTranslator dataElementTranslator;
	private EdifactLineElement edifactLine;
	private String messageNumber; //SRC01110600602
	private String ediMessageType; //INVOIC or CLOCKT
	private String versionNumber; //D or 003
	private String releaseNumber; //02A or 007
	private String organisationId; //UN or EF
	private String organisationDescription; //UN
	private String messageIdentificationId; //DF0811
	private String messageIdentificationDescription; //DF0811

	public EdifactLineElement getMainLine() {
		return edifactLine;
	}

	public String getSegmentIdentifier() {
		return "UNH";
	}

	public int getSubLineCount() {
		return 0;
	}

	public ArrayList<EdifactLineElement> getSubLines() {
		return null;
	}
	
	
	public void createFromData(String ediMessageType,String messageId) throws InvalidArgumentException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
		//e.g. INVOIC:
		//UNH+SRC01110600602+INVOIC:D:02A:UN:DF0811'
		//e.g. CLOCKT:
		//UNH+SRC06060800305+CLOCKT:003:005:EF'
		if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)){
			createFromData(ediMessageType,messageId,"003","005","EF",null);
		}else if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)){
			createFromData(ediMessageType,messageId,"D","02A","UN","DF0811");
		}else{
			throw new InvalidArgumentException("Unknown message type: "+ediMessageType);
		}
	}
	
	public void createFromData(String ediMessageType,String messageId,String versionNr,String releaseNr,String organisationId,String messageIdentificationId) throws InvalidArgumentException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
		//e.g. INVOIC:
		//UNH+SRC01110600602+INVOIC:D:02A:UN:DF0811'
		//e.g. CLOCKT:
		//UNH+SRC06060800305+CLOCKT:003:005:EF'

		if(messageId==null){
			throw new InvalidArgumentException("Message ID cannot be NULL");
		}
		String line="UNH+"+messageId+"+"+ediMessageType+":"+versionNr+":"+releaseNr+":"+organisationId;
		if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)){
			//nothing
		}else if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)){
			line+=":"+messageIdentificationId;
		}else{
			throw new InvalidArgumentException("Unknown message type: "+ediMessageType);
		}
		
		
		edifactLine=new EdifactLineElement(line);
		createFromEdifactLine(edifactLine);
	}
	
	
	public void createFromEdifactLines(ArrayList<EdifactLineElement> edifactLines) throws ParseException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException{
		for(EdifactLineElement line:edifactLines){
			if(line.getLineIdentifier().equals("UNH")){
				edifactLine=line;
				break;
			}
		}
		if(edifactLine==null){
			throw new ParseException("Line UNH not found, not a correct edifact message");
		}

		createFromEdifactLine(edifactLine);
	}
	
	public void createFromEdifactLine(EdifactLineElement eLine) throws AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
		this.edifactLine=eLine;
		//e.g. INVOIC:
		//UNH+SRC01110600602+INVOIC:D:02A:UN:DF0811'
		//e.g. CLOCKT:
		//UNH+SRC06060800305+CLOCKT:003:005:EF'
		messageNumber=edifactLine.getDataElementContent(1,0); //SRC01110600602
		ediMessageType=edifactLine.getDataElementContent(2,0); //INVOIC
		versionNumber=edifactLine.getDataElementContent(2,1); //D
		releaseNumber=edifactLine.getDataElementContent(2,2); //02A
		organisationId=edifactLine.getDataElementContent(2,3); //UN
		if(dataElementTranslator!=null){
			organisationDescription=dataElementTranslator.getDataElementValueAndDescription(organisationId, "0051", false);
		}else{
			organisationDescription="?";
		}
		if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)){
			messageIdentificationId=edifactLine.getDataElementContent(2,4); //DF0811
			if(dataElementTranslator!=null){
				messageIdentificationDescription=dataElementTranslator.getDataElementValueAndDescription(messageIdentificationId,"0057" , false);
			}else{
				messageIdentificationDescription="?";
			}
		}else if(ediMessageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)){
			messageIdentificationId="";
			messageIdentificationDescription="";
		}else{
			throw new ParseException("Unknown message type: "+ediMessageType);
		}

	}
	
	public String getSegmentDescription(){
		String enter = "\r\n";
		String desc="**** UNH Message Header ****"+enter;
		desc+="Original line = "+edifactLine.getLine()+"'"+enter;
		desc+="Message number = "+messageNumber+enter;
		desc+="ediMessageType = "+ediMessageType+enter;
		desc+="version nr. = "+versionNumber+" ; release nr. = "+releaseNumber+enter;
		desc+="organisation = "+organisationId+" ("+organisationDescription+")"+enter;
		desc+="Message identification = "+messageIdentificationDescription+"("+messageIdentificationDescription+")"+enter;
		return desc;
	}
	
	public String getEdiMessageType(){
		return ediMessageType;
	}
	
	public String getMessageNr(){
		return messageNumber;
	}

	public void setEdifactDataElementTranslator(
			EdifactDataElementTranslator dataElementTranslator) {
		this.dataElementTranslator=dataElementTranslator;
		
	}



}
