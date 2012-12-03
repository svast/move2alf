package eu.xenit.move2alf.validation;

import java.util.List;

public class ParamListTestClass {

	@ParamList(maxKey=5, maxValue=10, message="Max length is 5-10")
	private List<String> stringList;

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}
}
