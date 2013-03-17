package test.nl.iamit.edifact.parsers;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import nl.iamit.edifact.message.EdifactMessage;
import nl.iamit.edifact.message.EdifactMessageGeneric;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.edifact.parsers.EdifactParser;
import nl.iamit.io.exceptions.QueryBeforeSaveException;
import nl.iamit.io.exceptions.QueryNoDataException;
import nl.iamit.io.exceptions.UnknownModeException;
import nl.iamit.util.exceptions.InvalidArgumentException;
import nl.iamit.util.exceptions.InvalidProcedureCallException;
import nl.iamit.util.exceptions.ParseException;

import org.junit.Test;

public class TestEdifactParser {

	private static Logger log = Logger.getLogger(TestEdifactParser.class.getName());
	private static String testDataPath = System.getProperty("user.home")+ System.getProperty("file.separator") +"testData"+System.getProperty("file.separator") +"IAmFlora";
	private static String testDataPathFlorecom = testDataPath+System.getProperty("file.separator")+"florecom";
	private static String testDataPathCodeList =testDataPath+System.getProperty("file.separator")+"testCodeLists";
	//NOTE: testDataPathEdiFact is in repo of product IAmFlora (not in IAmIO)
	private static String testDataPathEdiFact =testDataPath+System.getProperty("file.separator")+"testEdiFact";
	private static String testDataPathEdiFactEkt =testDataPathEdiFact+System.getProperty("file.separator")+"messages"+System.getProperty("file.separator")+"ekt";
	

	@Test
	public void setUp() throws Exception {
		// get the top Logger:
		Logger topLogger = java.util.logging.Logger.getLogger("");
		topLogger.setLevel(java.util.logging.Level.INFO);

		Logger topLoggerIAmIT = java.util.logging.Logger.getLogger("nl.iamit");
		topLoggerIAmIT.setLevel(java.util.logging.Level.FINEST);
		Logger topLoggerTest = java.util.logging.Logger
				.getLogger("test.nl.iamit");
		topLoggerTest.setLevel(java.util.logging.Level.FINEST);

		// Handler for console (reuse it if it already exists)
		Handler consoleHandler = null;
		// see if there is already a console handler
		for (Handler handler : topLogger.getHandlers()) {
			if (handler instanceof ConsoleHandler) {
				// found the console handler
				consoleHandler = handler;
				break;
			}
		}

		if (consoleHandler == null) {
			// there was no console handler found, create a new one
			consoleHandler = new ConsoleHandler();
			topLogger.addHandler(consoleHandler);
		}
		// set the console handler to fine:
		consoleHandler.setLevel(java.util.logging.Level.FINEST);

	}
	
	@Test
	public void testParseFactuurbericht() throws IOException, InvalidArgumentException, ParseException, AccessControlException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{
		String messagePath=testDataPathEdiFact +System.getProperty("file.separator")+"messages"+System.getProperty("file.separator")+"factuurbericht";
		String fileName="TEST001_FH_BB_voorbeeld_CM.edi";
		//parse the file in to lines
		ArrayList<String> lines=EdifactParser.parseFiletoLines(messagePath+System.getProperty("file.separator")+fileName);
		assertTrue("Expected="+208+" result="+lines.size(),lines.size()==208);

		ArrayList<EdifactLineElement> ediLines=EdifactParser.parseLinesToEdifactLines(lines);
		assertTrue("Expected="+208+" result="+lines.size(),lines.size()==208);
		
		assertEquals(ediLines.get(0).getLine(),"UNB+UNOC:4+8714231140184:14+8714231236061:14+20060106:1221+SRC01110600602++FHN 1.1");
		assertTrue("Expected=8, result="+ediLines.get(0).getDataElements().size(),ediLines.get(0).getDataElements().size()==8);
		assertTrue("Expected=1, result="+ediLines.get(0).getDataElements().get(0).getSubElements().size(),ediLines.get(0).getDataElements().get(0).getSubElements().size()==1);
		assertEquals(ediLines.get(0).getDataElements().get(0).getSubElements().get(0),"UNB");
		assertEquals(ediLines.get(0).getDataElementContent(0,0),"UNB");
		
		EdifactMessageGeneric factuurBericht=(EdifactMessageGeneric)EdifactParser.parseBerichtFromEdifactLines(ediLines); 
		//factuurBericht=new EdiFecFactuurBericht(); 
		//factuurBericht.createFromEdiLines(ediLines);
		
		assertEquals(factuurBericht.getInterchangeHeader().getSyntaxIdentification(),"UNOC");
		assertEquals(factuurBericht.getInterchangeHeader().getSyntaxVersionNr(),4);
	}

	@Test
	public void testParseEktMessage() throws AccessControlException, ParseException, IOException, InvalidArgumentException, AssertionError, UnknownModeException, ClassNotFoundException, SQLException, QueryBeforeSaveException, QueryNoDataException, InvalidProcedureCallException{

		//file name test message:
		String fileNameEktMessage="om101208.0656";
		String fileName=testDataPathEdiFactEkt +System.getProperty("file.separator")+fileNameEktMessage;
		
		//NOTE: testdata is in repo of product IAmFlora (not in IAmIO)
		
		//note, test message is from cor (), on 8-12-2010 content is:
			//UNB+UNOA:2+8714231141907:14+449254:ZZ+101208:0619+SRC10120801622++FHR 1.2'
			//UNH+SRC10120801628+CLOCKT:003:005:EF'
			//BGM+493'
			//DTM+97:20101208:102'
			//NAD+BY+449254'
			//NAD+FLA+3'
			//LIN+++16207:VBN'
			//DTM+9:061800:402'
			//NAD+MF+8622'
			//RFF+ACE:122254'
			//RFF+ADZ:1178'
			//RFF+AGJ:2603'
			//RFF+BT:00301342026030000002'
			//RFF+CTS:157'
			//RFF+FAC:1'
			//RFF+FAN:428'
			//QTY+52:50'
			//QTY+66:2'
			//PRI+INV:0.560'
			//IMD++S99+:::R GR AVALANCHE?+'
			//IMD++S20+070'
			//IMD++L11+010'
			//IMD++S05+033'
			//IMD++S62+NL'
			//IMD++S98+A1'
			//IMD++K11+A'
			//PAC+++997'
			//PRI+PAP:7.000'
			//EQD+BX++1'
			//UNT+29+SRC10120801628'
			//UNZ+1+SRC10120801622'
		EdifactMessage message= EdifactParser.parseFileToBericht(fileName);
		
		assertFalse(message==null);
		assertTrue("class = "+message.getClass().getName(),message instanceof EdifactMessageGeneric);

		assertTrue(message.getEdiMessageType().equals(EdifactMessage.EDIFACT_MESSAGE_TYPE_CLOCKT));
	}
	
	
	
	
}
