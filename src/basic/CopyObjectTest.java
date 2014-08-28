package basic;
class Bo{
	String s;
}
class AObeject {
	String hello;
	int num;
	Bo b;
	public AObeject(){
		b = new Bo();
	}
	public AObeject(AObeject obj){
		this.hello = obj.hello;
		this.num = obj.num;
		this.b = obj.b;
	}
	public String asText(){
		return hello+" "+num+" "+b.s;
	}
}
/**
 * test copy object , then change it
 * @author v11
 * @date 2014年8月25日
 * @version 1.0
 */
public class CopyObjectTest {
	private void show(AObeject a,AObeject a_copy){
		System.out.println(a.toString() + " "+a.asText() );
		System.out.println("a_copy:"+a_copy.toString() + " "+a_copy.asText());
	}
	public void test() {
		AObeject a = new AObeject();
		a.hello = "hello";
		a.num = 5;
		a.b.s = "ha;";
		AObeject a_copy = a;
		a.num = 6;
		a.hello = "he";
		show(a,a_copy);
		
	}
	public void test1() {
		AObeject a = new AObeject();
		AObeject a_copy = new AObeject(a);
		show(a,a_copy);
		a.num = 6;
		a.hello = "he";
		a.b.s = "ha;";
		show(a,a_copy);
		
	}
	public static void main(String[] args) {
		CopyObjectTest t = new CopyObjectTest();
		t.test1();
	}
}
