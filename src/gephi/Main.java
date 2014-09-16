package gephi;

public class Main {
	static final int M = 10000;
	public static void testP() throws NumberFormatException, Exception{
		int count = 0; 
		while(count < M){
			PropagationGraphCreator1.test(count+"");
			//GephiCleaner.cleanUpLookup();
			System.out.println(count++);
			GephiCleaner.cleanUpThread();
		}
	}
	public static void main(String[] args) throws NumberFormatException, Exception {
		testP();
	}
}
