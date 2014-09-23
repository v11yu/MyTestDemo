package basic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import weibo4j.Friendships;
import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;
import cn.ac.ict.algorithm.nlp.EmotionDetection;
import cn.ac.ict.algorithm.socialnetwork.SimpleUserQualityMagicMirror;
import cn.ac.ict.persistense.MongoDBPersistence;
import cn.ac.ict.util.AccessTokenAllocation;
import cn.ac.ict.util.MongoDBManager;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 新浪博主关注的处理线程
 * 每个小时需要获取博主的粉丝情况，将其粉丝质量的统计数据插入至blogger_basic表中，将具体的粉丝情况插入至blogger_fans表中
 * 每5分钟遍历获取一下博主的微博情况，并将统计信息放入至blogger_statistic表中
 * @author Kontrol
 *
 */
public class SinaBloggerMonitorThread extends Thread{
	
	private static int FANSIDMAXCOUNT = 1500000;
	private static int oneThreadGetterSize = 200;	//每一个线程爬取监测的用户的数量
	private static int idsCount = 20;	//每次请求多少个监测的用户的微博，接口限定最大为20
	private static int pageSize = 200;	//每次请求多少条微博，接口限定最大为200
	private static int usersCountUidsSize = 100;	//评论获取用户粉丝数、关注数、微博数的批量大小
	private ArrayList<Long> monitoredBloggerIds = null;
	
	/**
	 * 从数据库中获取uids?
	 * @return
	 */
	private ArrayList<Long> getMonitoredBloggers(){
		ArrayList<Long> bloggerIds = new ArrayList<Long>();
		DBCollection bloggerBasicCollection = MongoDBManager.getDbCollection("blogger_basic");
		DBCursor cursor = bloggerBasicCollection.find();
		while(cursor.hasNext()){
			DBObject blogger = cursor.next();
			Long id = (Long) blogger.get("_id");
			bloggerIds.add(id);
		}
		cursor.close();
		return bloggerIds;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		monitoredBloggerIds = getMonitoredBloggers();
		//更新博主基本信息
		System.out.println("更新博主基本信息");
		updateBolggerBasicInfo();
		//获取微博信息
		System.out.println("获取博主微博数据");
		WeiboGetterThread weiboGetterThread = new WeiboGetterThread();
		weiboGetterThread.run();
		//获取粉丝信息
		System.out.println("— — — — — — — — — —  获取博主粉丝数据 — — — — — — — — — ");
		FansGetterThread fansGetterThread = new FansGetterThread();
		fansGetterThread.run();
	}
	
