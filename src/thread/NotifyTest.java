package thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * notify and notifyAll difference
 * @author v11
 * @date 2014年9月10日
 * @version 1.0
 */
public class NotifyTest {
	public static Object lock = new Object();
	class Waiter implements Runnable{
		String name;
		public Waiter(String name){
			this.name = name;
		}
		public void run() {
			// TODO Auto-generated method stub
			synchronized (lock) {
				
			
			try {
				lock.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(name);
			}
		}
		
	}
	public void testNotify(){
		ExecutorService pool = Executors.newCachedThreadPool();
		for(int i=0;i<5;i++){
			NotifyTest.Waiter tt = new Waiter(i+"");
			pool.execute(tt);
		}
		int i = 0;
		while(i<100){
			i++;
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		synchronized (lock) {
			lock.notify();
		}
		}
		pool.shutdown();
	}
	public void testNotifyAll(){
		ExecutorService pool = Executors.newCachedThreadPool();
		for(int i=0;i<5;i++){
			NotifyTest.Waiter tt = new Waiter(i+"");
			pool.execute(tt);
		}
		int i = 0;
		while(i<100){
			i++;
			try {
				Thread.sleep(2*1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		synchronized (lock) {
			lock.notifyAll();
		}
		}
		pool.shutdown();
	}
	public static void main(String[] args) {
		new NotifyTest().testNotify();
	}
}
