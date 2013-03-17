package nl.iamit.edifact.message.segments;


import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.parsers.EdifactDataElementTranslator;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.DateFunctions;
import nl.iamit.util.DateTimeFunctions;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.ParseException;

public class EdifactSegmentInterchangeHeader implements EdifactSegment{

		private static Logger log = Logger.getLogger(EdifactSegmentInterchangeHeader.class
				.getName());
		private EdifactDataElementTranslator dataElementTranslator;
		private EdifactLineElement edifactLine;

		private String syntaxIdentification;
		private int syntaxVersionNr;
		private String identificationSenderId;
		private String qualifierSenderId;
		private String qualifierSenderDescription;
		private String identificationReceiverId;
		private String qualifierReceiverId;
		private String qualifierReceiverDescription;

		private String dateCreateTransmission;
		private String timeCreateTransmission;
		private DateTime dateTimeCreateTransmission;
		private String transmissionNr;
		private String applicationReference;
		private boolean isTestMessage = false;

		// example from INVOIC:
		// UNB+UNOC:4+8714231140184:14+8714231236061:14+20060106:1221+SRC01110600602++FHN
		// 1.1'
		// example from CLOCKT:
		// UNB+UNOA:2+8714231140184:14+90924:ZZ+100421:0946+SRC06060800305++FHN
		// 1.10++++1'

		// INVOIC CLOCKT
		// UNB UNB
		// +UNOC:4 UNOA:2
		// +8714231140184:14 8714231140184:14
		// +8714231236061:14 90924:ZZ
		// +20060106:1221 +100421:0946
		// +SRC01110600602 +SRC06060800305
		// + +
		// +FHN 1.1' +FHN 1.10
		// +
		// +
		// +
		// +1'

		public EdifactSegmentInterchangeHeader() {

		}
		
