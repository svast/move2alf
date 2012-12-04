package eu.xenit.move2alf.web.dto;

public class DestinationInfo {
	
	private String destinationName;
	
	private String destinationUrl;
	
	private String destinationValue;
	
	public void setDestinationName(String destinationName){
		this.destinationName=destinationName;
	}
	
	public String getDestinationName(){
		return this.destinationName;
	}
	
	public void setDestinationUrl(String destinationUrl){
		this.destinationUrl=destinationUrl;
	}
	
	public String getDestinationUrl(){
		return this.destinationUrl;
	}
	
	public void setDestinationValue(String destinationValue){
		this.destinationValue=destinationValue;
	}
	
	public String getDestinationValue(){
		return this.destinationValue;
	}
}
