package eu.xenit.move2alf.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class DestinationConfig {

	@NotEmpty
	private String name;
    private int nbrThreads = 1;

    public String getName() {
		return name;
	}

    public void setName(String name){
        this.name = name;
    }

    public void setNbrThreads(int nbrThreads) {
        this.nbrThreads = nbrThreads;
    }

    public int getNbrThreads() {
        return nbrThreads;
    }
}
