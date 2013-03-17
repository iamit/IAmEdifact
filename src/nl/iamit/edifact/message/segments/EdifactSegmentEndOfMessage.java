package nl.iamit.edifact.message.segments;


	import java.io.FileNotFoundException;
	import java.security.AccessControlException;
	import java.sql.SQLException;
	import java.util.ArrayList;

	import nl.iamit.io.exceptions.QueryBeforeSaveException;
	import nl.iamit.io.exceptions.QueryNoDataException;
	import nl.iamit.io.exceptions.UnknownModeException;
	import nl.iamit.util.exceptions.InvalidArgumentException;
	import nl.iamit.util.exceptions.ParseException;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.parsers.EdifactDataElementTranslator;

	public class EdifactSegmentEndOfMessage implements EdifactSegment{
		private EdifactLineElement ediLine;
		private int nrOfMessages;
		private String interchangeTransactionId;	
		
		public EdifactSegmentEndOfMessage(){
			
		}


		public EdifactLineElement getMainLine(){
			return ediLine;
		}

		public void createFromEdifactLines(ArrayList<EdifactLineElement> ediLines) throws ParseException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException{
			for(EdifactLineElement line:ediLines){
				if(line.getLineIdentifier().equals("UNZ")){

						ediLine=line;
						break;
				}
			}
			if(ediLine==null){
				throw new ParseException("Line UNZ not found, not a correct edifact message");
			}
			createFromEdifactLine(ediLine);
		}
		
		public void createFromData(int nrOfMessages,String interchangeTransactionId) throws ParseException, InvalidArgumentException{
			//UNZ+1+SRC01110600602'
			String line="UNZ+"+nrOfMessages+"+"+interchangeTransactionId;
			ediLine=new EdifactLineElement(line);
			createFromEdifactLine(ediLine);
		}
		
		public void createFromEdifactLine(EdifactLineElement eLine) throws ParseException, NumberFormatException, InvalidArgumentException{
			if(!eLine.getLineIdentifier().equals("UNZ")){
				throw new ParseException("Line UNZ not found, not a correct edifact message");
			}
			this.ediLine=eLine;
			nrOfMessages=Integer.parseInt(ediLine.getDataElementContent(1,0));
			interchangeTransactionId=ediLine.getDataElementContent(2,0);
		}

		
		public String getSegmentDescription(){
			String enter = "\r\n";
			String desc="**** UNZ End of message ****"+enter;
			desc+="Original line = "+ediLine.getLine()+"'"+enter;
			desc+="nr Of Messages"+nrOfMessages+enter;
			desc+="interchange Transaction Id"+interchangeTransactionId+enter;	

			return desc;
		}


		public String getSegmentIdentifier() {
			return "UNZ";
		}


		public int getSubLineCount() {
			return 0;
		}


		public ArrayList<EdifactLineElement> getSubLines() {
			return null;
		}


		public void setEdifactDataElementTranslator(
				EdifactDataElementTranslator dataElementTranslator) {
			
		}

	}

