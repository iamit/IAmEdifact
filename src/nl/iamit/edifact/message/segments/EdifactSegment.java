package nl.iamit.edifact.message.segments;

import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;

import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.parsers.EdifactDataElementTranslator;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.ParseException;

public interface EdifactSegment {
	
	public String getSegmentIdentifier();
	public EdifactLineElement getMainLine();
	public ArrayList<EdifactLineElement> getSubLines();
	public int getSubLineCount();
	public void createFromEdifactLines(ArrayList<EdifactLineElement> edifactLines) throws ParseException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException;
	public String getSegmentDescription();
	public void setEdifactDataElementTranslator(EdifactDataElementTranslator dataElementTranslator);

}