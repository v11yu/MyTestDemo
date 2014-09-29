package extend.classs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Animal<T> {
	private T t;

	public void set(T t) {
		this.t = t;
	}

	public T getT() {
		return t;
	}

}
