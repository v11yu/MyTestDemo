package thread.interrupt;

class Example3 extends Thread {

	volatile boolean stop = false;

	public static void main(String args[]) throws Exception {

		Example3 thread = new Example3();

		System.out.println("Starting thread...");

		thread.start();

		Thread.sleep(3000);

		System.out.println("Asking thread to stop...");

		thread.stop = true;// 如果线程阻塞，将不会检查此变量

		thread.interrupt();

		Thread.sleep(3000);

		System.out.println("Stopping application...");

		// System.exit( 0 );

	}

	public void run() {
		try {
			while (!stop) {

				System.out.println("Thread running...");

				Thread.sleep(18000);

			}
		} catch (InterruptedException e) {

			System.out.println("Thread interrupted...");

		}finally{
			System.out.println("doing some sth when close the tread,example : free memory");
		}

		System.out.println("Thread exiting under request...");

	}

}
