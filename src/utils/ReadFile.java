package utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 文件读写类
 * @author v11
 *
 */
public class ReadFile {
	private static final String utf_8 = "utf-8";
	/**
	 * 读取文件转化为hashMap
	 * @param fileName
	 * @return
	 */
	public static HashMap<String, Integer> readFileToMap(String fileName){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

		try {
			InputStreamReader isr = new InputStreamReader(is, utf_8);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while ((line = reader.readLine()) != null) {
				map.put(line.split(" ")[0],
						Integer.parseInt(line.split(" ")[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 读取文件转化为hashMap
	 * @param fileName
	 * @param weight 正负权重 1,or -1
	 * @return
	 */
	public static HashMap<String, Integer> readFileToMap(String fileName,Integer weight){
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

		try {
			InputStreamReader isr = new InputStreamReader(is, utf_8);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while ((line = reader.readLine()) != null) {
				map.put(line.split(" ")[0],
						weight * Integer.parseInt(line.split(" ")[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 读取文件，转化为List类型
	 * @param fileName
	 * @return
	 */
	public static List<String> readFileToList(String fileName){
		List<String> ls = new ArrayList<String>();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

		try {
			InputStreamReader isr = new InputStreamReader(is, utf_8);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while ((line = reader.readLine()) != null) {
				ls.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls;
	}
	/**
	 * 读取文件，转化为Set类型
	 * @param fileName
	 * @return
	 */
	public static Set<String> readFileToSet(String fileName){
		Set<String> ls = new HashSet<String>();
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);

		try {
			InputStreamReader isr = new InputStreamReader(is, utf_8);
			BufferedReader reader = new BufferedReader(isr);
			String line = null;
			while ((line = reader.readLine()) != null) {
				ls.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ls;
	}
}
