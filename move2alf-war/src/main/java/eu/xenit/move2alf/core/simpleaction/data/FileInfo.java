package eu.xenit.move2alf.core.simpleaction.data;

import java.io.Serializable;
import java.util.HashMap;

public class FileInfo extends HashMap<String, Object> implements Serializable{
	private static final long serialVersionUID = -8480714061115309704L;
	
	public String toString() { 
		String result = "";
		for(String key : this.keySet()) {
			result += key + ":" + this.get(key) + "; ";
		}
		return result;
	}
}
