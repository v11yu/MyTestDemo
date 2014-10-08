package mongo;



import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import mongo.util.*;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;
/**
 * MongoDB管理类
 * 单例模式
 * @author v11
 *
 */
public class MongoDBManager {
	private static MongoDBManager uniqueMongoDBManager;
	private Mongo mongo;
	private Log log = LogFactory.getLog(MongoDBManager.class.getName());
	private DB db;

	/**
	 * 获取mongoDB新建连接时的配置参数
	 * @param autoConnectRetry 系统发生错误时，是否重试
	 * @param connectionsPerHost 单个主机连接到mongo实例允许的最大连接数
	 * @param maxWaitTime 线程等待链接可用的最长时间
	 * @param socketTimeout 执行io操作的超时时间
	 * @param connecTimeout 建立链接的超时时间
	 * @param threadsAllowedToBlockForConnectionMultiplier 允许阻塞请求的最大值
	 * @return 配置参数Options
	 */
	private MongoOptions getOptions(boolean autoConnectRetry,
			int connectionsPerHost,int maxWaitTime,
			int socketTimeout,int connectTimeout,
			int threadsAllowedToBlockForConnectionMultiplier){
		MongoOptions options = new MongoOptions();
		/*
		 * 系统发生错误时，是否重试
		 * 默认是false
		 */
        options.autoConnectRetry = autoConnectRetry;
        /*
         * 单个主机连接到mongo实例允许的最大连接数
         * 超过了就会新建链接，默认值是10，大并发的话较小
         */
        options.connectionsPerHost = connectionsPerHost;
        /*
         * 线程等待链接可用的最长时间，ms单位，默认120,000，两分钟。
         */
        options.maxWaitTime = maxWaitTime;
        /*
         * 执行io操作的超时时间，默认为0，代表不超时
         */
        options.socketTimeout = socketTimeout;
        /*
         * 建立链接的超时时间。默认为10,000，10s
         */
        options.connectTimeout = connectTimeout;
        /*
         * 这个参数是跟connectionsPerHost配套的
         * 当连接数超过connectionsPerHost的时候
         * 需要建立新的连接，连接请求会被阻塞，这个参数就代表允许阻塞请求的最大值
         * 超过这个值之后的请求都会报错。默认值5
         * Example:connectionsPerHost is 10,threadsAllowedToBlockForConnectionMultiplier is 5
         * then 50 threads can block more than that and an exception will be throw
         * 意思是c = 10, t = 5,50个threads可以阻塞
         */
        options.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
        return options;
	}
	private MongoDBManager(){
		MongoOptions options = getOptions(false, 100, 5000, 0, 15000, 50);
		try {
			mongo = new Mongo(new ServerAddress(MongoConfig.getValue("hostIp"), MongoConfig.getNum("port")),options);
		} catch (UnknownHostException e) {
			log.error("MongoDB connect failed.");
		}
		
	}
	/**
	 * 单例模式，获取唯一的MongoDBManager
	 * @return Mongo数据库管理类
	 */
	public static MongoDBManager getUniqueMongoDBManager(){
		if(uniqueMongoDBManager == null){
			uniqueMongoDBManager = new MongoDBManager();
		}
		return uniqueMongoDBManager;
	}
	/**
	 * 获取数据库DB,利用mongodb.properties中默认的数据库名
	 * @return  a logical database on a server
	 */
	public DB getDB(){
		if(db==null){
			db = mongo.getDB(MongoConfig.getValue("DB_NAME"));
		}
		return db;
	}
	/**
	 * 获取数据库DB
	 * @param DBName 数据库名
	 * @return a logical database on a server
	 */
	public DB getDB(String DBName){
		if(db==null){
			db = mongo.getDB(DBName);
		}
		return db;
	}
	/**
	 * 获取数据库表操作的类
	 * @param collname 表名
	 * @return
	 */
	public DBCollection getColl(String collname){

		if (getDB().collectionExists(collname)) {
	        return getDB().getCollection(collname);
	    } else {
	        DBObject options = BasicDBObjectBuilder.start().add("capped", false).get();
	        return getDB().createCollection(collname, options);
	    }

	}
	
}
