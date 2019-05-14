package org.insa.algo.utils;

import org.insa.graph.Node;
import org.insa.graph.Point;

public class LabelStar extends Label{

	private double coutDestination;
	
	/* n'utilise pas la "methode siouxe" pour différencier deux chemins de cout égaux par le cout vers la destination */
	public LabelStar(Node sommetCourant, Node destination) {
		super(sommetCourant);
		this.coutDestination = Point.distance(getSommetCourant().getPoint(), destination.getPoint());
	}
	
	public double getTotalCost() {
		return this.cout + this.coutDestination;
	}
	
	
	@Override
	public int compareTo(Label o) {
		LabelStar other = (LabelStar) o;
		if(this.getTotalCost() < other.getTotalCost())
			return -1;
		else if(this.getTotalCost() > other.getTotalCost())
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
