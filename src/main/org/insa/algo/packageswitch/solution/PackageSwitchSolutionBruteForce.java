package org.insa.algo.packageswitch.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.insa.algo.packageswitch.PackageSwitchData;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.utils.Label;
import org.insa.graph.Node;
import org.insa.graph.Path;

public class PackageSwitchSolutionBruteForce extends PackageSwitchSolutionDijkstra {

	public PackageSwitchSolutionBruteForce(PackageSwitchData data) {
		super(data);
	}
	
	private Double sumCost(List<HashMap<Integer, Label>> distances, Integer nodeId) throws Exception {
		Double nodeCost = 0.0;
		for(HashMap<Integer, Label> map : distances) {
			Label l = map.get(nodeId);
			if(l == null)
				throw new Exception("Node" + nodeId + " not found");
			else
				nodeCost += l.getCost();
		}
		return nodeCost;
	}
	
	@Override
	protected PackageSwitchSolution doRun() {
		List<DijkstraAlgorithm> algos = this.getAlgorithms();
		
		/* calcule toutes les distances */
		List<HashMap<Integer, Label>> distances = new ArrayList<HashMap<Integer, Label>>();
		for(DijkstraAlgorithm algo : algos) {
			algo.run();
			distances.add(algo.getLabels());
		}
		
		/* Choisit le noeud avec la somme des cout la plus faible */
		Node meetingPoint = null;
		Double minCost = Double.MAX_VALUE;
		Set<Integer> keySet = distances.get(0).keySet();
		
		for(Integer nodeId : keySet) {
			try {
				Double nodeCost = sumCost(distances, nodeId);
				if(nodeCost < minCost) {
					meetingPoint = distances.get(0).get(nodeId).getSommetCourant();
					minCost = nodeCost;
				}
			} catch(Exception e) {
				continue;
			}
		}
		
		if(meetingPoint == null) {
			return new PackageSwitchSolution(getInputData(), PackageSwitchSolution.Status.INFEASIBLE);			
		} else {
			/* crée les deux chemins */
			List<Path> paths = new ArrayList<Path>();
			paths.add(Path.concatenate(algos.get(0).buildPath(meetingPoint), transposePath(algos.get(2).buildPath(meetingPoint))));
			paths.add(Path.concatenate(algos.get(1).buildPath(meetingPoint), transposePath(algos.get(3).buildPath(meetingPoint))));
			
			return new PackageSwitchSolution(getInputData(), PackageSwitchSolution.Status.OPTIMAL, paths);
		}
		
		
	}
}