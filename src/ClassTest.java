class A{
	
}
class B{
	
}
public class ClassTest {
	public static void PrintClassName(Object obj){
		System.out.println("the class of "+obj+" the name is "
				+obj.getClass().getName());
	}
	public static void main(String[] args) {
		A aa = new A();
		ClassTest.PrintClassName(aa);
	}
}
