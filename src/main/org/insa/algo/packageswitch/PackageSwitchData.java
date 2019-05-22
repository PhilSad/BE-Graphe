package org.insa.algo.packageswitch;

import java.util.Arrays;
import java.util.List;

import org.insa.algo.AbstractInputData;
import org.insa.algo.ArcInspector;
import org.insa.graph.Graph;
import org.insa.graph.Node;

public class PackageSwitchData extends AbstractInputData {

	private List<Node> nodes;
	
    public PackageSwitchData(Graph graph, Node originA, Node originB, Node destinationA, Node destinationB, ArcInspector arcFilter) {
        super(graph, arcFilter);
        this.nodes = Arrays.asList(new Node[] {originA, originB, destinationA, destinationB});
    }

    public Node getOriginA() {
    	return this.nodes.get(0);
    }
    
    public Node getOriginB() {
    	return this.nodes.get(1);
    }
    
    public Node getDestinationA() {
    	return this.nodes.get(2);
    }
    
    public Node getDestinationB() {
    	return this.nodes.get(3);
    }

	public List<Node> getNodes() {
		return this.nodes;
	}

}
