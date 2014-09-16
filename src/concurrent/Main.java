package concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	static final Integer CATNUM = 10;
	public static void main(String[] args) {
		ExecutorService pool = Executors.newCachedThreadPool();
		for(int i=0;i<CATNUM;i++){
			String name = "cat"+i;
			Cat cat = new Cat(name);
			Worker worker = new Worker(cat);
			pool.execute(worker);
		}
		pool.shutdown();
	}
}
