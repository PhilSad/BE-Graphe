package org.insa.algo.packageswitch;

import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.utils.Label;
import org.insa.algo.utils.LabelStar;
import org.insa.graph.Node;

public class PackageSwitchAStar extends DijkstraAlgorithm {

	private PackageSwitchData dataPS;
	
	public PackageSwitchAStar(ShortestPathData dataSP, PackageSwitchData dataPS) {
		super(dataSP);
		this.dataPS = dataPS;
	}
	
	@Override
	public Label createLabel(Node current, ShortestPathData data) {
		return new PackageSwitchLabel(current, dataPS, data);
	}


}
