package mongo;

import java.util.ArrayList;
import java.util.List;

import mongo.model.Weibo;
import mongo.util.MongoConfig;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;


public class WeiboDao extends BasicDao{
	public WeiboDao(){
		this.Collection = MongoDBManager.getUniqueMongoDBManager().getColl(MongoConfig.getValue("weibo"));
	}
	public WeiboDao(String dbname){
		this.Collection = MongoDBManager.getUniqueMongoDBManager().getColl(dbname);
	}
	public static List<Weibo> toList(DBCursor weiboCursor){
		List<Weibo> weibos = new ArrayList<Weibo>();
		while(weiboCursor.hasNext()){
			DBObject object = weiboCursor.next();
			Weibo weibo = new Weibo(object);
			weibos.add(weibo);
		}
		return weibos;
	}
	public static void main(String[] args) {
		WeiboDao wdao = new WeiboDao();
		DBCursor cursor = wdao.findByAll();
		System.out.println(cursor.count());
		DBObject obj = cursor.next();
		obj.put("type", "w");
		System.out.println(obj.get("content"));
		cursor.close();
		wdao.update(obj, "type");
	}
	
}
