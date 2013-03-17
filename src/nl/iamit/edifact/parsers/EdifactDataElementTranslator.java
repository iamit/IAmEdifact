package nl.iamit.edifact.parsers;

import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.sql.SQLException;

import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;

public interface EdifactDataElementTranslator {

	public String getDataElementValueAndDescription(String qualifierId,
			String dataElementCode, boolean includeRemarks)
			throws AccessControlException, FileNotFoundException,
			AssertionError, UnknownModeException, InvalidArgumentException,
			ClassNotFoundException, SQLException, QueryBeforeSaveException,
			QueryNoDataException;
	
	public String translateDateTimePatternToStandards(String nonStandardPattern)  throws InvalidArgumentException ;
}
