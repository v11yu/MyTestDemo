package mongo;
import java.net.UnknownHostException;  
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;  
import java.util.Date;
import java.util.List;  
import java.util.Set;  
import java.util.regex.Pattern;  

import com.mongodb.AggregationOutput;  
import com.mongodb.BasicDBList;  
import com.mongodb.BasicDBObject;  
import com.mongodb.BasicDBObjectBuilder;  
import com.mongodb.DB;  
import com.mongodb.DBCollection;  
import com.mongodb.DBCursor;  
import com.mongodb.DBObject;  
import com.mongodb.MapReduceCommand;  
import com.mongodb.MapReduceOutput;  
import com.mongodb.Mongo;  
import com.mongodb.QueryBuilder;  
import com.mongodb.WriteConcern; 
public class Test {
	public void testGetDBs(){
		List<String> dbnames = MongoUtil.getMong().getDatabaseNames();  
        for (String dbname : dbnames) {  
            System.out.println("dbname:" + dbname);  
        }  
	}
	public void count(){
		System.out.println(MongoUtil.getColl("weibo").getCount());  
	}
	public DBCursor queryMulti2() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		Date d = null;
		try {
			//d = df.parse("2014-03-01 00-00-00");
			d = df.parse("2013-12-01 00-00-00");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // or make a date out of a string...
		DBObject query = QueryBuilder.start().put("ca").lessThanEquals(d).get();
        //query.put("ca", new BasicDBObject("$gt", 5)); // e.g. find all where i >  
        DBCursor cursor = MongoUtil.getColl("weibo").find(query);  
        System.out.println("cursor:"+cursor.count());
		return cursor;
    } 
	public void copyData(){
		DBCursor cursor = queryMulti2();
		int i = 0,j = 0;
		try {
			while (cursor.hasNext()) {
				i++;
				if(i%100==0) System.out.println(i);
				DBObject obj = cursor.next();
				if(MongoTo.getColl("weibo").findOne(obj) == null){
					MongoTo.getColl("weibo").insert(obj);
					//MongoUtil.getColl("weibo").setWriteConcern(WriteConcern.SAFE);
				}
				else {
					//System.out.println("");
					j++;
				}
				System.out.println("exit:"+j);
			}
		} finally {
			cursor.close();
		}
		
		
	}

	public static void main(String[] args) {
		Test t = new Test();
//		t.testGetDBs();
//		t.count();
		t.queryMulti2();
//		t.copyData();
	}
}
