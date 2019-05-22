package org.insa.algo.packageswitch;

import java.util.List;

import org.insa.algo.AbstractSolution;
import org.insa.algo.AbstractSolution.Status;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.graph.Path;

public class PackageSwitchSolution extends AbstractSolution {

    private List<Path> paths;

	protected PackageSwitchSolution(PackageSwitchData data, Status status) {
        super(data, status);
    }
    
	protected PackageSwitchSolution(PackageSwitchData data, Status status, List<Path> paths) {
        super(data, status);
        this.paths = paths;
    }

	public List<Path> getPaths() {
		return this.paths;
	}

}
