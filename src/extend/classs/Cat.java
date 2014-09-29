package extend.classs;

public class Cat extends Animal<CatFood>{
	public static void main(String[] args) {
		Cat c = new Cat();
		CatFood cd = new CatFood();
		c.set(cd);
	}
}
