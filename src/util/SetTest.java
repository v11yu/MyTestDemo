package util;

import java.util.HashSet;
import java.util.Set;

public class SetTest {
	public void work(){
		Set<String> stops = new HashSet<String>();
		String s1 = "haha";
		String s2 = "gege";
		stops.add(s1);
		stops.add(s2);
		for(String str:stops){
			System.out.println(str);
		}
		System.out.println(stops.add(s1));
	}
	public static void main(String[] args) {
		SetTest t = new SetTest();
		t.work();
		
	}
}
