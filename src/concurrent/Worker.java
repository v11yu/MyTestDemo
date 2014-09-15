package concurrent;

public class Worker implements Runnable{
	private Cat cat;
	public Worker(Cat cat){
		this.cat = cat;
	}
	@Override
	public void run() {
		Food.getUnique().create(cat);
		System.out.println(Food.getUnique().getFood(cat));
		Food.cleanUp();
	}
}
