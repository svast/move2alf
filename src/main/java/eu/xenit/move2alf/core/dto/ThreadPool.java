package eu.xenit.move2alf.core.dto;

import eu.xenit.move2alf.common.IdObject;

public class ThreadPool extends IdObject {
	private String name = "";

	private int size = 0;
	
	public ThreadPool() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
}
