package org.insa.algo.utils;

import java.util.HashMap;

import org.insa.graph.Node;

public class Label implements Comparable<Label>{

	private Node sommetCourant;
	private boolean marque;
	private double cout;
	private Node pere;
	
	private static HashMap<Integer,Label> hashmap = new HashMap<Integer, Label>();
	
	private Label(Node sommetCourant) {
		this.sommetCourant = sommetCourant;
		this.marque = false;
		this.cout = Double.MAX_VALUE;
		this.pere = null;
	}
	
	public static Label addLabel(Node sommetCourant) {
		Label label = new Label(sommetCourant);
		Label.hashmap.put(sommetCourant.getId(), label);
		return label;
	}
	
	public static Label getLabel(int id) {
		return Label.hashmap.get(id);
	}
	

    public static boolean unmarkedNodeExist() {
    	for(int key : hashmap.keySet()) {
    		if(!hashmap.get(key).marque)
    			return true;
    	}
    	return false;
    }

	public double getCost() {
		return this.cout;
	}
	
	public boolean isMarque() {
		return this.marque;
	}

	public void setMarque(boolean marque) {
		this.marque = marque;
	}
	
	public Node getSommetCourant() {
		return this.sommetCourant;
	}

	@Override
	public int compareTo(Label o) {
		return (int) (this.getCost() - o.getCost());
	}

	public void setCost(Double cost) {
		this.cout = cost;		
	}

	public void setPere(Node pere) {
		this.pere = pere;
	}
	
	public Node getPere() {
		return this.pere;
	}

	@Override
	public String toString() {
		if(pere != null)
			return sommetCourant.getId() + " : " + cout + " [" + pere.getId() + ", " + marque + "]";
		else
			return sommetCourant.getId() + " : " + cout + " [ null, " + marque + "]";
	}
}
