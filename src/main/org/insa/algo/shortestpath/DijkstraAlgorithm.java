package org.insa.algo.shortestpath;

import java.util.List;

import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.Label;
import org.insa.graph.Arc;
import org.insa.graph.Node;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }

    @Override
    protected ShortestPathSolution doRun() {
        ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;
        
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
        List<Node> nodes = this.data.getGraph().getNodes();
        
        Node origin = data.getOrigin();
        
        for(Node node : nodes) {
        	Label.addLabel(node); // on crée des labels pour chaque noeud
        }
        
        Label originLabel = Label.getLabel(origin.getId());
        originLabel.setCost(0.0);
        tas.insert(originLabel);
        
        while(Label.unmarkedNodeExist()) {
        	Label x = tas.findMin();
        	x.setMarque(true);
        	
        	for(Arc a : x.getSommetCourant().getSuccessors()) {
        		Label y = Label.getLabel(a.getDestination().getId());
        		if(!y.isMarque()) {
        			Double newCost = Math.min(y.getCost(), x.getCost() + a.getLength());
        			if(newCost != y.getCost()) {
        				y.setCost(newCost);
        				y.setPere(x.getSommetCourant());
        				tas.insert(y);
        			}        			
        		}
        	}
        }
        
        Node destination = data.getDestination();        
        
        return solution;
    }

}
