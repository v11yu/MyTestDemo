package gephi;

import org.gephi.project.api.Project;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class GephiManager {
	
	private static Project project = null;
	private static ProjectController pc = null;
	
	private GephiManager(){
		
	}
	
	/**
	 * 通过本函数获取一个新的workspace
	 * @return
	 */
	public static synchronized Workspace newWorkspace() {
		if (project == null || pc == null) {
			pc = Lookup.getDefault().lookup(ProjectController.class);
			pc.newProject();
			project = pc.getCurrentProject();
		}
		return pc.newWorkspace(project);
	}
	
	public static synchronized ProjectController getProjectController(){
		if (pc==null) {
			pc = Lookup.getDefault().lookup(ProjectController.class);
		}
		return pc;
	}
	
	/**
	 * 删除一个workspace释放空间
	 * @param workspace
	 */
	public static synchronized void deleteWorkspace(Workspace workspace){
		if (project != null && pc != null) {
			pc.cleanWorkspace(workspace);
			pc.deleteWorkspace(workspace);
			workspace = null;	//将workspace设置为null
		}
	}
	
	/**
	 * 关闭整个project
	 */
	public static synchronized void closeProject(){
		if (project != null && pc != null) {
			pc.closeCurrentProject();
			project = null;
			pc = null;
		}
	}
}

