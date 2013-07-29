package eu.xenit.move2alf.web.dto;

public class DestinationInfo {
	
	private String name;
	
	private String url;

    private String type;

    private String userName;

    private int threads;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public String getUrl(){
		return this.url;
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

	public void setThreads(int threads){
		this.threads = threads;
	}
	
	public int getThreads(){
		return this.threads;
	}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
