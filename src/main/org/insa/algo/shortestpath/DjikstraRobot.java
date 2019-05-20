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

public class DjikstraRobot extends ShortestPathAlgorithm {

	/* cout du chemin le plus court */
	public Double cost = null;
	
	public List<HashMap<Integer, Node>> distances;
	
    public DjikstraRobot(ShortestPathData data) {
        super(data);
    }
    
    public Label createLabel(Node current, ShortestPathData data) {
    	return new Label(current);
    }
    
    private Node getMeetingPoint() {
    	
    }
    
    private List<Path> getPaths(){
    	
    }
    
    private HashMap<Integer, Label> getDistance(Node origin, List<Node> destinations) {
    	/* Declaration des variables */
        ShortestPathDataRobot data = (ShortestPathDataRobot) getInputData();
        
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
    	HashMap<Integer,Label> lablels = new HashMap<Integer, Label>();
        boolean fin = false;
        
        /* On ajoute le sommet de départ dans le tas */
        Label labelOrigin = createLabel(origin, data);
        lablels.put(origin.getId(), labelOrigin);
        labelOrigin.setCost(0.0);
        tas.insert(labelOrigin);

        notifyOriginProcessed(origin); // notify origin processed
        
        /* Algorithme de Dijkstra */
        while(!tas.isEmpty() && !fin) {
        	Label x = tas.deleteMin();
        	x.setMarque(true);
        	
        	notifyNodeMarked(x.getSommetCourant()); // notify node marked
        	
        	for(Arc a : x.getSommetCourant().getSuccessors()) {
        		
        		// on verifie que la route est empruntable
                if (!data.isAllowed(a))
                	continue;
        		
                // on recupere le label si il existe, sinon on le crée
                Label y = lablels.get(a.getDestination().getId());
                if(y == null) {
                	y = createLabel(a.getDestination(), data);
                	lablels.put(a.getDestination().getId(), y);
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
        	
        	for(Node destination : destinations)
        		if(lablels.get(destination.getId()) != null)
        			fin &= lablels.get(destination.getId()).isMarque();
        }
        
		return lablels;
    }

    @Override
    protected ShortestPathSolution doRun() {        
        /* Crée le path*/
        Label curLabel = lablels.get(destination.getId());
        if(curLabel == null) {
        	curLabel = createLabel(destination, data);
        }
        
        if(curLabel.getPere() == null) {
        	solution = new ShortestPathSolution(data, Status.INFEASIBLE);
        } else {
            /* cout vers la destination */
            this.cost = lablels.get(destination.getId()).getCost();
            
        	notifyDestinationReached(data.getDestination()); // notify destination reached
        	
        	List<Node> pathNodes = new ArrayList<Node>();        
        	do {
        		pathNodes.add(curLabel.getSommetCourant());
        		curLabel = lablels.get(curLabel.getPere().getId());
        	}while(!curLabel.getSommetCourant().equals(origin));
        	
        	pathNodes.add(origin);
        	Collections.reverse(pathNodes);
        	
        	Path path = Path.createShortestPathFromNodes(data.getGraph(), pathNodes);
        	
        	solution = new ShortestPathSolution(data, Status.FEASIBLE, path);
       }
        
        
        return null;
    }
    
    public double getCost() {
    	return cost;
    }

}
