package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.insa.algo.AbstractInputData;
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

        notifyOriginProcessed(origin); // notify origin processed
        
        /* Algorithme de Dijkstra */
        while(!tas.isEmpty()) {
        	Label x = tas.deleteMin();
        	x.setMarque(true);        	
        	
        	notifyNodeMarked(x.getSommetCourant()); // notify node marked
        	
        	for(Arc a : x.getSommetCourant().getSuccessors()) {
        		
        		// Small test to check allowed roads...
                if (!data.isAllowed(a)) {
                	continue;
                }
        		
        		Label y = Label.getLabel(a.getDestination().getId());
        		if(!y.isMarque()) {
        			
        			Double newCost;
        			
        			if(data.getMode() == AbstractInputData.Mode.LENGTH) {
        				newCost = Math.min(y.getCost(), x.getCost() + a.getLength());
        			}
        			else {
        				newCost = Math.min(y.getCost(), x.getCost() + a.getMinimumTravelTime());
        			}
        			
        			if(newCost != y.getCost()) {
        				
    					//rajoute dans le tas si on n'a pas encore touché sa valeur
        				if(y.getCost() == Double.MAX_VALUE) {
        					tas.insert(y);
        					notifyNodeReached(y.getSommetCourant()); // notify node reached
        				}
        				
        				y.setCost(newCost);
        				y.setPere(x.getSommetCourant());
        			}        			
        		}
        	}
        }
        /* Crée le path*/
        
        Node destination = data.getDestination();
        Label curLabel = Label.getLabel(destination.getId());
        
        if(curLabel.getPere() == null) {
        	solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        } else {
        	notifyDestinationReached(data.getDestination()); // notify destination reached
        	
        	List<Node> pathNodes = new ArrayList<Node>();        
        	do {
        		pathNodes.add(curLabel.getSommetCourant());
        		curLabel = Label.getLabel(curLabel.getPere().getId());
        	}while(!curLabel.getSommetCourant().equals(origin));
        	
        	pathNodes.add(origin);
        	Collections.reverse(pathNodes);
        	
        	Path path = Path.createShortestPathFromNodes(data.getGraph(), pathNodes);
        	
        	solution = new ShortestPathSolution(data, Status.FEASIBLE, path);
       }
        
        
        return solution;
    }

}
