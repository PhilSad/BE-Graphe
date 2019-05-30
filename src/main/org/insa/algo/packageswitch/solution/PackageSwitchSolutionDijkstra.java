package org.insa.algo.packageswitch.solution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.insa.algo.AbstractInputData;
import org.insa.algo.packageswitch.PackageSwitchAlgorithm;
import org.insa.algo.packageswitch.PackageSwitchData;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.utils.Label;
import org.insa.graph.Arc;
import org.insa.graph.Graph;
import org.insa.graph.Node;
import org.insa.graph.Path;
import org.insa.graph.Point;

public class PackageSwitchSolutionDijkstra extends PackageSwitchAlgorithm {
	public PackageSwitchSolutionDijkstra(PackageSwitchData data) {
		super(data);
	}

	private DijkstraAlgorithm createSPAlgorithm(Node origin, List<Node> destinations, Graph graph) {
		// choisit le point le plus loin
		Node destination = null;
		Double maxDist = 0.0;
		for (Node n : destinations) {
			Double dist = Point.distance(origin.getPoint(), n.getPoint());
			if (dist > maxDist) {
				maxDist = dist;
				destination = n;
			}
		}

		ShortestPathData spData = new ShortestPathData(graph, origin, destination, this.getInputData().getArcInspector());
		return SPAlgorithm(spData, this.getInputData());
	}
	
	protected DijkstraAlgorithm SPAlgorithm(ShortestPathData dataSP, PackageSwitchData dataPS) {
		return new DijkstraAlgorithm(dataSP);		
	}
	
	protected List<DijkstraAlgorithm> getAlgorithms(){
		Graph graph = this.getInputData().getGraph();
		Graph transposedGraph = graph.transpose();
		List<Node> dests = this.getInputData().getNodes();
		Node oA = this.getInputData().getOriginA();
		Node oB = this.getInputData().getOriginB();
		Node dA = this.getInputData().getDestinationA();
		Node dB = this.getInputData().getDestinationB();
		DijkstraAlgorithm algoOA = createSPAlgorithm(oA, dests, graph);
		DijkstraAlgorithm algoOB = createSPAlgorithm(oB, dests, graph);
		DijkstraAlgorithm algoDA = createSPAlgorithm(dA, dests, transposedGraph);
		DijkstraAlgorithm algoDB = createSPAlgorithm(dB, dests, transposedGraph);
		List<DijkstraAlgorithm> algos = Arrays.asList(algoOA, algoOB, algoDA, algoDB);
		
		for(DijkstraAlgorithm algo : algos)
			algo.init();
		
		return algos;
	}
	
	//nécessaire car on ne peut pas concatener un chemin d'un graphe avec un chemin du même graphe transposé
	public Path transposePath(Path path) {
		List<Node> nodes = new ArrayList<Node>();
		nodes.add(path.getArcs().get(0).getOrigin());
		
		for(Arc a : path.getArcs())
			nodes.add(a.getDestination());
		
		Collections.reverse(nodes);
		Path transposedPath;
		
		if(this.getInputData().getMode() == AbstractInputData.Mode.TIME)
			transposedPath = Path.createFastestPathFromNodes(this.getInputData().getGraph(), nodes);
		else
			transposedPath = Path.createShortestPathFromNodes(this.getInputData().getGraph(), nodes);
		
		return transposedPath;
	}

	@Override
	protected PackageSwitchSolution doRun() {
		List<DijkstraAlgorithm> algos = this.getAlgorithms();
		HashMap<Integer, Integer> markedCounter = new HashMap<Integer, Integer>();
		
		DijkstraAlgorithm nextAlgo = null;
		
		Node meetingPoint = null;
		Integer nbMarked = 0;
		while(nbMarked != 4) {
			Label nextLabel = new Label(null);
			nextLabel.setCost(Double.MAX_VALUE);
			
			/* choisit l'algorithme qui a le noeud de plus bas cout */
			for(DijkstraAlgorithm algo : algos) {
				if(algo.getTas().isEmpty())
					continue;
				
				Label minLabel = algo.getTas().findMin();
				if (!algo.isOver() && minLabel.getCost() < nextLabel.getCost()) {
					nextLabel = minLabel;
					nextAlgo = algo;
				}
			}
			
			if(nextLabel.getSommetCourant() == null) { /* Il n'existe plus de sommets à parcourir */
				return new PackageSwitchSolution(getInputData(), PackageSwitchSolution.Status.INFEASIBLE);
			} else {				
				/* compte le nombre de fois ou le noeud est marque */
				Node n = nextAlgo.step();
				nbMarked = markedCounter.getOrDefault(n.getId(), 0) + 1;
				markedCounter.put(n.getId(), nbMarked);
				
				if(nbMarked == 4)
					meetingPoint = n;
				
				notifyNodeReached(n, nbMarked-1);
			}
		}
		
		/* crée les deux chemins */
		List<Path> paths = new ArrayList<Path>();
		paths.add(Path.concatenate(algos.get(0).buildPath(meetingPoint), transposePath(algos.get(2).buildPath(meetingPoint))));
		paths.add(Path.concatenate(algos.get(1).buildPath(meetingPoint), transposePath(algos.get(3).buildPath(meetingPoint))));
		
		return new PackageSwitchSolution(getInputData(), PackageSwitchSolution.Status.OPTIMAL, paths);
	}
}
