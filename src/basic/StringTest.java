package basic;
class StringObecjt{
	String s;
}
public class StringTest {
	public static void change(String s){
		s ="a";
	}
	public static void change(StringObecjt s){
		s.s = "haha";
	}
	public static void main(String[] args) {
		String s = new String("aa");
		change(s);
		System.out.println(s);
		StringObecjt ss = new StringObecjt();
		ss.s = "aa";
		change(ss);
		System.out.println(ss.s);
	}
}
