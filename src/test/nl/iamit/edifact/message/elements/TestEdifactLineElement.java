package test.nl.iamit.edifact.message.elements;
import static org.junit.Assert.*;
import nl.iamit.edifact.message.elements.EdifactLineElement;
import nl.iamit.util.exceptions.InvalidArgumentException;

import org.junit.Test;

public class TestEdifactLineElement {



		@Test
		public void testEdiLineImd() throws InvalidArgumentException{
			//IMD++S99+:::GE MI KIMSEY'
			String line="IMD++S99+:::GE MI KIMSEY";
			EdifactLineElement eLine=new EdifactLineElement(line);
			assertEquals(eLine.getDataElementCount(),4);
			assertEquals(eLine.getDataElementSubElementCount(0),1);
			assertEquals(eLine.getDataElementSubElementCount(1),1);
			assertEquals(eLine.getDataElementSubElementCount(2),1);
			assertEquals(eLine.getDataElementSubElementCount(3),4);
			assertEquals(eLine.getDataElementContent(3, 3),"GE MI KIMSEY");
		}
		
		@Test
		public void testEdiLineWithAnsWithoutQuote() throws InvalidArgumentException
		{
			String line="LIN+++2551:VBN'";
			EdifactLineElement eLine=new EdifactLineElement(line);
			String itemCode = eLine.getDataElementContent(3, 0);
			String itemCodeType = eLine.getDataElementContent(3, 1);
			assertEquals(itemCode,"2551");
			assertEquals(itemCodeType,"VBN");

			line="LIN+++2551:VBN";
			eLine=new EdifactLineElement(line);
			itemCode = eLine.getDataElementContent(3, 0);
			itemCodeType = eLine.getDataElementContent(3, 1);
			assertEquals(itemCode,"2551");
			assertEquals(itemCodeType,"VBN");
			
		}
	}
