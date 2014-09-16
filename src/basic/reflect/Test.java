package basic.reflect;

import java.lang.reflect.Field;

public class Test {
	public static void demo1() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Cat cat = new Cat();
		
		System.out.println(cat.getName());
		Field personNameField = Cat.class.getDeclaredField("name");
		personNameField.setAccessible(true);
		personNameField.set(cat, "胖虎先森");
		System.out.println(cat.getName());
	}
	public static void demo2() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Cat cat = new Cat();
		
		System.out.println(cat.getAge());
		Field personNameField = Cat.class.getDeclaredField("age");
		personNameField.setAccessible(true);
		personNameField.set(cat, 20);
		System.out.println(cat.getAge());
	}
	public static void main(String[] args) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		demo2();
	}
}
class Cat{
	private String name = "hello";
	private static Integer age = 5;
	public String getName(){
		return name;
	}
	public static Integer getAge(){
		return age;
	}
}
