package nl.iamit.edifact.message.elements;

import java.util.ArrayList;
import java.util.logging.Logger;

import nl.iamit.util.exceptions.InvalidArgumentException;

public class EdifactLineElement {
	private static Logger log = Logger.getLogger(EdifactLineElement.class.getName());
	private String line;
	private ArrayList<EdifactDataElement> dataElements;
	
	public EdifactLineElement(String line) throws InvalidArgumentException{
		if(line==null){
			throw new InvalidArgumentException("Cannot parse a NULL line");
		}
		if(line.length()<3){
			throw new InvalidArgumentException("Cannot parse a line smaller than size 3 (identifier size is minimal 3)");
		}
		if(line.endsWith("+")||line.endsWith(":")){
			while(line.endsWith("+")){
				line=line.substring(0,line.length()-1);
			}
			while(line.endsWith(":")){
				line=line.substring(0,line.length()-1);
			}
		}
		this.line=line.toString();
		if(line.endsWith("'")){
			this.line=line.substring(0,line.length()-1).toString();
		}
		parseLine();
	}
	
	/**
	 * returns the line without the closing "'" 
	 * @return
	 */
	public String getLine(){
		return line;
	}
	
	public void parseLine(){
		dataElements=new ArrayList<EdifactDataElement>();
		String[] elements=line.split("\\+");
		for(String element:elements){
			EdifactDataElement de=new EdifactDataElement(element);
			dataElements.add(de);
		}
	}
	
	public ArrayList<EdifactDataElement> getDataElements(){
		return dataElements;
	}
	
	public int getDataElementCount(){
		if(dataElements==null){
			return 0;
		}
		return dataElements.size();
	}

	public int getDataElementSubElementCount(int indexElement){
		return dataElements.get(indexElement).getSubElementCount();
	}
	
	public String getLineIdentifier(){
		return dataElements.get(0).getSubElement(0);
	}
	
	public EdifactDataElement getDataElement(int index) throws InvalidArgumentException{
		if(index<0){
			throw new InvalidArgumentException("index cannot be negative");
		}
		if(index>=getDataElementCount()){
			throw new InvalidArgumentException("Line has only "+getDataElementCount()+" elements: "+line+"'");
		}
		return dataElements.get(index);
	}

	public String getDataElementContent(int indexElement,int indexSubElement) throws InvalidArgumentException{
		if(indexElement<0||indexSubElement<0){
			throw new InvalidArgumentException("index cannot be negative");
		}
		if(indexElement>=getDataElementCount()){
			throw new InvalidArgumentException("Line has only "+getDataElementCount()+" elements: "+line+"'");
		}
		if(indexSubElement>=dataElements.get(indexElement).getSubElementCount()){
			throw new InvalidArgumentException("DataElement has only "+dataElements.get(indexElement).getSubElementCount()+" subelements, datalement: "+dataElements.get(indexElement).getDataElement()+" of line: "+line+"'");
		}
		return dataElements.get(indexElement).getSubElement(indexSubElement);
	}


}
