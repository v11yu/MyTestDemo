package concurrent;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton
 */
public class Food {
	private Map<String, String> foodMap;
	private static Food unique;
	private Food(){
		foodMap = new HashMap<String, String>();
	}
	public static Food getUnique(){
		if(unique == null) unique = new Food();
		return unique;
	}
	public static void cleanUp(){
		unique = null;
	}
	/**
	 * add food to this cat
	 */
	public void create(Cat cat){
		String catName = cat.getName();
		foodMap.put(catName, catName+" food");
	}
	public String getFood(Cat cat){
		String catName = cat.getName();
		return foodMap.get(catName);
	}
}
