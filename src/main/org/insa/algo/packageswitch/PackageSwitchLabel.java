package org.insa.algo.packageswitch;

import java.util.ArrayList;
import java.util.List;

import org.insa.algo.AbstractInputData;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.utils.Label;
import org.insa.graph.Node;
import org.insa.graph.Point;

public class PackageSwitchLabel extends Label{

	private Double coutDestination;
	
	public PackageSwitchLabel(Node sommetCourant, PackageSwitchData dataPS, ShortestPathData dataSP) {
		super(sommetCourant);
		
		List<Node> dests = new ArrayList<Node>();
		for(Node node :  dataPS.getNodes())
			if(node.getId() != dataSP.getOrigin().getId())
				dests.add(node);
		
		float x = (dests.get(0).getPoint().getLongitude() + dests.get(1).getPoint().getLongitude() + dests.get(2).getPoint().getLongitude())/3;
		float y = (dests.get(0).getPoint().getLatitude() + dests.get(1).getPoint().getLatitude() + dests.get(2).getPoint().getLatitude())/3;
		Point p = new Point(x,y);
		
		Double distance = Point.distance(getSommetCourant().getPoint(), p);
		
		if(dataPS.getMode() == AbstractInputData.Mode.TIME) {
			int vitesse = Math.max(dataPS.getGraph().getGraphInformation().getMaximumSpeed(),dataPS.getMaximumSpeed());
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
		PackageSwitchLabel other = (PackageSwitchLabel) o;
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
