package org.insa.algo.packageswitch.solution;

import org.insa.algo.packageswitch.PackageSwitchAStar;
import org.insa.algo.packageswitch.PackageSwitchData;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;

public class PackageSwitchSolutionAStar extends PackageSwitchSolutionDijkstra{

	public PackageSwitchSolutionAStar(PackageSwitchData data) {
		super(data);
	}
	
	@Override
	protected DijkstraAlgorithm SPAlgorithm(ShortestPathData dataSP, PackageSwitchData dataPS) {
		return new PackageSwitchAStar(dataSP, dataPS);
	}

}
