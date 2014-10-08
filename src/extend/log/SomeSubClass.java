package extend.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SomeSubClass extends SomeSuperClass{
	//protected Logger log = LoggerFactory.getLogger(SomeSuperClass.class);
	public static void main(String[] args) {
		new SomeSubClass().show();
		new SomeSuperClass().show();
	}
}
