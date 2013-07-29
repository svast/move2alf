package eu.xenit.move2alf.web.dto;

import org.hibernate.validator.constraints.NotEmpty;

public class DestinationConfig {

	@NotEmpty
	private String name;

	@NotEmpty
	private String type;

	public String getName() {
		return name;
	}

    public void setName(String name){
        this.name = name;
    }

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}


}
