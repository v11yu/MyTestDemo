package extend;
class Animal{
	String name ;
	public String getName(){
		System.out.println("animal name is "+name);
		return name;
	}
}
class Cat extends Animal{
	String name;
}
public class YieldTest {
	public static void main(String[] args) {
		Cat c = new Cat();
		c.name = "haha";
		c.getName();
	}
}
