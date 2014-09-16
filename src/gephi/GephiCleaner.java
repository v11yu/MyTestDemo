package gephi;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.openide.util.Lookup;

/**
 * Gephi内存泄露清除工具类
 * @author v11
 * @date 2014年9月15日
 * @version 1.0
 */
public class GephiCleaner {
	private static final Logger log = Logger.getLogger(GephiCleaner.class);
	/**
	 * 清除LookUp容器，gephi中projectControllerImpl调用了netbeans中LookUp的Api
	 * 设计为singleton模式，但是projectController中存在List成员，导致内存泄露。
	 * 需要手动删除LookUp容器。
	 * @date 2014年9月15日
	 */
	public static synchronized void cleanUpLookup(){
		Lookup obj = Lookup.getDefault();
		Field personNameField;
		try {
			personNameField = Lookup.class.getDeclaredField("defaultLookup");
			personNameField.setAccessible(true);
			personNameField.set(obj, null);
		} catch (Exception e){
			log.info("清除LookUp容器出错！！！");
		}
		
	}
	/**
	 * gephi画图中造成大量的守护进程，没办法杀死
	 * 用Thread.stop强制kill
	 * @date 2014年9月15日
	 */
	public static synchronized void cleanUpThread(){
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		System.out.println("线程数：" + tg.activeCount());
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		for(Thread t:thrds){
			if(t == null) continue;
			if(t.getName().equals("graph-event-bus") || t.getName().equals("DHNS View Destructor") || t.getName().equals("attribute-event-bus"))
				t.stop();
		}
	}
	
}
