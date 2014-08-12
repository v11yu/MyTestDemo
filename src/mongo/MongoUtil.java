package mongo;


import java.net.UnknownHostException;







import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;
import com.mongodb.ServerAddress;


/**
 * to see:http://www.mongodb.org/display/DOCS/Java+Driver+Concurrency
 * Mongo������:���Ϊ����ģʽ��ÿ���·ݷ����仯�����ݿ��������ƾͻᷢ���仯������ҵ�����
 * �� MongoDB��Java�������̰߳�ȫ�ģ�����һ���Ӧ�ã�ֻҪһ��Mongoʵ�����ɣ�Mongo�и����õ����ӳأ��ش�СĬ��Ϊ10������
 * �����д���д�Ͷ��Ļ����У�Ϊ��ȷ����һ��Session��ʹ��ͬһ��DBʱ�����ǿ��������·�ʽ��֤һ���ԣ�
 *	 DB mdb = mongo.getDB('dbname');
 *	 mdb.requestStart();
 *	 // ҵ�����
 *	 mdb.requestDone();
 * DB��DBCollection�Ǿ����̰߳�ȫ��
 * @author wujintao
 */
public class MongoUtil{
	
	private static Mongo mongo;
	private static DBCollection coll;
	private static Log log = LogFactory.getLog(MongoUtil.class);
	private static DB db;
	private static String FROM_HOST = "10.23.0.195";
	private static String TO_HOST = "10.23.0.194";
	private static int PORT = 27017;
	private static String DB_NAME = "vmojing_enterprise";
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
			mongo = new Mongo(new ServerAddress(FROM_HOST, PORT),options);
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
		return getDB().getCollection(collname);
	}
	
}