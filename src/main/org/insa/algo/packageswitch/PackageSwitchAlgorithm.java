package org.insa.algo.packageswitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.insa.algo.AbstractAlgorithm;
import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathDataRobot;
import org.insa.algo.shortestpath.ShortestPathObserver;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.algo.utils.BinaryHeap;
import org.insa.algo.utils.Label;
import org.insa.graph.Arc;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class PackageSwitchAlgorithm extends AbstractAlgorithm<PackageSwitchObserver> {

	public List<HashMap<Integer, Label>> distances;

	/**
     * Create a new PackageSwitchAlgorithm with the given data.
     * 
     * @param data
     */
    public PackageSwitchAlgorithm(PackageSwitchData data) {
        super(data);
        this.distances = new ArrayList<HashMap<Integer, Label>>();
    }

    @Override
    public PackageSwitchSolution run() {
        return (PackageSwitchSolution) super.run();
    }

    @Override
    public PackageSwitchData getInputData() {
        return (PackageSwitchData) super.getInputData();
    }
    
    
    public Label createLabel(Node current, PackageSwitchData data) {
    	//TODO ameliorer avec un A* prenant le point au milieu du triangle des 3 dest ?
    	return new Label(current);
    }
    
    private Node getMeetingPoint() {
    	PackageSwitchData data = this.getInputData();
    	
    	 // calcule les distances depuis chaque point
    	for(Node n : data.getNodes()) {
    		ArrayList<Node> dest = new ArrayList<Node>(data.getNodes());
    		dest.remove(n);
    		distances.add(getDistanceToNodes(n, dest));
    	}
    	
    	// recupere les points en commun
    	Set<Integer> commonId = distances.get(0).keySet();
    	for(int i = 1; i < data.getNodes().size(); i++)
    		commonId.retainAll(distances.get(i).keySet());
    	
    	// choisit le point a plus bas cout
    	Node meetingPoint = null;
    	Double meetingCost = Double.MAX_VALUE;
    	for(Integer id : commonId) {
    		Double curCost = 0.0;
    		for(HashMap<Integer, Label> distance : distances)
    			curCost += distance.get(id).getCost();
    		
    		if(curCost < meetingCost)
    			meetingPoint = distances.get(0).get(id).getSommetCourant();
    	}
    	
    	return meetingPoint;
    		
    }
    
    private HashMap<Integer, Label> getDistanceToNodes(Node origin, List<Node> destinations) {
    	/* Declaration des variables */
        PackageSwitchData data = this.getInputData();
        
        BinaryHeap<Label> tas = new BinaryHeap<Label>();
    	HashMap<Integer,Label> lablels = new HashMap<Integer, Label>();
        boolean fin = false;
        
        /* On ajoute le sommet de départ dans le tas */
        Label labelOrigin = createLabel(origin, data);
        lablels.put(origin.getId(), labelOrigin);
        labelOrigin.setCost(0.0);
        tas.insert(labelOrigin);

        notifyOriginProcessed(origin); // notify origin processed
        //TODO observer
        
        /* Algorithme de Dijkstra */
        while(!tas.isEmpty() && !fin) {
        	Label x = tas.deleteMin();
        	x.setMarque(true);
        	
        	notifyNodeMarked(x.getSommetCourant()); // notify node marked
        	//TODO observer
        	
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
        					//TODO observer
        				}

        				tas.remove(y);
        				
        				y.setCost(newCost);
        				y.setPere(x.getSommetCourant());
        				
        				//on supprime et rajoute le label pour mettre à jour sa position
        				tas.insert(y);
        			}        			
        		}
        	}
        	
        	boolean allDestinationReached = true;
        	for(Node destination : destinations) {
        		if(lablels.get(destination.getId()) != null) {
        			allDestinationReached &= lablels.get(destination.getId()).isMarque();        			
        		} else {
        			allDestinationReached = false;
        			break;
        		}
        	}
        	fin = allDestinationReached;
        }
        
		return lablels;
    }
    
    private Path createPath(Node origin , Node destination, HashMap<Integer, Label> labels) {    	
        /* Crée le path*/
    	List<Node> pathNodes = new ArrayList<Node>();
    	
        Label curLabel = labels.get(destination.getId());        	
    	do {
    		pathNodes.add(curLabel.getSommetCourant());
    		curLabel = labels.get(curLabel.getPere().getId());
    	}while(!curLabel.getSommetCourant().equals(origin));
    	pathNodes.add(origin);
    	
    	Collections.reverse(pathNodes);
    	
    	Path path = Path.createShortestPathFromNodes(data.getGraph(), pathNodes);

        return path;
    }

    @Override
    protected PackageSwitchSolution doRun() {
    	Node meetingPoint = getMeetingPoint();
    	List<Path> paths = new ArrayList<Path>();
    	
    	paths.add(this.createPath(this.getInputData().getOriginA(), meetingPoint, this.distances.get(0)));
    	paths.add(this.createPath(this.getInputData().getDestinationA(), meetingPoint, this.distances.get(2)));
    	
    	paths.add(this.createPath(this.getInputData().getOriginB(), meetingPoint, this.distances.get(1)));
    	paths.add(this.createPath(this.getInputData().getDestinationB(), meetingPoint, this.distances.get(3)));
    	
		PackageSwitchSolution sol = new PackageSwitchSolution(this.getInputData(), Status.FEASIBLE, paths);
		return sol;
    }
    
    /**
     * Notify all observers that the origin has been processed.
     * 
     * @param node Origin.
     */
    public void notifyOriginProcessed(Node node) {
        for (PackageSwitchObserver obs: getObservers()) {
            obs.notifyOriginProcessed(node);
        }
    }

    /**
     * Notify all observers that a node has been reached for the first time.
     * 
     * @param node Node that has been reached.
     */
    public void notifyNodeReached(Node node) {
        for (PackageSwitchObserver obs: getObservers()) {
            obs.notifyNodeReached(node);
        }
    }

    /**
     * Notify all observers that a node has been marked, i.e. its final value has
     * been set.
     * 
     * @param node Node that has been marked.
     */
    public void notifyNodeMarked(Node node) {
        for (PackageSwitchObserver obs: getObservers()) {
            obs.notifyNodeMarked(node);
        }
    }

    /**
     * Notify all observers that the destination has been reached.
     * 
     * @param node Destination.
     */
    public void notifyDestinationReached(Node node) {
        for (PackageSwitchObserver obs: getObservers()) {
            obs.notifyDestinationReached(node);
        }
    }

}
