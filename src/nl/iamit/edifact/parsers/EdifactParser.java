package nl.iamit.edifact.parsers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.message.EdifactMessageGeneric;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.message.segments.EdifactSegmentInterchangeHeader;
import nl.iamit.edifact.message.segments.EdifactSegmentMessageHeader;
import nl.iamit.io.IAmFileReader;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.StringFunctions;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

public class EdifactParser {
	private static Logger log = Logger.getLogger(EdifactParser.class.getName());
	public static final String EOL_BASIC = "'";
	public static final String EOL_LF = "'\n";
	public static final String EOL_CR_LF = "'\r\n";
	public static final String LF = "'\n";
	public static final String CR_LF = "'\r\n";
	public static final String CR = "'\r";
	
	public static final int PARSE_ERROR_NO_EKT_MESSAGE = 1;
	public static int last_parse_error=-1;
	
	public static ArrayList<String> parseFiletoLines(String fileName)
			throws IOException, InvalidArgumentException {
		last_parse_error=-1;
		ArrayList<String> lines = new ArrayList<String>();
		String content = IAmFileReader.getFileAsString(fileName);
		if(content==null){
			log.severe("no content in file "+fileName);
			throw new InvalidArgumentException("no content in file "+fileName);
		}
		if(content.length()<=0){
			log.severe("no content in file "+fileName);
			throw new InvalidArgumentException("no content in file "+fileName);
		}
		log.fine(content);
		int countEnd = StringFunctions.countCharsInString(content, '\'');
		if (countEnd == 0) {
			log.severe("No end counts in file "+fileName);
			last_parse_error=PARSE_ERROR_NO_EKT_MESSAGE;
			return lines;
		}
		// int countReturn=StringFunctions.countCharsInString(content, '\r');
		// int countNewLine=StringFunctions.countCharsInString(content, '\n');
		// int countReturnNewLine=StringFunctions.countStringInString(content,
		// "\r\n");
		int countNewLineEnd = StringFunctions.countStringInString(content,
				EOL_LF);
		int countReturnNewLineEnd = StringFunctions.countStringInString(
				content, EOL_CR_LF);
		String splitString = EOL_BASIC;
		if (countEnd == countNewLineEnd) {
			splitString = EOL_LF;
		} else if (countEnd == countReturnNewLineEnd) {
			splitString = EOL_CR_LF;
		} 

		String[] aLines = content.split(splitString);
		for (String line : aLines) {
			if (line == null) {
				continue;
			}
			if (line.length() == 0) {
				continue;
			}
			//to avoid lines that only has CR or LF
			line = line.replaceAll("(\\r|\\n)", "");

			if (line.length() == 0) {
				continue;
			}
			if (line.equals(LF) || line.equals(CR_LF)) {
				continue;
			}
			lines.add(line);
		}
		//log.fine("Last line: [" + lines.get(lines.size() - 1) + "]");
		return lines;
	}
	
	/**
	 * The result of parseFileToLines parsed to ediLines
	 * @param lines
	 * @return
	 * @throws InvalidArgumentException 
	 */
	public static ArrayList<EdifactLineElement> parseLinesToEdifactLines(ArrayList<String> lines) throws InvalidArgumentException{
		if(lines==null){
			throw new InvalidArgumentException("Cannot parse null lines");
		}
		if(lines.isEmpty()){
			throw new InvalidArgumentException("Cannot parse empty lines");
		}

		ArrayList<EdifactLineElement> ediLines=new ArrayList<EdifactLineElement>();
		for(String line:lines){
			ediLines.add(new EdifactLineElement(line));
		}
		return ediLines;
		
	}

	
	/**
	 * Parse a file to the edi FEC lines
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws InvalidArgumentException
	 */
	public static ArrayList<EdifactLineElement> parseFileToEdifactLines(String fileName) throws IOException, InvalidArgumentException{
		last_parse_error=-1;
		return parseLinesToEdifactLines(parseFiletoLines(fileName));
	}
	
	public static EdifactMessage parseFileToBericht(String fileName) throws ParseException, IOException, InvalidArgumentException, AccessControlException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		last_parse_error=-1;
		return parseBerichtFromEdifactLines(parseFileToEdifactLines(fileName));
	}
	
	public static EdifactMessage parseBerichtFromEdifactLines(ArrayList<EdifactLineElement> edifactLines) throws ParseException, AccessControlException, FileNotFoundException, AssertionError, UnknownModeException, InvalidArgumentException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		last_parse_error=-1;
		EdifactSegmentMessageHeader messageHeader=new EdifactSegmentMessageHeader();
		messageHeader.createFromEdifactLines(edifactLines);
		String ediMessageType=messageHeader.getEdiMessageType();
		
		EdifactSegmentInterchangeHeader interchangeHeader=new EdifactSegmentInterchangeHeader();
		interchangeHeader.createFromEdifactLines(edifactLines,ediMessageType);

		EdifactMessage message=new EdifactMessageGeneric();
		message.createFromEdifactLines(interchangeHeader, messageHeader, edifactLines);
		
		return message;

	}
	
}

