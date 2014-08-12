package mongo;


import java.net.UnknownHostException;








import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;


/**
 * MongoUtil ����
 * copy data to this mongoDB
 */
public class MongoTo{
	
	private static Mongo mongo;
	private static DBCollection coll;
	private static Log log = LogFactory.getLog(MongoUtil.class);
	private static DB db;
	private static String FROM_HOST = "10.23.0.195";
	private static String TO_HOST = "10.23.0.194";
	private static String LocalHost = "127.0.0.1";
	private static int PORT = 27017;
	private static String DB_NAME = "vmojing_enterprise_copy";
	static{
		try {
		      MongoOptions options = new MongoOptions();
                      options.autoConnectRetry = true;
                      options.connectionsPerHost = 1000;
                      options.maxWaitTime = 5000;
                      options.socketTimeout = 0;
                      options.connectTimeout = 15000;
                      options.threadsAllowedToBlockForConnectionMultiplier = 5000;
			//��ʵ�ϣ�Mongoʵ��������һ�����ݿ����ӳأ���ʹ�ڶ��̵߳Ļ����У�һ��Mongoʵ����������˵�Ѿ��㹻��
			mongo = new Mongo(new ServerAddress(LocalHost, PORT),options);
			//mongo = new Mongo(DBMongoConfig.getHost(),DBMongoConfig.getPort());
			// or, to connect to a replica set, supply a seed list of members
			// Mongo m = new Mongo(Arrays.asList(new ServerAddress("localhost",
			// 27017),
			// new ServerAddress("localhost", 27018),
			// new ServerAddress("localhost", 27019)));

			// ע��Mongo�Ѿ�ʵ�������ӳأ��������̰߳�ȫ�ġ�
			// �󲿷��û�ʹ��mongodb���ڰ�ȫ�����£��������mongodb��Ϊ��ȫ��֤ģʽ������Ҫ�ڿͻ����ṩ�û��������룺
			// boolean auth = db.authenticate(myUserName, myPassword);
		} catch (UnknownHostException e) {
			log.info("get mongo instance failed");
		}
	}
	
	public static DB getDB(){
		if(db==null){
			db = mongo.getDB(DB_NAME);
		}
		return db;
	}
	
	
	public static Mongo getMong(){
		return mongo;
	}
	
	public static DBCollection getColl(String collname){

		if (getDB().collectionExists(collname)) {
	        return getDB().getCollection(collname);
	    } else {
	        DBObject options = BasicDBObjectBuilder.start().add("capped", false).get();
	        return getDB().createCollection(collname, options);
	    }

	}
	
}