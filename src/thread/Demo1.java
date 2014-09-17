package thread;

class B extends Thread{
	@Override
	public void run(){
		doSomething();
	}
	public void doSomething(){}
}
class A extends Thread{
	@Override
	public void run(){
		new B().start();
	}
}
class AA extends Thread{
	@Override
	public void run(){
		new B().doSomething();
	}
}
public class Demo1 {
	public static void main(String[] args) {
		new A().start();
	}
}
