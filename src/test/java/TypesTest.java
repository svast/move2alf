import java.util.ArrayList;


public class TypesTest {
	class Foo extends ArrayList<String> {

	}
	
	public static void main(String[] args) {
		Foo list = (Foo) new ArrayList<String>();
		list.add("sdfsdf");
		System.out.println(list.get(0));
	}
}
