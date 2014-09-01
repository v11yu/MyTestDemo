package util;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MapTest {
	HashMap<String, Integer> map = new HashMap<String, Integer>();
	public MapTest(){
		map.put("a", 2);
		map.put("a1", 4);
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
	public void testSort(){
		ValueComparator bvp = new ValueComparator(map);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvp);
		sortedMap.putAll(map);
		System.out.println(sortedMap.toString());
	}
	public static void main(String[] args) {
		new MapTest().testSort();
	}
}
class ValueComparator implements Comparator<String>{
	Map<String, Integer> base;
	public ValueComparator(Map<String,Integer> base){
		this.base = base;
		
	}
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		if(base.get(o1) > base.get(o2)){
			return 1;
		}
		else return -1;
		//return base.get(o1) - base.get(o2);
	}
	
}
