package gephi;

import org.gephi.graph.api.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;


/**
 * 用于gephi memory leak测试
 * @author v11
 * @date 2014年9月11日
 * @version 1.0
 */
public class Demo {

	private ProjectController pc  ;
	private GraphModel graphModel ;
	private Workspace workspace;
	private DirectedGraph directedGraph;
	private UndirectedGraph undirectedGraph;
	/**
	 * Init value 
	 * LookUp to find singleton
	 */
	private void initProject(){
		//Init a project - and therefore a workspace
		pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        workspace = pc.getCurrentWorkspace();
        //Get a graph model - it exists because we have a workspace
        graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
	}
	private void addEdge(){
		
	}
	private void addNode(String ids,String label){
		Node n0 = graphModel.factory().newNode("n0");
		n0.getNodeData().setLabel(label);
		
	}
	private void showGraph(){
		
	}
	public void work(){
		initProject();
	}
	
}
