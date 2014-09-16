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
import mongo.*;


public class PropagationGraphCreator {
	private ProjectController pc = null;
	private Workspace workspace = null;
	private Container container = null;
	private ImportController importController = null;
	private GraphModel graphModel = null;
	private Graph directedGraph = null;
	private int prevNodeCount = 0;

	private long weiboId;


	public PropagationGraphCreator(long weiboId) throws Exception {
		// TODO Auto-generated constructor stub
		// 初始化一些微博的信息
		this.weiboId = weiboId;

		// 初始化
		init();
	}
	

	private void init() {

		pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		workspace = pc.getCurrentWorkspace();
		// append the exist graph file
		// Append container to graph structure

		File file = new File("gexf/3642139672327163.gexf");
		importController = Lookup.getDefault().lookup(ImportController.class);
		try {
			container = importController.importFile(file);
			container.setAllowAutoNode(false);
			synchronized (pc) { // 此处需要将workspace设置为Project的当前workspace，然后再将当前的现有的gexf文件导入至workspace中方能不出错，因此需要同步！
				pc.openWorkspace(workspace);
				importController.process(container, new DefaultProcessor(),
						workspace);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		graphModel = Lookup.getDefault().lookup(GraphController.class)
				.getModel(workspace);
		directedGraph = graphModel.getGraph();
		prevNodeCount = directedGraph.getNodeCount();
		System.out.println("Nodes: " + directedGraph.getNodeCount());
		System.out.println("Edges: " + directedGraph.getEdgeCount());

	}


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

	public void export(String filePath){
		ExportController ec = Lookup.getDefault().lookup(ExportController.class);
		try {
			ec.exportFile(new File(filePath), workspace);	//导出当前的workspace的文本
		} catch (IOException ex) {
			ex.printStackTrace();

			return;
		}

	}
	

	
	/**
	 *	单元测试
	 * @param args
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	public static void main(String[] args) throws NumberFormatException,Exception {

		long id = Long.valueOf("3642139672327163");
		PropagationGraphCreator creator = null;
		try {
			creator = new PropagationGraphCreator(id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		creator.adjust();
		creator.export("gexf" + File.separator + id + ".gexf");
	}
}
