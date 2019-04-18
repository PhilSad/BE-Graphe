package org.insa.algo.utils;

import org.insa.graph.Node;

public class Label implements Comparable<Label>{

	private Node sommetCourant;
	private boolean marque;
	private double cout;
	private Node pere;
	
	public Label(Node sommetCourant) {
		this.sommetCourant = sommetCourant;
		this.marque = false;
		this.cout = Double.MAX_VALUE;
		this.pere = null;
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
