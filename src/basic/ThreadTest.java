package basic;
class A extends Thread{
	int a = 2;
	@Override
	public void run() {
		a++;
		show();
	}
	private void show(){
		System.out.println(a);
	}
}
/**
 * 多线程测试
 * @author v11
 * @date 2014年8月22日
 * @version 1.0
 */
public class ThreadTest {
	public static void main(String[] args) {
		A a[] = new A[3];
		for(int i=0;i<3;i++){
			a[i] = new A();
			a[i].start();
		}
	}
}
