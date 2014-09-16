package gephi;

public class ThreadTools {
	@SuppressWarnings("deprecation")
	public static void killThread(){
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		Thread thrds[] = new Thread[tg.activeCount()];
		tg.enumerate(thrds);
		if(thrds == null) System.out.println("null");
		for(Thread t:thrds){
			if(t == null) continue;
			if(t.getName().equals("graph-event-bus") || t.getName().equals("DHNS View Destructor") || t.getName().equals("attribute-event-bus"))
				t.stop();
		}
		
	}
	public static void prntThreadNum(String str){
		ThreadGroup tg = Thread.currentThread().getThreadGroup();
		System.out.println(str + tg.activeCount());
		
	}
}
