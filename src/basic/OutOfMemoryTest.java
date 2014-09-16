package basic;

import java.util.ArrayList;
import java.util.List;

import basic.Person;

/**
 * using to test memory leak
 * make processor outofmemory
 * @author v11
 * @date 2014年9月10日
 * @version 1.0
 */
public class OutOfMemoryTest {
	public static void main(String[] args) {
		List<Person> ls = new ArrayList<Person>();
		int count = 0;
		while(true){
			System.out.println(count++);
			ls.add(new Person("hello","@@",10));
		}
	}
}
class Person{
	private String name;
	private String email;
	private Integer age;
	public Person(String name,String email,Integer age){
		this.name = name;
		this.email = email;
		this.age = age;
		
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
}