package org.insa.algo.utils;

import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.graph.Node;
import org.insa.graph.Point;
import org.insa.algo.AbstractInputData;;

public class LabelStar extends Label{

	private double coutDestination;
	
	/* n'utilise pas la "methode siouxe" pour différencier deux chemins de cout égaux par le cout vers la destination */
	public LabelStar(Node sommetCourant, ShortestPathData data) {
		super(sommetCourant);
		
		Double distance = Point.distance(getSommetCourant().getPoint(), data.getDestination().getPoint());
		
		if(data.getMode() == AbstractInputData.Mode.TIME) {
			int vitesse = Math.max(data.getGraph().getGraphInformation().getMaximumSpeed(),data.getMaximumSpeed());
			this.coutDestination = distance/((double) vitesse/3.6);
		} else {
			this.coutDestination = distance;
		}
	}
	
	public double getTotalCost() {
		return this.getCost() + this.coutDestination;
	}
	
	@Override
	public int compareTo(Label o) {
		LabelStar other = (LabelStar) o;
		if(this.getTotalCost() < other.getTotalCost())
			return -1;
		else if(this.getTotalCost() > other.getTotalCost())
			return 1;
		
		// en cas d'égalité on regarde le cout vers la destination
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
