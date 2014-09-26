package mongo;

import java.util.ArrayList;
import java.util.List;





import org.apache.log4j.Logger;
import org.bson.types.ObjectId;


import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

public class BasicDao {
	private static final Logger log = Logger.getLogger(BasicDao.class);
	protected DBCollection Collection;
	
	/**
	 * 查询全部记录
	 * @return DB iterator
	 */
	public DBCursor findByAll(){
		DBCursor cursor = Collection.find();
		return cursor;
	}
	/**
	 * 返回小于等于value信息
	 * @param valueName 字段名
	 * @param value 值
	 * @return 返回条件查询结果
	 */
	public DBCursor findByValueLessThanEquals(String valueName,Object value){
		DBObject query = QueryBuilder.start().put(valueName).lessThanEquals(value).get();
		DBCursor cursor = Collection.find(query);
		return cursor;
	}
	/**
	 * 返回大于等于value信息
	 * @param valueName 字段名
	 * @param value 值
	 * @return 返回条件查询结果
	 */
	public DBCursor findByValueGreaterThanEquals(String valueName,Object value){
		DBObject query = QueryBuilder.start().put(valueName).greaterThanEquals(value).get();
		DBCursor cursor = Collection.find(query);
		return cursor;
	}
	/**
	 * 获取相等
	 * @param valueName
	 * @param value
	 * @return
	 */
	public DBCursor findByValueEquals(String valueName,Object value){
		DBObject query = QueryBuilder.start().and(valueName).is(value).get();
		log.info(query.toString());
		DBCursor cursor = Collection.find(query);
		return cursor;
	}
	/**
	 * 保存记录
	 * @param obj  
	 */
	public void save(DBObject obj){
		Collection.save(obj);
	}
	/**
	 * 保存所以记录
	 * @param cursor
	 */
	public void saveAll(DBCursor cursor){
		while(cursor.hasNext()){
			DBObject obj = cursor.next();
			save(obj);
		}
	}
	/**
	 * 删除该表所有记录
	 */
	public void dropAll(){
		Collection.drop();
	}
	/**
	 * 更新数据
	 * @param obj完整的变动后的 obj
	 * @param keyName 变动的keyName
	 */
	public void update(DBObject obj,String keyName){
		DBObject query = new BasicDBObject("_id",obj.get("_id"));
        DBObject updateObj = new BasicDBObject(keyName,obj.get(keyName));
        Collection.update(query, new BasicDBObject("$set", updateObj));
		//Collection.update(query, updateObj);
        //会变成下面这样:
//        {
//        	  "_id" : ObjectId("53ec6b92419d5d941c6ba8a2"),
//        	  "type" : "w"
//        }
	}
	/**
	 * 对已有的Cursor进行排序
	 * @param keyName 字段名
	 * @param op 1：asc，-1：desc
	 * @param cursor 待处理的DBCursor
	 * @return
	 * @date 2014年9月22日
	 */
	public DBCursor sort(String keyName,int op,DBCursor cursor){
		return cursor.sort(new BasicDBObject(keyName,op));
	}
	/**
	 * 分页查找
	 * @param itemCount 每页个数
	 * @param pageNum 页数
	 * @param cursor 待处理的DBCursor
	 * @return
	 * @date 2014年9月22日
	 */
	public DBCursor findByPage(Integer itemCount,Integer pageNum,DBCursor cursor){
		return cursor.skip(itemCount * pageNum).limit(itemCount);
	}
	
}
