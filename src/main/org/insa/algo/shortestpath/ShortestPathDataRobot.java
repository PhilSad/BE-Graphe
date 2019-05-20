package org.insa.algo.shortestpath;

import java.util.List;

import org.insa.algo.ArcInspector;
import org.insa.graph.Graph;
import org.insa.graph.Node;

public class ShortestPathDataRobot extends ShortestPathData{

	private List<Node> destinations;
	
	public ShortestPathDataRobot(Graph graph, Node origin, List<Node> destinations, ArcInspector arcInspector) throws IllegalArgumentException {
		super(graph, null, null, arcInspector);
		
		if(destinations.size() != 4)
			throw new IllegalArgumentException("Uncorrect number of destination");
		
		this.destinations = destinations;
	}
	
	public List<Node> getDestinations(){
		return this.destinations;
	}

}
