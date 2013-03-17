package nl.iamit.edifact.message.elements;

import java.util.ArrayList;

public class EdifactDataElement {
	private String dataElement;
	private ArrayList<String> subElements;
	
	public EdifactDataElement(String _dataElement){
		this.dataElement=_dataElement.toString();
		parseDataElement();
	}
	
	public String getDataElement(){
		return this.dataElement;
	}
	
	public void parseDataElement(){
		subElements=new ArrayList<String>();
		//data element can be empty
		if(dataElement==null){
			subElements.add("");
			return;
		}
		if(dataElement.length()==0){
			subElements.add("");
			return;
		}
		if(dataElement.equals(":")){
			dataElement="";
		}
		if(dataElement.endsWith(":")){
			while(dataElement.endsWith(":")){
				if(dataElement.equals(":")){
					dataElement="";
				}else
				{
					dataElement=dataElement.substring(0,dataElement.length()-1);
				}
			}
		}
		if(!dataElement.contains(":")){
			//Only 1 subelements, just add 1
			subElements.add(dataElement);
			return;
		}
		String[] subs=dataElement.split(":");
		for(String sub:subs){
			//ub elements can also be empty, but must be added (to get the right sub element by its index)
			subElements.add(sub);
		}
	}
	
	public ArrayList<String> getSubElements(){
		return subElements;
	}
	
	public int getSubElementCount(){
		if(subElements==null){
			return 0;
		}
		return subElements.size();
	}
	
	public String getSubElement(int index){
		return subElements.get(index);
	}

}
