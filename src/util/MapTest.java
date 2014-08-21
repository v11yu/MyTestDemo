package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class MapTest {
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	public MapTest(){
		map.put("a", 1);
		map.put("a1", 1);
		map.put("a2", 1);
		map.put("a3", 1);
		map.put("a3", 1);
		map.put("a4", 1);
	}
	public void testRemove(){
		Iterator<Entry<String, Integer>> iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Integer> en = iter.next();
			System.out.println(en.getKey());
			if(en.getKey().equals("a") ||en.getKey().equals("a3")){
				iter.remove();
			}
		}
		System.out.println("=============");
		iter = map.entrySet().iterator();
		while(iter.hasNext()){
			Entry<String, Integer> en = iter.next();
			System.out.println(en.getKey());
		}
	}
	public static void main(String[] args) {
		new MapTest().testRemove();
	}
}
