package basic;

public class ExceptionTest {
	public static void getError() throws Exception{
		throw new Exception();
	}
	public static void main(String[] args) {
		try{
			getError();
			System.out.println("run");
		}
		catch(Exception e){
			System.out.println("error");
		}finally{
			System.out.println("finally");
		}
	}
}
