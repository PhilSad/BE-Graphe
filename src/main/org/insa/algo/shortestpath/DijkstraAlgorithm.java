package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.Label;
import org.insa.graph.Arc;
import org.insa.graph.Node;
import org.insa.graph.Path;

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
        
        for(Node node : nodes) { // on crée des labels pour chaque noeud
        	Label.addLabel(node);
        }
        
        Label originLabel = Label.getLabel(origin.getId());
        originLabel.setCost(0.0);
        tas.insert(originLabel);
        
        /* Algorithme de Dijkstra */
        while(!tas.isEmpty()) {
        	Label x = tas.deleteMin();
        	x.setMarque(true);
        	for(Arc a : x.getSommetCourant().getSuccessors()) {
        		
        		// Small test to check allowed roads...
                if (!data.isAllowed(a)) {
                	
                	continue;
                }
        		
        		Label y = Label.getLabel(a.getDestination().getId());
        		if(!y.isMarque()) {
        			Double newCost = Math.min(y.getCost(), x.getCost() + a.getLength());
        			if(newCost != y.getCost()) {
        				if(y.getCost() == Double.MAX_VALUE) //rajoute dans le tas si on n'a pas touché sa valeur
        					tas.insert(y);        					
        				
        				y.setCost(newCost);
        				y.setPere(x.getSommetCourant());
        			}        			
        		}
        	}
        }
        
        /* Crée le path*/
        
        Node destination = data.getDestination();     
        
        List<Node> pathNodes = new ArrayList<Node>();
        
        Label curNode = Label.getLabel(destination.getId());
        do {
        	pathNodes.add(curNode.getSommetCourant());
        	curNode = Label.getLabel(curNode.getPere().getId());
        }while(!curNode.getSommetCourant().equals(origin));
        
        pathNodes.add(origin);
        Collections.reverse(pathNodes);
        
        Path path = Path.createShortestPathFromNodes(data.getGraph(), pathNodes);
        
        solution = new ShortestPathSolution(data, Status.FEASIBLE, path);
        
        return solution;
    }

}
