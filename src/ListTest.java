import java.util.*;
public class ListTest {
	public static void errorMove1(){
		ArrayList<String> list = new ArrayList<String>(Arrays.asList("a","b","c","d","e"));
		for(String s:list){
			if(s.equals("a")){
				list.remove(s);
			}
		}
	}
	public static void acceptMove(){
		ArrayList<String> list = new ArrayList<String>(Arrays.asList("a","b","c","d"));
		Iterator<String> iter = list.iterator();
		while(iter.hasNext()){
		        String s = iter.next();
		        if(s.equals("a")){
		            iter.remove();
		    }
		}
		System.out.println(list);
	}
	public static void main(String[] args) {
		ListTest.acceptMove();
	}
}
