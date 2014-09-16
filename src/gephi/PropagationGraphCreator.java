package gephi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.datalab.api.AttributeColumnsController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.AutoLayout;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.partition.api.Partition;
import org.gephi.partition.api.PartitionController;
import org.gephi.partition.plugin.NodeColorTransformer;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.Ranking;
import org.gephi.ranking.api.RankingController;
import org.gephi.ranking.api.Transformer;
import org.gephi.ranking.plugin.transformer.AbstractSizeTransformer;
import org.gephi.statistics.plugin.GraphDistance;
import org.gephi.statistics.plugin.Modularity;
import org.openide.util.Lookup;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.ac.ict.util.GridFSManager;
import cn.ac.ict.util.MongoDBManager;

/**
 * 传播图生成器，主要是生成传播图，每条微博的传播图由此生成
 * 采取叠加的方式，而不是从数据库中获取该条微博的所有转发重新构造传播图
 * @author Kontrol
 * @since 2013.8.2
 */
public class PropagationGraphCreator {
	private ProjectController pc = null;
	private Workspace workspace = null;
	private Container container = null;
	private ImportController importController = null;
	private GraphModel graphModel = null;
	private Graph directedGraph = null;
	private int prevNodeCount = 0;
	private DBCollection retweetsCollection = MongoDBManager.getDbCollection("clue_retweet");
	private DBCollection retweetsUserCollection = MongoDBManager.getDbCollection("user");
	private DBCollection weiboCollection = MongoDBManager.getDbCollection("clue_basic");

	private String weiboUserScreenName = null;
	private long weiboId;
	private boolean finished = false;

	/**
	 * 构造函数，需要传进来一条微博的id号，以便生成该条微博的传播图.
	 * @param weiboId
	 * @throws Exception
	 */
	/*
	 * @writer v11
	 * 构造函数赋值
	 * weiboId
	 * weidoUserScreenName
	 */
	public PropagationGraphCreator(long weiboId) throws Exception {
		// TODO Auto-generated constructor stub
		// 初始化一些微博的信息
		this.weiboId = weiboId;
		DBCursor weiboCursor = weiboCollection.find(new BasicDBObject("_id",weiboId));
		if (!weiboCursor.hasNext()) {
			weiboCursor.close();
			throw new Exception("cannot find the weibo " + weiboId + "!");
		}
		DBObject weiboObject = weiboCursor.next();
		long userId = (Long) weiboObject.get("uid");
		weiboCursor.close();
		DBCursor weiboUserCursor = retweetsUserCollection.find(new BasicDBObject("_id", userId)); 
		if (!weiboUserCursor.hasNext()) {
			weiboUserCursor.close();
			throw new Exception("cannot find the weibo " + weiboId + " 's user!");
		}
		DBObject weiboUserObject = weiboUserCursor.next();
		weiboUserScreenName = (String) weiboUserObject.get("sn");
		weiboUserCursor.close();
		// 初始化
		init();
	}
	
