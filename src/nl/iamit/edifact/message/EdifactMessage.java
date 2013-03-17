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

public interface EdifactMessage {
	
	public static final String EDIFACT_MESSAGE_TYPE_INVOIC = "INVOIC";
	public static final String EDIFACT_MESSAGE_TYPE_CLOCKT = "CLOCKT";
	public EdifactSegmentInterchangeHeader getInterchangeHeader();
	public EdifactSegmentMessageHeader getMessageHeader();
	public EdifactSegment getMessageBegin();
	
	public EdifactSegmentMessageEndTrailer getMessageEndTrailer();
	public EdifactSegmentEndOfMessage getEndOfMessage();
	
	
	public String getEdiMessageType();
	
	public String getMessageDescription();
	
	public String getMessageContent();
	
	public DateTime getMessageDateTime();
	
	public String getMessageNr();
	
	public String getTransmissionNr();
	
	public ArrayList<String> getLines();
	
	public ArrayList<EdifactLineElement> getEdifactLines();
	
	public void createFromEdifactLines(EdifactSegmentInterchangeHeader interchangeHeader,EdifactSegmentMessageHeader messageHeader,ArrayList<EdifactLineElement> edifactLines) throws ParseException,AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException,InvalidProcedureCallException;


}
