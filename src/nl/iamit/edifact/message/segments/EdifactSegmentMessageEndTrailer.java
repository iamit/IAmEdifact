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

public class EdifactSegmentMessageEndTrailer implements EdifactSegment{
	private EdifactLineElement ediLine;

	private int nrSegments;
	private String messageId;

	public EdifactSegmentMessageEndTrailer() {
		
	}

	public EdifactLineElement getMainLine(){
		return ediLine;
	}


	public void createFromEdifactLines(ArrayList<EdifactLineElement> ediLines) throws ParseException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException{
		for(EdifactLineElement line:ediLines){
			if(line.getLineIdentifier().equals("UNT")){

					ediLine=line;
					break;

			}
		}
		if(ediLine==null){
			throw new ParseException("Line UNT not found, not a correct edifact message");
		}
		createFromEdifactLine(ediLine);
	}
	
	public void putNrSegments(int nrSegments) throws ParseException, InvalidArgumentException{
		this.nrSegments=nrSegments;
		createFromData(nrSegments,messageId);
		createFromEdifactLine(ediLine);
		
	}
	
	public void createFromData(int nrSegments,String messageId) throws ParseException, InvalidArgumentException{
		//UNT+206+SRC01110600602'
		String line="UNT+"+nrSegments+"+"+messageId;
		ediLine=new EdifactLineElement(line);
		createFromEdifactLine(ediLine);
	}
	
	public void createFromEdifactLine(EdifactLineElement eLine) throws ParseException, NumberFormatException, InvalidArgumentException{
		if(!eLine.getLineIdentifier().equals("UNT")){
			throw new ParseException("Line UNT not found, not a correct edifact message");
		}
		ediLine=eLine;
		nrSegments=Integer.parseInt(ediLine.getDataElementContent(1, 0));
		messageId=ediLine.getDataElementContent(2, 0);
		
	}
	
	public String getSegmentDescription(){
		String enter = "\r\n";
		String desc="**** UNT Message Trailer ****"+enter;
		desc+="Original line = "+ediLine.getLine()+"'"+enter;
		desc+="nr of Segments = "+nrSegments+enter;
		desc+="message Id = "+messageId+enter;

		return desc;
	}

	public String getSegmentIdentifier() {
		return "UNT";
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
