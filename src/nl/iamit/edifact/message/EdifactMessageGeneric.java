package nl.iamit.edifact.message;

import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.message.segments.EdifactSegment;
import nl.iamit.edifact.message.segments.EdifactSegmentEndOfMessage;
import nl.iamit.edifact.message.segments.EdifactSegmentInterchangeHeader;
import nl.iamit.edifact.message.segments.EdifactSegmentMessageEndTrailer;
import nl.iamit.edifact.message.segments.EdifactSegmentMessageHeader;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

import org.joda.time.DateTime;

public class EdifactMessageGeneric implements EdifactMessage {

	private EdifactSegmentInterchangeHeader interchangeHeader;
	private EdifactSegmentMessageHeader messageHeader;
	private ArrayList<EdifactLineElement> edifactLines;

	public EdifactMessageGeneric() {

	}

	public void createFromEdifactLines(
			EdifactSegmentInterchangeHeader interchangeHeader,
			EdifactSegmentMessageHeader messageHeader,
			ArrayList<EdifactLineElement> edifactLines) throws ParseException,
			AccessControlException, FileNotFoundException, AssertionError,
			UnknownModeException, InvalidArgumentException,
			ClassNotFoundException, SQLException, QueryBeforeSaveException,
			QueryNoDataException, InvalidProcedureCallException {
		this.interchangeHeader = interchangeHeader;
		this.messageHeader = messageHeader;
		this.edifactLines = new ArrayList<EdifactLineElement>();
		if (edifactLines != null) {
			if (!edifactLines.isEmpty()) {
				this.edifactLines.addAll(edifactLines);
			}
		}
	}

	public String getEdiMessageType() {
		return messageHeader.getEdiMessageType();
	}

	public ArrayList<EdifactLineElement> getEdifactLines() {
		return edifactLines;
	}

	public EdifactSegmentEndOfMessage getEndOfMessage() {
		return null;
	}

	public EdifactSegmentInterchangeHeader getInterchangeHeader() {
		return interchangeHeader;
	}

	public ArrayList<String> getLines() {
		ArrayList<String> lines = new ArrayList<String>();
		if(edifactLines!=null){
			if(!edifactLines.isEmpty()){
				for (EdifactLineElement ediLine : edifactLines) {
				lines.add(ediLine.getLine());
				}
			}
		}
		return lines;
	}

	public EdifactSegment getMessageBegin() {
		return null;
	}

	public String getMessageContent() {
		if (edifactLines == null) {
			return "";
		}
		if (edifactLines.isEmpty()) {
			return "";
		}
		StringBuffer buf = new StringBuffer("");
		for (EdifactLineElement ediLine : edifactLines) {
			buf.append(ediLine.getLine());
			buf.append("'\r\n");
		}
		return buf.toString();
	}

	public DateTime getMessageDateTime() {
		return this.interchangeHeader.getDateTimeCreateTransaction();
	}

	public String getMessageDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public EdifactSegmentMessageEndTrailer getMessageEndTrailer() {
		// TODO Auto-generated method stub
		return null;
	}

	public EdifactSegmentMessageHeader getMessageHeader() {
		return messageHeader;
	}

	public String getMessageNr() {
		return messageHeader.getMessageNr();
	}

	public String getTransmissionNr() {
		return interchangeHeader.getTransmissionNr();
	}

}
