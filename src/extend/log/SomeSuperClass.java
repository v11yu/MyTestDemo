package extend.log;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class SomeSuperClass {
	private static Map<Class,Logger> loggers = new HashMap<Class,Logger>();
	public void show(){
		getLogger().info("hello");
	}
	protected Logger getLogger()
	{
	    Logger logger = null;
	    if (SomeSuperClass.loggers.containsKey(this.getClass())) {
	        logger = SomeSuperClass.loggers.get(this.getClass());
	    } else {
	        logger = LoggerFactory.getLogger(this.getClass());
	        SomeSuperClass.loggers.put (this.getClass(), logger);
	    }
	    return logger;
	}
}
