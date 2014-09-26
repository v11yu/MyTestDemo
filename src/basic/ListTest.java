package basic;
class ListNode{
	int v;
	ListNode next;
	public ListNode(int v,ListNode next){
		this.v = v;
		this.next = next;
		
	}
}
class Some{
	Integer t;
}
public class ListTest {
	public void work1(){
		ListNode c = new ListNode(3,null);
		ListNode b = new ListNode(2,c);
		ListNode a = new ListNode(1,b);
		ListNode iter = a;
		while(iter != null){
			System.out.println(iter.v);
			iter = iter.next;
		}
		iter = a;
		while(iter != null){
			System.out.println(iter.v);
			iter = iter.next;
		}
	}
	public void work2(){
		Some a = new Some();
		a.t = 5;
		Some b = a;
		System.out.println(a.t);//5
		b.t = 10;
		System.out.println(a.t);//10 changed!
	}
	public static void main(String[] args) {
		
		
	}
}
