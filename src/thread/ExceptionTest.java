package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ExceptionThread implements Runnable{
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Integer i = null;
		System.out.println(i/2);
	}
}

public class ExceptionTest {
	public static void main(String[] args) {
		ExecutorService pools = Executors.newCachedThreadPool();
		ExceptionThread a = new ExceptionThread();
		pools.execute(a);
		System.out.println("go here");
		pools.shutdown();
	}
}
