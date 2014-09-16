package gephi;

/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 Gephi is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

 Gephi is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.gephi.graph.api.*;
import org.gephi.graph.*;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.project.impl.ProjectControllerImpl;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.Lookups;

/**
 * This demo shows basic features from GraphAPI, how to create and query a graph
 * programmatically.
 * 
 * @author Mathieu Bastian
 */
public class ManualGraph {
	private final Logger log = Logger.getLogger(this.getClass());
	ProjectController pc;
	private void MyPrintln(String str){
		System.out.println(str);
	}
	private void release() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException{
		Lookup obj = Lookup.getDefault();
		Field personNameField = Lookup.class.getDeclaredField("defaultLookup");
		personNameField.setAccessible(true);
		personNameField.set(obj, null);
	}
	public void script() {
		// Init a project - and therefore a workspace
		//pc = new ProjectControllerImpl();
		pc = Lookup.getDefault().lookup(ProjectController.class);
		pc.newProject();
		Workspace workspace = pc.getCurrentWorkspace();

		// Get a graph model - it exists because we have a workspace

		GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel(workspace);
		//GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
		// Create three nodes
		Node n0 = graphModel.factory().newNode("n0");
		n0.getNodeData().setLabel("Node 0");
		Node n1 = graphModel.factory().newNode("n1");
		n1.getNodeData().setLabel("Node 1");
		Node n2 = graphModel.factory().newNode("n2");
		n2.getNodeData().setLabel("Node 2");

		// Create three edges
		Edge e1 = graphModel.factory().newEdge(n1, n2, 1f, true);
		Edge e2 = graphModel.factory().newEdge(n0, n2, 2f, true);
		Edge e3 = graphModel.factory().newEdge(n2, n0, 2f, true); // This is
																	// e2's
																	// mutual
																	// edge

		// Append as a Directed Graph
		DirectedGraph directedGraph = graphModel.getDirectedGraph();
		directedGraph.addNode(n0);
		directedGraph.addNode(n1);
		directedGraph.addNode(n2);
		directedGraph.addEdge(e1);
		directedGraph.addEdge(e2);
		directedGraph.addEdge(e3);

		// Count nodes and edges
		MyPrintln("directedGraph Nodes: "
				+ directedGraph.getNodeCount() + " Edges: "
				+ directedGraph.getEdgeCount());

		// Get a UndirectedGraph now and count edges
		UndirectedGraph undirectedGraph = graphModel.getUndirectedGraph();
		MyPrintln("UndirectedGraph Nodes: "
				+ undirectedGraph.getNodeCount() + " Edges: "
				+ undirectedGraph.getEdgeCount()); // The mutual edge is
													// automatically merged
		MyPrintln("-------directedGraph-------");
		// Iterate over nodes
		for (Node n : directedGraph.getNodes()) {
			Node[] neighbors = directedGraph.getNeighbors(n).toArray();
			MyPrintln(n.getNodeData().getLabel() + " has "
					+ neighbors.length + " neighbors");
		}

		// Iterate over edges
		for (Edge e : directedGraph.getEdges()) {
			MyPrintln(e.getSource().getNodeData().getId() + " -> "
					+ e.getTarget().getNodeData().getId());
		}
		MyPrintln("-------directedGraph-------");

		MyPrintln("-------undirectedGraph-------");
		// Iterate over nodes
		for (Node n : undirectedGraph.getNodes()) {
			Node[] neighbors = undirectedGraph.getNeighbors(n).toArray();
			MyPrintln(n.getNodeData().getLabel() + " has "
					+ neighbors.length + " neighbors");
		}

		// Iterate over edges
		for (Edge e : undirectedGraph.getEdges()) {
			MyPrintln(e.getSource().getNodeData().getId() + " -> "
					+ e.getTarget().getNodeData().getId());
		}
		MyPrintln("-------undirectedGraph-------");

		// Find node by id
		Node node2 = directedGraph.getNode("n2");

		// Get degree
		MyPrintln("Node2 degree: " + directedGraph.getDegree(node2));

		// Modify the graph while reading
		// Due to locking, you need to use toArray() on Iterable to be able to
		// modify
		// the graph in a read loop
		for (Node n : directedGraph.getNodes().toArray()) {
			directedGraph.removeNode(n);
		}
		try {
			release();
		} catch (Exception e) {
			log.info("释放内存有问题");
		}
		//cleanUp
//		workspace.remove(graphModel);
//		graphModel.clear();
//		pc.getCurrentProject().remove(graphModel);
//		workspace.remove(graphModel);
//		pc.cleanWorkspace(workspace);
//		pc.deleteWorkspace(workspace);
//		pc.closeCurrentProject();
//		pc.closeCurrentWorkspace();
//		graphModel = null;
//		workspace = null;
//		pc = null;
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		int count = 100000;
		
		try {
			while (count > 0) {
				//Thread.sleep(1*1000);
				ManualGraph manualG = new ManualGraph();
				manualG.script();
				//killThread();
				count--;
				System.out.println(count);
				//ThreadTools.killThread();
				ThreadTools.prntThreadNum(" ");
			}
		} catch (OutOfMemoryError e) {
			System.out.println("hehe");
			ThreadTools.killThread();
		}
//		manualG.script();
//		killThread();
	}

}
