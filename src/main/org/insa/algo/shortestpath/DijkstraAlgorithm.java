package org.insa.algo.shortestpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.insa.algo.AbstractInputData;
import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.Label;
import org.insa.graph.Arc;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {
	
	private HashMap<Integer,Label> labels;
	private BinaryHeap<Label> tas;
	
    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
        
    	/* Initialisation de l'algorithme*/
    	init();
    }
    
    public Label createLabel(Node current, ShortestPathData data) {
    	return new Label(current);
    }
    
    private void init() {
    	/* Declaration des variables */
        ShortestPathData data = getInputData();
        this.tas = new BinaryHeap<Label>();      
        Node origin = data.getOrigin();
        this.labels = new HashMap<Integer, Label>();
        
        /* On ajoute le sommet de départ dans le tas */
        Label labelOrigin = createLabel(origin, data);
        labels.put(origin.getId(), labelOrigin);
        labelOrigin.setCost(0.0);
        tas.insert(labelOrigin);

        notifyOriginProcessed(origin); // notify origin processed
    }    
    
    public Node step() {
    	Label x = tas.deleteMin();
    	x.setMarque(true);
    	
    	notifyNodeMarked(x.getSommetCourant()); // notify node marked
    	
    	for(Arc a : x.getSommetCourant().getSuccessors()) {
    		
    		// on verifie que la route est empruntable
            if (!data.isAllowed(a))
            	continue;
    		
            // on recupere le label si il existe, sinon on le crée
            Label y = labels.get(a.getDestination().getId());
            if(y == null) {
            	y = createLabel(a.getDestination(), this.getInputData());
            	labels.put(a.getDestination().getId(), y);
            }
            
    		if(!y.isMarque()) {
    			Double newCost = Math.min(y.getCost(), x.getCost() + data.getCost(a));
    			
    			if(newCost < y.getCost()) {           				
					// rajoute dans le tas si on n'a pas encore touché sa valeur
    				if(y.getCost() == Double.MAX_VALUE) {
    					tas.insert(y);
    					notifyNodeReached(y.getSommetCourant()); // notify node reached
    				}

    				tas.remove(y);
    				
    				y.setCost(newCost);
    				y.setPere(x.getSommetCourant());
    				
    				//on supprime et rajoute le label pour mettre à jour sa position
    				tas.insert(y);
    			}        			
    		}
    	}
    	
    	return x.getSommetCourant();
    }
    
    public boolean isOver() {
    	boolean destinationReached = false;
    	Node destination = this.getInputData().getDestination();
    	if(labels.get(destination.getId()) != null)
    		destinationReached = labels.get(destination.getId()).isMarque();
    	
    	return tas.isEmpty() || destinationReached;
    }
    
    public Path buildPath(Node destination) {
        ShortestPathData data = getInputData();
        Node origin = data.getOrigin();
        
        Label curLabel = labels.get(destination.getId());
        if(curLabel == null) {
        	curLabel = this.createLabel(destination, data);
        }
        
        if(curLabel.getPere() == null) {
        	return null;
        } else {            
        	notifyDestinationReached(data.getDestination()); // notify destination reached
        	
        	List<Node> pathNodes = new ArrayList<Node>();        
        	do {
        		pathNodes.add(curLabel.getSommetCourant());
        		curLabel = labels.get(curLabel.getPere().getId());
        	}while(!curLabel.getSommetCourant().equals(origin));
        	
        	pathNodes.add(origin);
        	Collections.reverse(pathNodes);
        	
        	return Path.createShortestPathFromNodes(data.getGraph(), pathNodes);
       }
    }

    @Override
    protected ShortestPathSolution doRun() {    	
        /* Algorithme de Dijkstra */
        do {
        	this.step();
        }while(!isOver());
        
        Path path = buildPath(this.getInputData().getDestination());
        
        if(path != null)
        	return new ShortestPathSolution(this.getInputData(), Status.OPTIMAL, path);
        else
        	return new ShortestPathSolution(this.getInputData(), Status.INFEASIBLE);
    }
    
    public BinaryHeap<Label> getTas() {
    	return this.tas;
    }
    

}
