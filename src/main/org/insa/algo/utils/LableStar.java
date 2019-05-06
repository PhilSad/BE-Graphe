package org.insa.algo.utils;

import org.insa.graph.Node;

public class LableStar extends Label {

	private double coutDestination;
	
	public LableStar(Node sommetCourant) {
		super(sommetCourant);
		this.coutDestination = Double.MAX_VALUE;
	}
	
	@Override
	public double getCost() {
		return super.getCost() + this.coutDestination;
	}
		
	public void setCostDestination(Double costDestination) {
		this.coutDestination = costDestination;
	}
	
	@Override
	public int compareTo(Label o) {
	}
}
