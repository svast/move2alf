package eu.xenit.move2alf.core.dto;

public class ThreadPool {
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