		public void createFromData(String senderId,String receiverId,String transactionId,String messageType) throws AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
			createFromData(senderId,"14",receiverId,"14",transactionId,messageType,"IAM","1.5");
		}
		
		public void createFromData(String senderId,String senderQualifierId,String receiverId,String receiverQualifierId,String transactionId,String messageType,String softwareId,String applicationVersion) throws AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
			// example from INVOIC:
			// UNB+UNOC:4+8714231140184:14+8714231236061:14+20060106:1221+SRC01110600602++FHN
			// 1.1'
			// example from CLOCKT:
			// UNB+UNOA:2+8714231140184:14+90924:ZZ+100421:0946+SRC06060800305++FHN
			// 1.10++++1'
			
			String line="UNB+";
			if(messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)){
				line+="UNOA:2+";
			}else if(messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)){
				line+="UNOC:4+";
			}else{
				throw new InvalidArgumentException("Unknown message type: "+messageType);
			}
			//sender:
			line+=senderId+":"+senderQualifierId+"+";
			//receiver:
			line+=receiverId+":"+receiverQualifierId+"+";
			//transmissionDate:
			String pattern="";
			if(messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)){
				if(dataElementTranslator!=null){
					pattern=dataElementTranslator.translateDateTimePatternToStandards("YYMMDD");
				}else{
					pattern="yyMMdd";
				}
			}else if(messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)){
				if(dataElementTranslator!=null){
					pattern=dataElementTranslator.translateDateTimePatternToStandards("CCYYMMDD");
				}else{
					pattern="yyyyMMdd";
				}
			}else{
				throw new InvalidArgumentException("Unknown message type: "+messageType);
			}
			line+=DateFunctions.convertLocalDateToPattern(DateFunctions.getCurrentDate(), pattern)+":";
			//transmissionTime:
			String timePattern="HHmm";
			if(dataElementTranslator!=null){
			 timePattern=dataElementTranslator.translateDateTimePatternToStandards("HHMM");
			}
			DateTime currentDateTime=DateTimeFunctions.getCurrentDateTime();
			String time=DateTimeFunctions.convertDateTimeToPattern(currentDateTime, timePattern);
			line+=time+"+";
			//transactionId:
			line+=transactionId+"+";
			//?
			line+="+";
			//application
			if(applicationVersion==null){
				line+=softwareId;
			}else{
				if(applicationVersion.length()>0){
					line+=softwareId+" "+applicationVersion;
				}else{
					line+=softwareId;
				}
			}
			edifactLine=new EdifactLineElement(line);
			createFromEdiLine(edifactLine,messageType);
		}
		
		public EdifactLineElement getMainLine(){
			return edifactLine;
		}
		

		
		public void createFromEdiLine(EdifactLineElement eLine,String messageType) throws AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, ParseException{
			this.edifactLine=eLine;
			//CLOCKT example:
			//UNB+UNOA:2+8714231141907:14+449254:ZZ+101124:0747+SRC10112407203++FHR 1.2'

			
			// syntax
			// +UNOC:4
			syntaxIdentification = edifactLine.getDataElementContent(1, 0);
			syntaxVersionNr = Integer.parseInt(edifactLine.getDataElementContent(1, 1));

			// +8714231140184:14
			identificationSenderId = edifactLine.getDataElementContent(2, 0);
			qualifierSenderId = edifactLine.getDataElementContent(2, 1);
			if(dataElementTranslator!=null){
				qualifierSenderDescription = dataElementTranslator.getDataElementValueAndDescription(qualifierSenderId, "0007",true);
			}else{
				qualifierSenderDescription="?";
			}

			// +8714231236061:14
			identificationReceiverId = edifactLine.getDataElementContent(3, 0);
			qualifierReceiverId = edifactLine.getDataElementContent(3, 1);
			if(dataElementTranslator!=null){
				qualifierReceiverDescription = dataElementTranslator.getDataElementValueAndDescription(qualifierReceiverId, "0007",true);
			}else{
				qualifierReceiverDescription="?";
			}


			// INVOIC:
			// +20060106:1221
			// CLOCKT:
			// +100421:0946+

			dateCreateTransmission = edifactLine.getDataElementContent(4, 0);
			timeCreateTransmission = edifactLine.getDataElementContent(4, 1);
			String parsePattern;
			if (messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)) {
				parsePattern = "CCYYMMDD : HHMM";
				if(dataElementTranslator!=null){
					parsePattern=dataElementTranslator
					.translateDateTimePatternToStandards("CCYYMMDD : HHMM");
				}else{
					parsePattern = "yyyyMMdd : HHmm";
				}
					
			} else if (messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)) {
				parsePattern = "YYMMDD : HHMM";
				if(dataElementTranslator!=null){
					parsePattern=dataElementTranslator
					.translateDateTimePatternToStandards("YYMMDD : HHMM");
				}else{
					parsePattern = "yyMMdd : HHmm";
				}
					


			} else {
				throw new InvalidArgumentException("Unknown message type: "
						+ messageType);
			}
			
			dateTimeCreateTransmission = DateTimeFunctions
					.convertPatternToDateTime(
							dateCreateTransmission + " : " + timeCreateTransmission,parsePattern
							);
			// +SRC01110600602
			transmissionNr = edifactLine.getDataElementContent(5, 0);
			// +
			// ? what is this empty field

			// +FHN 1.1'
			applicationReference = edifactLine.getDataElementContent(7, 0);

			// optional field
			isTestMessage = false;
			int size = 0;
			if (messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_INVOIC)) {
				size = 8;
			} else if (messageType.equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT)) {
				size = 10;
			} else {
				throw new ParseException("Unknown message type: " + messageType);
			}

			if (edifactLine.getDataElementCount() > size) {
				int val = Integer.parseInt(edifactLine.getDataElementContent(size + 1,
						0));
				isTestMessage = (val == 1);
			}

			
		}

		public void createFromEdifactLines(ArrayList<EdifactLineElement> edifactLines,
				String messageType) throws ParseException, InvalidArgumentException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException {
			for (EdifactLineElement line : edifactLines) {
				if (line.getLineIdentifier().equals(getSegmentIdentifier())) {
					edifactLine = line;
					break;
				}
			}
			if (edifactLine == null) {
				throw new ParseException(
						"Line UNB not found, not a correct edifact message");
			}
			createFromEdiLine(edifactLine,messageType);
		}
		
		
		public String getSegmentIdentifier() {
			return "UNB";
		}

		//Note: wrong name, should be transmission
		@Deprecated
		public  DateTime getDateTimeCreateTransaction(){
			return dateTimeCreateTransmission;
		}
		public  DateTime getDateTimeCreateTransmission(){
			return dateTimeCreateTransmission;
		}

		public String getSyntaxIdentification() {
			return syntaxIdentification;
		}

		public int getSyntaxVersionNr() {
			return syntaxVersionNr;
		}

		public String getSegmentDescription() {
			String enter = "\r\n";
			String desc = "**** UNB Interchange Header ****" + enter;
			desc += "Original line = " + edifactLine.getLine() + "'" + enter;
			desc += "Syntax: id=" + syntaxIdentification + " versionNr="
					+ syntaxVersionNr + enter;
			desc+="Sender: "+identificationSenderId+" qualification: "+qualifierSenderId+" ("+qualifierSenderDescription+")"+enter;
			desc+="Receiver: "+identificationReceiverId+" qualification: "+qualifierReceiverId+" ("+qualifierReceiverDescription+")"+enter;
			desc+= "Date/time transaction: date="+dateCreateTransmission+" time="+timeCreateTransmission+" ("+dateTimeCreateTransmission.toString()+")"+enter;
			desc+="transactionNr = "+transmissionNr+enter;
			/*if(transactionNr.startsWith("IAm")){
				//a transaction nr form IAmFlora: so must be translated back:
				String clientId="?";
				try {
					clientId = EdiFecFunctions.getClientIdFromTransactionId(transactionNr);
				} catch (InvalidArgumentException e) {
					log.warning(e.getMessage());
					e.printStackTrace();
				}
				desc+="IAmFlora client ID = "+clientId+enter;
			}*/
			desc+="application = "+applicationReference+enter;
			if(isTestMessage){
				desc+="This is a Test Message!"+enter;
			}
			return desc;
		}

		public String getTransmissionNr() {
			return transmissionNr;
		}
		
		public String getSenderIdentification() {
			return identificationSenderId;
		}
		public String getSenderIdentificationQualifier() {
			return qualifierSenderId;
		}
		public String getSenderQualifierDescription() {
			return qualifierSenderDescription;
		}
		public String getReceiverIdentification() {
			return identificationReceiverId;
		}
		public String getReceiverIdentificationQualifier() {
			return qualifierReceiverId;
		}
		public String getReceiverQualifierDescription() {
			return qualifierReceiverDescription;
		}


		public void createFromEdifactLines(
				ArrayList<EdifactLineElement> edifactLines)
				throws ParseException, AccessControlException,
				FileNotFoundException, AssertionError, UnknownModeException,
				InvalidArgumentException, ClassNotFoundException, SQLException,
				QueryBeforeSaveException, QueryNoDataException {
			throw new InvalidArgumentException("Use the procedure with the type of message");
			
		}


		public int getSubLineCount() {
			return 0;
		}

		public ArrayList<EdifactLineElement> getSubLines() {
			return null;
		}

		public void setEdifactDataElementTranslator(
				EdifactDataElementTranslator dataElementTranslator) {
			this.dataElementTranslator=dataElementTranslator;
			
		}

	}
