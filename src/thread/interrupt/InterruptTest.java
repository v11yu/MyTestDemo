package thread.interrupt;
/**
 * 无法中断
 * @author v11
 * @date 2014年9月15日
 * @version 1.0
 */
public class InterruptTest extends Thread {
	boolean stop = false;

	public static void useInterrupt() throws InterruptedException{
		InterruptTest thread = new InterruptTest();
		System.out.println("Starting thread...");
		thread.start();
		Thread.sleep(3000);
		System.out.println("Interrupting thread...");
		thread.interrupt();
		Thread.sleep(3000);
		System.out.println("Stopping application...");
		// System.exit(0);
	}
	public static void main(String args[]) throws Exception {
		useInterrupt();
	}

	public void run() {
		while (!stop) {
			System.out.println("Thread is running...");
			long time = System.currentTimeMillis();
			while ((System.currentTimeMillis() - time < 1000)) {
			}
		}
		System.out.println("Thread exiting under request...");
	}
}