	/**
	 * 更新博主基本信息，主要是更新微博数、关注数、粉丝数
	 */
	private void updateBolggerBasicInfo() {
		// TODO Auto-generated method stub
		String accessToken = AccessTokenAllocation.getAccessToken();
		Users usersGetter = new Users();
		usersGetter.setToken(accessToken);
		for (int i = 0; i < monitoredBloggerIds.size() / usersCountUidsSize + 1; i++) {
			String uids = "";
			for (int j = 0; j < usersCountUidsSize && (i*usersCountUidsSize + j < monitoredBloggerIds.size()); j++) {
				uids = uids + monitoredBloggerIds.get(i*usersCountUidsSize + j)+",";
			}
			if(uids.length() > 0){
				uids = uids.substring(0, uids.length()-1);
			}
			JSONArray countArray = null;
			try {
				countArray = usersGetter.getUserCount(uids);
				if (countArray != null) {
					for (int j = 0; j < countArray.length(); j++) {
						JSONObject oneCountJsonObject = countArray.getJSONObject(j);
						long userId = oneCountJsonObject.getLong("id");
						int weiboCount = oneCountJsonObject.getInt("statuses_count");
						int friendsCount = oneCountJsonObject.getInt("friends_count");
						int followersCount = oneCountJsonObject.getInt("followers_count");
						Map<String, Object> features = new HashMap<String, Object>();
						features.put("folc", followersCount);
						features.put("fric", friendsCount);
						features.put("sc", weiboCount);
						MongoDBPersistence.updateBloggerBasicInfo(userId, features);
					}
				}
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				countArray = null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * 批量获取指定新浪微博用户的微博
	 * @author Kontrol
	 * @since 2013.10.9
	 */
	public class WeiboGetterThread extends Thread{
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (monitoredBloggerIds == null || monitoredBloggerIds.size() == 0) {
				return ;
			}
			String accessToken =AccessTokenAllocation.getAccessToken();
			Timeline tm = new Timeline();
			tm.setToken(accessToken);
			for (int i = 0; i < monitoredBloggerIds.size(); i = i+oneThreadGetterSize) {
				CrawlThread thread = new CrawlThread(tm, i);
				thread.start();
			}
		}
		
		/**
		 * 爬取的线程，调用的是那个批量爬取的接口，每次爬取最多20个用户，然后爬取200条微博；
		 * @author Kontrol
		 */
		public class CrawlThread extends Thread {
			private Timeline tm;	//时间线
			private int index;	//是第几个线程，用于决定此线程需要负责获哪些人的微博
			
			/**
			 * 构造函数
			 * @param tm
			 * @param index
			 */
			public CrawlThread(Timeline tm, int index) {
				// TODO Auto-generated constructor stub
				this.tm = tm;
				this.index = index;
			}
			
			/**
			 * 调用接口批量获取发布的微博
			 */
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					StatusWapper status = null;
					int i = index;
					Map<User, JSONObject> retweetBloggers = new HashMap<User, JSONObject>();	//转发了哪些人的微博，为{"李开复":1,"潘石屹":2}的格式
					Map<User, JSONObject> atBloggers = new HashMap<User, JSONObject>();	//@了哪些人，为{"李开复":1,"潘石屹":2}的格式
					String reg = "(?<!//)@([-\\w\\u4e00-\\u9fa5]){2,30}";
					Pattern pattern = Pattern.compile(reg);
					while(i<index+oneThreadGetterSize){
						String uids = "";
						int j = 0;
						for (; (j < idsCount) && (i+j)<monitoredBloggerIds.size(); j++) {
							String oneId = monitoredBloggerIds.get(i+j)+"";
							uids = uids + oneId + ",";
						}
						if (uids.length()==0) {	//可以结束了
							break;
						}
						uids = uids.substring(0, uids.lastIndexOf(","));
						List<Status> statuses = new ArrayList<Status>();
						try{
							status = tm.getUserTimelineBatchByUids(uids, pageSize, 1);
						}catch (WeiboException e) {
							 System.err.println("SinaBloggerMonitorThread  批量获取微博时出错，重新再获取一遍！");
							try{
								status = tm.getUserTimelineBatchByUids(uids, pageSize, 1);
							}catch (WeiboException e1) {
								System.err.println("SinaBloggerMonitorThread  批量获取微博时再次出错，不再获取了！");
								status = null;
							}
						}
						if (status!= null) {
							statuses.addAll(status.getStatuses());
						}
						
						for (int k = 0; k < statuses.size(); k++) {
							//将新增的微博存储起来，并且记得更新blogger_statistic表
							if (MongoDBManager.getDbCollection("weibo").findOne(new BasicDBObject("_id", Long.valueOf(statuses.get(k).getId())))==null) {
								//将新增的微博存储起来
								Map<String, Object> extraInfo = new HashMap<String, Object>();
								int emotionStatus = EmotionDetection.getEmotionRank(statuses.get(k).getText());
								extraInfo.put("es", emotionStatus);
								double userQuality = SimpleUserQualityMagicMirror.getCostSum(statuses.get(k).getUser());
								extraInfo.put("uq", userQuality);
								int userRank = SimpleUserQualityMagicMirror.getUserQuality(userQuality);
								extraInfo.put("uqr", userRank);
								MongoDBPersistence.saveWeibo(statuses.get(k), extraInfo, "weibo");
								MongoDBPersistence.saveUser(statuses.get(k).getUser(), "user", null);
								if ((statuses.get(k).getRetweetedStatus() != null) && (statuses.get(k).getRetweetedStatus().getUser() != null)) {	//如果是转发的，保存原始的微博
									extraInfo.clear();	//清除
									emotionStatus = EmotionDetection.getEmotionRank(statuses.get(k).getRetweetedStatus().getText());
									extraInfo.put("es", emotionStatus);
									userQuality = SimpleUserQualityMagicMirror.getCostSum(statuses.get(k).getRetweetedStatus().getUser());
									extraInfo.put("uq", userQuality);
									userRank = SimpleUserQualityMagicMirror.getUserQuality(userQuality);
									extraInfo.put("uqr", userRank);
									MongoDBPersistence.saveWeibo(statuses.get(k).getRetweetedStatus(), extraInfo, "weibo");
									MongoDBPersistence.saveUser(statuses.get(k).getRetweetedStatus().getUser(), "user", null);
								} else {
									System.out.println("微博"+statuses.get(k).getId()+"的源微博已经被删除了！");
								}
								
								//先统计这个微博@了哪些人
								Matcher matcher = pattern.matcher(statuses.get(k).getText());
								while (matcher.find()) {
									String atUserName = matcher.group();
									atUserName = atUserName.substring(1);	//去除@符号
									JSONObject jsonObject = atBloggers.get(statuses.get(k).getUser());
									if (jsonObject == null) {
										jsonObject = new JSONObject();
									}
									jsonObject.put(atUserName, jsonObject.getInt(atUserName)+1);
									atBloggers.put(statuses.get(k).getUser(), jsonObject);
								}
								//如果是转发的，将转发的也存储起来，并且统计这个人转发了哪些人的微博 //重复了？
								if (statuses.get(k).getRetweetedStatus() != null && statuses.get(k).getRetweetedStatus().getUser() != null) {	
									MongoDBPersistence.saveWeibo(statuses.get(k).getRetweetedStatus(), null, "weibo");
									MongoDBPersistence.saveUser(statuses.get(k).getRetweetedStatus().getUser(), "user", null);
									JSONObject jsonObject = retweetBloggers.get(statuses.get(k).getUser());
									if (jsonObject == null) {
										jsonObject = new JSONObject();
									}
									String retweetUserScreenName = statuses.get(k).getRetweetedStatus().getUser().getScreenName();
									jsonObject.put(retweetUserScreenName, jsonObject.getInt(retweetUserScreenName)+1);
									retweetBloggers.put(statuses.get(k).getUser(), jsonObject);
								}
							}
						}
						System.out.println("获取到"+uids+"的微博数为："+statuses.size());
						if(j != idsCount){
							break;
						}
						i = i+idsCount;
					}
					//更新blogger_statistic表
					Date nowDate = new Date();
					Date startDate = new Date(nowDate.getYear(), nowDate.getMonth(), nowDate.getDate(), nowDate.getHours(), 0, 0);
					DBCollection bloggerStatisticCol = MongoDBManager.getDbCollection("blogger_statistic");
					//更新at的博主
					for (User oneUser : atBloggers.keySet()) {
						DBObject existStatisticObject = bloggerStatisticCol.findOne(new BasicDBObject("uid", Long.valueOf(oneUser.getId())).append("ut", new BasicDBObject("$gte", startDate)));
						if (existStatisticObject == null) {
							existStatisticObject = new BasicDBObject();
						}
						existStatisticObject.put("uid", Long.valueOf(oneUser.getId()));
						existStatisticObject.put("fc", oneUser.getFollowersCount());
						existStatisticObject.put("wc", oneUser.getStatusesCount());
						existStatisticObject.put("folc", oneUser.getFriendsCount());
						existStatisticObject.put("ut", startDate);
						String atBloggersStr = (String) existStatisticObject.get("atb");
						if (atBloggersStr == null) {
							atBloggersStr = "{}";
						}
						JSONObject oneUserAtBloggers = new JSONObject(atBloggersStr);
						existStatisticObject.put("atb", bloggerDistributionAdd(atBloggers.get(oneUser), oneUserAtBloggers).toString());
						bloggerStatisticCol.findAndModify(new BasicDBObject("_id", existStatisticObject.get("_id")), null, null, false, existStatisticObject, false, true);
					}
					//更新retweet的博主
					for (User oneUser : retweetBloggers.keySet()) {
						DBObject existStatisticObject = bloggerStatisticCol.findOne(new BasicDBObject("uid", Long.valueOf(oneUser.getId())).append("ut", new BasicDBObject("$gte", startDate)));
						if (existStatisticObject == null) {
							existStatisticObject = new BasicDBObject();
						}
						existStatisticObject.put("uid", Long.valueOf(oneUser.getId()));
						existStatisticObject.put("fc", oneUser.getFollowersCount());
						existStatisticObject.put("wc", oneUser.getStatusesCount());
						existStatisticObject.put("folc", oneUser.getFriendsCount());
						existStatisticObject.put("ut", startDate);
						String retweetBloggersStr = (String) existStatisticObject.get("rb");
						if (retweetBloggersStr == null) {
							retweetBloggersStr = "{}";
						}
						JSONObject oneUserRetweetBloggers = new JSONObject(retweetBloggersStr);
						existStatisticObject.put("rb", bloggerDistributionAdd(retweetBloggers.get(oneUser), oneUserRetweetBloggers).toString());
						bloggerStatisticCol.findAndModify(new BasicDBObject("_id", existStatisticObject.get("_id")), null, null, false, existStatisticObject, false, true);
					}
					TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));//定义时区，可以避免虚拟机时间与系统时间不一致的问题
					Date nowTime = new Date();
					SimpleDateFormat matter = new SimpleDateFormat("yyyy年MM月dd日E HH时mm分ss秒");
					System.out.println(matter.format(nowTime)+"线程"+Thread.currentThread().getName()+"执行完成！");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
			/**
			 * 转发了哪些博主的微博和@了哪些博主相加
			 * @param addObject
			 * @param oldObject
			 * @return
			 */
			private JSONObject bloggerDistributionAdd(JSONObject addObject,
					JSONObject oldObject) {
				// TODO Auto-generated method stub
				if (oldObject == null) {
					return addObject;
				}
				JSONObject newJsonObject = null;
				try {
					newJsonObject = new JSONObject(addObject.toString());
					@SuppressWarnings("unchecked")
					Iterator<String> iterator = oldObject.keys();
					while(iterator.hasNext()){
						String keyStr = iterator.next();
						newJsonObject.put(keyStr, addObject.getInt(keyStr)+oldObject.getInt(keyStr));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					System.out.println("在将两个json字符串相加时出现错误！");
					e.printStackTrace();
				}
				return newJsonObject;
			}
		}
	}
	
	
	/**
	 * 粉丝获取的线程
	 * @author Kontrol
	 *
	 */
	private class FansGetterThread extends Thread {
		private String getFaStr(List<Long> list) {
			if(list == null || list.size() == 0)
				return null;
			
			String str = "";
			for(long id : list)
				str += String.valueOf(id) + ",";
			str = str.substring(0, str.length() - 1);
			return str;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (monitoredBloggerIds == null || monitoredBloggerIds.size() == 0) {
				return ;
			}
			DBCollection bloggerFansCol = MongoDBManager.getDbCollection("blogger_fans");
			DBCollection fansListCol = MongoDBManager.getDbCollection("blogger_fansList");			
			DBCollection bloggerBasicCol = MongoDBManager.getDbCollection("blogger_basic");
			Friendships friendships = new Friendships();
			friendships.setToken(AccessTokenAllocation.getAccessToken());
			for (int i = 0; i < monitoredBloggerIds.size(); i++) {
				JSONObject userQualityDitribution = new JSONObject();	//用户粉丝质量的
				long id = monitoredBloggerIds.get(i);
				HashMap<Integer, ArrayList<Long>> bloggerFansLists = new HashMap<Integer, ArrayList<Long>>();
				try {
					boolean flag = true;
					long nextCursor = 0;
					int sameCount = 0;	//相同的次数
					while(flag){
						UserWapper userWapper = friendships.getFollowersById(id+"", 200, (int)nextCursor);
						List<User> users = userWapper.getUsers();
						for (int j = 0; j < users.size(); j++) {
							Map<String, Object> extraFeatures = new HashMap<String, Object>();
							User oneFans = users.get(j);
							double userCost = SimpleUserQualityMagicMirror.getCostSum(oneFans);
							int userQuality =  SimpleUserQualityMagicMirror.getUserQuality(userCost);
							extraFeatures.put("uq", userCost);
							DBCursor cursor = bloggerFansCol.find(new BasicDBObject("_id", Long.valueOf(oneFans.getId())));
							
							ArrayList<Long> fans = null;
							if(bloggerFansLists.containsKey(userQuality))
								fans = bloggerFansLists.get(userQuality);
							else 
								fans = new ArrayList<Long>();
							
							if (cursor.hasNext()) {
								DBObject fansObject = cursor.next();
								String followUserId = (String) fansObject.get("fuid");
								if (followUserId.indexOf(id+"") != -1) {	//如果已经记录下该粉丝了
									sameCount++;
									if (sameCount >=25) {	//如果获取到超过25个重复的粉丝数据了，就是增量获取到尽头了，不用再获取粉丝了
										cursor.close();
										flag = false;
										break;
									} else {
										continue;
									}
								} else {
									fans.add(Long.valueOf(oneFans.getId()));
									bloggerFansLists.put(userQuality, fans);
									extraFeatures.put("fuid", followUserId + ","+id);	//标示为用户的粉丝
									userQualityDitribution.put(userQuality+"", userQualityDitribution.getInt(userQuality+"")+1);
								}
							} else {
								fans.add(Long.valueOf(oneFans.getId()));
								bloggerFansLists.put(userQuality, fans);
								
								extraFeatures.put("fuid", id+"");	//标示为用户的粉丝
								userQualityDitribution.put(userQuality+"", userQualityDitribution.getInt(userQuality+"")+1);
							}
							cursor.close();
							MongoDBPersistence.saveUser(oneFans, "blogger_fans", extraFeatures);
						}
						nextCursor = userWapper.getNextCursor();
						
						//在blogger_fansList中更新 <博主 -粉丝>  的数据
						if(bloggerFansLists.size() != 0) {
							DBObject query = new BasicDBObject();
							query.put("uid", id);
							query.put("count", new BasicDBObject("$lt", FANSIDMAXCOUNT));
							for(Integer level : bloggerFansLists.keySet()) {
								query.put("level", level);
								ArrayList<Long> fansList = bloggerFansLists.get(level);
								DBObject ans = bloggerFansCol.findOne(query);
								if(ans != null) {
									int count = Integer.valueOf(ans.get("count").toString());
									if(count + fansList.size() >= FANSIDMAXCOUNT) {   //当前记录超过粉丝id列表的最大容量
										ans.put("count", FANSIDMAXCOUNT);
										ans.put("fansid", ans.get("count").toString() + "," + getFaStr( 
										               fansList.subList(0,FANSIDMAXCOUNT - count - 1)));
										fansListCol.update(query, ans);
										
										DBObject newRcd = new BasicDBObject();
										newRcd.put("uid", id);
										newRcd.put("level", level);
										newRcd.put("count", count + fansList.size() - FANSIDMAXCOUNT);
										newRcd.put("fansid", getFaStr(fansList.subList(FANSIDMAXCOUNT - count, fansList.size())));
										fansListCol.insert(newRcd);
									}
									else {
										ans.put("count", count + fansList.size());
										ans.put("fansid", ans.get("fansid").toString() + "," + getFaStr(fansList));
										fansListCol.update(query, ans);
									}
								}else {
									DBObject record = new BasicDBObject();
									record.put("uid", id);
									record.put("level", level);
									record.put("count", fansList.size());
									record.put("fansid", getFaStr(fansList));
									fansListCol.insert(record);
								}
							}
							System.out.println("\n\n*************" + new Date(System.currentTimeMillis()) + id + " 更新blogger_fansList数据表: " + bloggerFansLists.size() + "项");
						}
					}
					
					System.out.println("获取完成用户"+id+"的粉丝！");
					DBCursor blogBasicCursor = bloggerBasicCol.find(new BasicDBObject("_id",id));
					if (blogBasicCursor.hasNext()) {
						DBObject bloggerBasic = blogBasicCursor.next();
						String fansQualityDistributionStr  = (String)bloggerBasic.get("fqd");
						if (fansQualityDistributionStr == null) {
							fansQualityDistributionStr = "{}";
						}
						JSONObject fansQualityDistribution = new JSONObject(fansQualityDistributionStr);
						fansQualityDistribution = fansQualityDistributionAdd(userQualityDitribution, fansQualityDistribution);
						bloggerBasic.put("fqd", fansQualityDistribution.toString());
						bloggerBasicCol.findAndModify(new BasicDBObject("_id", id), bloggerBasic);
					}
					blogBasicCursor.close();
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		
		/**
		 * 两个粉丝质量分布的jsonObejct相加
		 * @param fansQualityDistributionAdd
		 * @param jsonObject
		 * @return
		 */
		private JSONObject fansQualityDistributionAdd(JSONObject addObject,
				JSONObject oldObject) {
			// TODO Auto-generated method stub
			if (oldObject == null) {
				return addObject;
			}
			JSONObject newJsonObject = null;
			try {
				newJsonObject = new JSONObject(addObject.toString());
				@SuppressWarnings("unchecked")
				Iterator<String> iterator = oldObject.keys();
				while(iterator.hasNext()){
					String keyStr = iterator.next();
					newJsonObject.put(keyStr, addObject.getInt(keyStr)+oldObject.getInt(keyStr));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("在将两个json字符串相加时出现错误！");
				e.printStackTrace();
			}
			return newJsonObject;
		}
	}
	
	public static void main(String[] args) {
//		String text = "【第二季】台湾TMT创业项目专场投融资对接会马上就要开始了！想近距离了解台湾同胞的TMT创业项目吗？想面对面了解台湾同胞如何创业吗？想为台湾同胞来内地创业提供创业资源吗？11月22日@创业影院 联合@活动行 搭建海峡两岸创业、投资对接桥梁！报名点击http://t.cn/zRJeOyy";
//		String reg = "@\\S+?\\b";
//		Pattern pattern = Pattern.compile(reg);
//		Matcher matcher = pattern.matcher(text);
//        while (matcher.find()) {
//        	String name = matcher.group();
//        	System.out.println(name.substring(1));
//        }
//		String[] slices = text.split("@");
//		Pattern pattern = Pattern.compile("\\b");
//		for (int i = 1; i < slices.length; i++) {
//			String[] names = pattern.split(slices[i]);
//			System.out.println(names[1]);
//		}
		
		SinaBloggerMonitorThread test = new SinaBloggerMonitorThread();
		test.run();
	}
}
