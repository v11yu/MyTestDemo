package basic;

import java.io.ObjectInputStream.GetField;

class Animal{
	public String name = "h";
	protected int age = 5;
	private String color = "red";
	public Animal(){}
	public Animal(String name){
		this.name = name;
	}
	protected void showName(){
		System.out.println("hey,my name is "+name);
	}
	private void showColor(){
		System.out.println("my color is "+ color);
	}
	public void showAge(){
		System.out.println("age "+age);
	}
	public void show(){
		showName();
		showColor();
		showAge();
	}
}
class Cat extends Animal{
	private String color = "green";
	public Cat(){
		this.age = 6;
	}
}
class Cat1 extends Animal{
	protected int age = 5;
	/*
	 *修改this.age，但是父类super没有改变
	 *因此输出还是5 
	 */
	public Cat1(){
		this.age = 6;
	}
}
class Cat2 extends Animal{
	protected int age = 5;

	public Cat2(){
		super.age = 6;
	}
}
class Cat3 extends Animal{
	public Cat3(){
		super("cat");
	}
}
public class ExtendTest {
	public static void main(String[] args) {
		Cat cat = new Cat();
		cat.show();
		Cat1 cat1 = new Cat1();
		cat1.show();
		Cat2 cat2 = new Cat2();
		cat2.show();
		Cat3 cat3 = new Cat3();
		cat3.show();
	}
}
