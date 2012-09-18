package eu.xenit.move2alf.validation;

import java.util.List;

public class TotalStringSizeTestClass {

	@TotalStringSize(max=10, message="Max length is 10")
	private List<String> stringList;

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}
}
