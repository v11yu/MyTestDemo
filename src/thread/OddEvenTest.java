package thread;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
class MyInteger{
	Integer num;
	public MyInteger(Integer num){
		this.num = num;
	}
	
}
class OddThread implements Runnable{
	private OddEvenTest mylock;
	private String name;
	private MyInteger val;
	public OddThread(String name,OddEvenTest mylock,MyInteger val){
		this.mylock = mylock;
		this.name = name;
		this.val = val;
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			synchronized (mylock) {
				while (val.num > 0) {
					Thread.sleep(2 * 1000);
					if (val.num % 2 == 0) {
						mylock.wait();
					} else {
						System.out.println(name + " " + val.num--);
						mylock.notify();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
}
class EvenThread implements Runnable{
	private OddEvenTest mylock;
	private String name;
	private MyInteger val;
	public EvenThread(String name,OddEvenTest mylock,MyInteger val){
		this.mylock = mylock;
		this.name = name;
		this.val = val;
	}
	public void run() {
		// TODO Auto-generated method stub
		try {
			synchronized (mylock) {
				while (val.num > 1) {
					Thread.sleep(2 * 1000);
					if (val.num % 2 == 1) {
						mylock.wait();
					} else {
						System.out.println(name + " " + val.num--);
						mylock.notify();
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
	
}
/**
 * 一个线程输出奇数
 * 另一个线程输出偶数
 * @author v11
 * @date 2014年9月10日
 * @version 1.0
 */
public class OddEvenTest {
	public static void main(String[] args) {
		MyInteger t = new MyInteger(10);
		OddEvenTest mylock = new OddEvenTest();
		ExecutorService executor = Executors.newCachedThreadPool();
		OddThread odd = new OddThread("odd",mylock,t);
		EvenThread even = new EvenThread("even",mylock,t);
		executor.execute(odd);
		executor.execute(even);
		executor.shutdown();
	}
}