	/**
	 * 根据给定的微博id来进行该条微博的worspace的创建，需要查询之前是有已经有该条微博的的传播图，有即可以导入
	 * 直接从mongodb中获取
	 * @since 2013.11.7
	 */
	private void init() {
		/*
		 * @writer v11
		 * workspace 一开始为null
		 * init只会在构造函数内调用，那么workspace肯定是null的
		 * 为什么这里还需要判断？
		 */
		if (workspace == null) {
			workspace = GephiManager.newWorkspace();
			pc = GephiManager.getProjectController();
			// append the exist graph file
			// Append container to graph structure
			if (GridFSManager.exist(weiboId+".gexf")) {
				File file = GridFSManager.getByFileName(weiboId+".gexf", "gexf" + File.separator + weiboId + ".gexf");
				importController = Lookup.getDefault().lookup(ImportController.class);
				try {
					container = importController.importFile(file);
					container.setAllowAutoNode(false);
					synchronized (pc) {	//此处需要将workspace设置为Project的当前workspace，然后再将当前的现有的gexf文件导入至workspace中方能不出错，因此需要同步！
						pc.openWorkspace(workspace);
						importController.process(container, new DefaultProcessor(),workspace);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
			directedGraph = graphModel.getGraph();
			prevNodeCount = directedGraph.getNodeCount();
			/*
			 * @writer v11
			 * 这里init只有从构造函数调用
			 * prevNodeCount初始值就是为0，为什么还需要判断？
			 */
			
			if (prevNodeCount == 0) {
				Node n0 = graphModel.factory().newNode(weiboUserScreenName + "0"); // 0代表是在第0层
				n0.getNodeData().setLabel(weiboUserScreenName);
				directedGraph.addNode(n0);
			}

			System.out.println("Nodes: " + directedGraph.getNodeCount());
			System.out.println("Edges: " + directedGraph.getEdgeCount());
		}
	}
	
	/**
	 * 从MongoDB中获取该条微博的所有转发，然后添加进传播图中
	 * @since 2013.8.2
	 */
	public void addRetweets() {
		DBCursor retweetsCursor = retweetsCollection.find(
				new BasicDBObject("rwid", weiboId)).sort(
				new BasicDBObject("_id", -1));
		if (!retweetsCursor.hasNext()) {
			retweetsCursor.close();
			return;
		}
		int addCount = 0;
		while (retweetsCursor.hasNext()) {
			DBObject retweetObject = retweetsCursor.next();
			System.out.println(addCount++);
			long retweetUserId = (Long) retweetObject.get("uid");
			DBCursor retweetsUserCursor = retweetsUserCollection
					.find(new BasicDBObject("_id", retweetUserId));
			if (!retweetsUserCursor.hasNext()) {
				continue;
			}
			DBObject retweetsUserObject = retweetsUserCursor.next();
			String screenName = (String) retweetsUserObject.get("sn");
			retweetsUserCursor.close();
			String text = (String) retweetObject.get("text");
			addOneRetweet(text, screenName);
		}
		retweetsCursor.close();
	}

	/**
	 * 往图上增加一条转发，根据该条转发的文本来进行处理，每条转发都是更具//@来就行分隔的
	 * @param retweetText
	 * @param retweetUserSN
	 * @since 2013.8.2
	 */
	public void addOneRetweet(String retweetText, String retweetUserSN) {
		String[] names = retweetText.split("//@");
		ArrayList<String> namesArray = new ArrayList<String>();
		namesArray.add(retweetUserSN);
		for (int i = 1; i < names.length; i++) {
			if (names[i].indexOf(":") != -1) {
				namesArray.add(names[i].substring(0, names[i].indexOf(":")));
			}
		}
		namesArray.add(weiboUserScreenName); // 该条微博的人的昵称
		/*
		 * @writer v11
		 * init 里面初始化了directedGraph了。这里不应该==0
		 */
		
		if (directedGraph.getNodeCount() == 0) {
			Node n0 = graphModel.factory().newNode(weiboUserScreenName + "0"); // 0代表是在第0层
			n0.getNodeData().setLabel(weiboUserScreenName);
			directedGraph.addNode(n0);
		}
		for (int i = namesArray.size() - 2; i >= 0; i--) {
			Node node = directedGraph.getNode(namesArray.get(i)
					+ (namesArray.size() - i - 1)); // namesArray.size()-i-1代表所在的层次
			boolean flag = false;
			if (node == null) {
				node = graphModel.factory().newNode(
						namesArray.get(i) + (namesArray.size() - i - 1));
				node.getNodeData().setLabel(namesArray.get(i));
				directedGraph.addNode(node);
				flag = true;
			}
			Node neighbor = directedGraph.getNode(namesArray.get(i + 1)
					+ (namesArray.size() - i - 2));
			String nameTemp = namesArray.get(i) + (namesArray.size() - i - 1)
					+ namesArray.get(i + 1) + (namesArray.size() - i - 2);
			int hashVal = nameTemp.hashCode();
			if ((directedGraph.getEdge(hashVal + "") == null) && flag && (neighbor!=null)) {
				Edge edge = graphModel.factory().newEdge(hashVal + "", node,
						neighbor, 1f, true);
				directedGraph.addEdge(edge);
			}
		}
	}

	/**
	 * 传播图进行调整
	 * 先将传播图划分成不同的Partition，
	 * 然后再将不同的Partition染上不同的颜色，
	 * 然后在调整每个节点的大小，
	 * 然后调用YifanHu算法进行自动布局,
	 * 最后删除多余的属性，以便减小生成的文件的大小.
	 * @since 2013.8.2
	 */
	public void adjust() {
		AttributeModel attributeModel = null;
		synchronized (pc) {
			pc.openWorkspace(workspace);
			//注意是getModel(workspace)，而不是getModel()!浪费掉我2小时！
			attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel(workspace);
			// Run modularity algorithm - community detection
			Modularity modularity = new Modularity();
			modularity.execute(graphModel, attributeModel);
			// Partition with ‘modularity_class’, just created by Modularity
			PartitionController partitionController = Lookup.getDefault().lookup(
					PartitionController.class);
			AttributeColumn modColumn = attributeModel.getNodeTable().getColumn(
					Modularity.MODULARITY_CLASS);
			Partition p = partitionController.buildPartition(modColumn,
					directedGraph);
			System.out.println(p.getPartsCount() + "partitions found");
			NodeColorTransformer nodeColorTransformer = new NodeColorTransformer();
			nodeColorTransformer.randomizeColors(p);
			partitionController.transform(p, nodeColorTransformer);

			RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
			GraphDistance distance = new GraphDistance();
			distance.setDirected(true);
			distance.execute(graphModel, attributeModel);

			AbstractSizeTransformer sizeTransformer = (AbstractSizeTransformer) rankingController.getModel().getTransformer(Ranking.NODE_ELEMENT,Transformer.RENDERABLE_SIZE);
			sizeTransformer.setMinSize(10);
			sizeTransformer.setMaxSize(100);
			Ranking degreeRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, Ranking.DEGREE_RANKING);
			rankingController.transform(degreeRanking, sizeTransformer);
		}
		
		//自动布局，布局的时间是根据新增了多少个节点来进行计算的
		int addCount = directedGraph.getNodeCount() - prevNodeCount;
		int time = addCount / 80 + 10;
		AutoLayout autoLayout = new AutoLayout(time, TimeUnit.SECONDS);
		autoLayout.setGraphModel(graphModel);
		YifanHuLayout firstLayout = new YifanHuLayout(null,new StepDisplacement(1f));
		autoLayout.addLayout(firstLayout, 1f);
		autoLayout.execute();
		
		//将以下属性进行删除，以便减小生成的gexf文件的大小
		AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
		ac.deleteAttributeColumn(attributeModel.getNodeTable(), attributeModel.getNodeTable().getColumn("modularity_class"));
		ac.deleteAttributeColumn(attributeModel.getNodeTable(), attributeModel.getNodeTable().getColumn("eccentricity"));
		ac.deleteAttributeColumn(attributeModel.getNodeTable(), attributeModel.getNodeTable().getColumn("closnesscentrality"));
		ac.deleteAttributeColumn(attributeModel.getNodeTable(), attributeModel.getNodeTable().getColumn("betweenesscentrality"));
	}
	
	/**
	 * 将图导出至指定的filepath下，需指定为gexf文件格式
	 * @param filePath
	 */
	public void export(String filePath){
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			ec.exportFile(new File(filePath), workspace);	//导出当前的workspace的文本
		} catch (IOException ex) {
			ex.printStackTrace();
			finish();
			finished = true;
			return;
		}
		finish();
		finished = true;
	}
	
	/**
	 * 将当前的workspace删除，以便释放内存
	 */
	public void finish(){
		if (workspace != null && !finished) {
			GephiManager.deleteWorkspace(workspace);	//删除当前workspace
			workspace = null;	//释放内存
			finished = true;
		}
	}
	
	/**
	 *	单元测试
	 * @param args
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public static void main(String[] args) throws NumberFormatException,Exception {
//		DBCollection monitoredWeiboStatisticCollection = MongoDBManager.getDbCollection("monitored_weibo_statistic");
//		DBCursor cursor = monitoredWeiboStatisticCollection.find(new BasicDBObject("ti",new BasicDBObject("$ne", -1)));
//		while(cursor.hasNext()){
//			DBObject oneDbObject = cursor.next();
//			final long id = (Long) oneDbObject.get("_id");
//			new Thread(new Runnable() {
//				public void run() {
//					PropagationGraphCreator creator = null;
//					try {
//						creator = new PropagationGraphCreator(id);
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						return;
//					}
//					creator.addRetweets();
//					creator.adjust();
//					creator.export("gexf" + File.separator + id + ".gexf");
//				}
//			}).start();
//			
//		}
//		cursor.close();
		long id = Long.valueOf("3642139672327163");
		PropagationGraphCreator creator = null;
		try {
			creator = new PropagationGraphCreator(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		//creator.addRetweets();
		creator.adjust();
		creator.export("gexf" + File.separator + id + ".gexf");
	}
}
