package mongo.model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Weibo {
	private String content;
	private String thumbnailPicture;
	private String uid;
	private Integer emotion;
	public Weibo(){}
	public Weibo(DBObject obj){
		this.content = (String) obj.get("text");
		//this.thumbnailPicture = (String) obj.get("tp");
		//this.uid = (String) obj.get("uid");
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getThumbnailPicture() {
		return thumbnailPicture;
	}
	public void setThumbnailPicture(String thumbnailPicture) {
		this.thumbnailPicture = thumbnailPicture;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getEmotion() {
		return emotion;
	}
	public void setEmotion(Integer emotion) {
		this.emotion = emotion;
	}
	public DBObject toDBObject(){
		DBObject obj = new BasicDBObject();
		obj.put("content", content);
		obj.put("emotion", emotion);
		return obj;
	}
	public String toString(){
		return toDBObject().toString();
	}
	
}
