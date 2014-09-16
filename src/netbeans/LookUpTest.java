package netbeans;

public class LookUpTest {
	public static void work1(){
		 String className = System.getProperty("org.openide.util.Lookup"); // NOI18N
		 System.out.println(className);
	}
	public static void main(String[] args) {
		work1();
	}
}
