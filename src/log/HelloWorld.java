package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorld {
  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(HelloWorld.class);
    logger.debug("Hello World--debug");
    logger.info("Hello World---info");
    logger.warn("Hello World --WARN");
    logger.error("Hello World --ERROR");
  }
}