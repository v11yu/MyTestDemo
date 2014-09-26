package extend;
abstract class A{
	int g;
}
class B extends A{
	public void show(){
		System.out.println(g);
	}
	public void setG(int v){
		g = v;
	}
}
public class Demo {
	public static void main(String[] args) {
		B b = new B();
		B a = new B();
		A aa = new B();
		a.setG(10);
		a.show();
		b.setG(2);
		b.show();
	}
}
