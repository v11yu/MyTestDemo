package gephi;

import java.util.ArrayList;
import java.util.List;
class A{
	Integer a;
}
public class MySingleton {
	private static MySingleton unique;
	List<Object> ls;
	private MySingleton(){
		ls = new ArrayList<Object>();
	}
	public static MySingleton getUnique(){
		if(unique == null)
			unique = new MySingleton();
		return unique;
	}
	public static void main(String[] args) {
//		MySingleton gg = MySingleton.getUnique();
//		System.out.println(gg.ls.size());
//		gg.ls.add("haha");
//		System.out.println(MySingleton.getUnique().ls.size());
//		gg = null;
//		System.out.println(MySingleton.getUnique().ls.size());
		A a = null;
		A b = a;
		a = new A();
		b.a = 5;
		System.out.println(b.a);
		a=null;
		System.out.println(b.a);
	}
}
