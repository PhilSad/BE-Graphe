package org.insa.algo.utils;

import org.insa.graph.Node;

public class LabelStar extends Label{

	private double coutDestination;
	
	public LabelStar(Node sommetCourant, Node destination) {
		super(sommetCourant);
		this.coutDestination = Double.MAX_VALUE;
		this.coutDestination = this.getSommetCourant().getPoint().distanceTo(destination.getPoint());
	}
	
	@Override
	public double getCost() {
		return this.cout + this.coutDestination;
	}
	
	@Override
	public int compareTo(Label o) {
		LabelStar other = (LabelStar) o;
		if(this.cout < other.cout)
			return -1;
		else if(this.cout > other.cout)
			return 1;
		
		/* en cas d'égalité on regarde le cout vers la destination */
		else {
			if(this.coutDestination < other.coutDestination)
				return -1;
			else if(this.coutDestination > other.coutDestination)
				return 1;
			else
				return 0;
		}
	}
}
