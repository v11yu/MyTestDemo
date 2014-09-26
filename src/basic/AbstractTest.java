package basic;
abstract class my {
    public void mymethod() {
        System.out.print("Abstract");
    }
}
/**
 * 
 *No, you are not creating the instance of your abstract class here. Rather you are creating an instance of an anonymous subclass of your abstract class. And then you are invoking the method on your abstract class reference pointing to subclass object
 *http://stackoverflow.com/questions/13670991/interview-can-we-instantiate-abstract-class/13671003#13671003
 */
public class AbstractTest {
	public static void main(String a[]) {
        my m = new my() {};
        m.mymethod();
    }
}
