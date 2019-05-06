package org.insa.graph;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insa.algo.AbstractInputData.Mode;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.shortestpath.BellmanFordAlgorithm;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.graph.RoadInformation.RoadType;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShortestPathTest {
	
    // Small graph use for tests
    private static Graph graph;

    // List of nodes
    private static Node[] nodes;

    // List of arcs in the graph, a2b is the arc from node A (0) to B (1).
    @SuppressWarnings("unused")
    private static Arc a2b, a2c, b2d, b2e, b2f, c2a, c2b, c2f, e2d, e2f, e2c, f2e;
    
    
    private static List<DijkstraAlgorithm> dijkstras;
    
    
    private class Pair <A,B> {
    	A a;
    	B b;
    	public Pair(A a, B b) {
    		this.a = a;
    		this.b = b;
    	}
    }
    
    @BeforeClass
    public static void initAll() throws IOException {

        // 10 and 20 meters per seconds
        RoadInformation roadInfo = new RoadInformation(RoadType.MOTORWAY, null, true, 0, "");

        // Create nodes
        nodes = new Node[6];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = new Node(i, null);
        }

        // Add arcs...
        a2b = Node.linkNodes(nodes[0], nodes[1], 7, roadInfo, null);
        a2c = Node.linkNodes(nodes[0], nodes[2], 8, roadInfo, null);
        b2d = Node.linkNodes(nodes[1], nodes[3], 4, roadInfo, null);
        b2e = Node.linkNodes(nodes[1], nodes[4], 1, roadInfo, null);
        b2f = Node.linkNodes(nodes[1], nodes[5], 5, roadInfo, null);
        c2a = Node.linkNodes(nodes[2], nodes[0], 7, roadInfo, null);
        c2b = Node.linkNodes(nodes[2], nodes[1], 2, roadInfo, null);
        c2f = Node.linkNodes(nodes[2], nodes[5], 2, roadInfo, null);
		e2d = Node.linkNodes(nodes[4], nodes[3], 2, roadInfo, null);
		e2f = Node.linkNodes(nodes[4], nodes[5], 3, roadInfo, null);
		e2c = Node.linkNodes(nodes[4], nodes[2], 2, roadInfo, null);
		f2e = Node.linkNodes(nodes[5], nodes[4], 3, roadInfo, null);

        graph = new Graph("ID", "", Arrays.asList(nodes), null);
        
        //Construction des scénarios de test
        
        Node [] origines;
        Node [] destinations;
        
        Map<Graph, List<Pair<Node, Node >>> scenarios = new HashMap<>();
        
        String basePath = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/";
        String[] graphPaths = {"Haute-Garonne", "french-polynesia.mapgr", "carre" };
        
        List<Graph> graphs = new ArrayList<>();
        GraphReader reader;
        for(String path : graphPaths) {
        	reader = new BinaryGraphReader(
                    new DataInputStream(new BufferedInputStream(new FileInputStream(basePath + path + ".mapgr"))));
        	
        	graphs.add(reader.read());
        }
        
        for(Graph g : graphs ) {
        	scenarios.put(g, new ArrayList<>());
        }
        
        scenarios.get(graphs.get(0)).add(new Pair<Node, Node>(graphs.get(0).get(10991), graphs.get(0).get(89149)) );// INSA -> Aeroport
        scenarios.get(graphs.get(0)).add(new Pair<Node, Node>(graphs.get(1).get(3765), graphs.get(1).get(67549)) ); // Ile 1 -> Ile 2
        scenarios.get(graphs.get(0)).add(new Pair<Node, Node>(graphs.get(2).get(9), graphs.get(2).get(89149)) ); // Carré coin -> coin (null)
        
        // Application de dixtra
        
        for(Graph g : scenarios.keySet() ) {
        	for(Pair p : scenarios.get(g)) {
        		
        	}
        }
    
    }


	@Test
	public void testTableauDistancier() {
		
		System.out.print("\t");
		for(Node node : nodes)
			System.out.print("   x" + node.getId() + "\t \t");
		
		System.out.println();
		
		for(Node origin : graph.getNodes()) {
			System.out.print("x" + origin.getId() + "\t");
			for(Node destination : graph.getNodes()) {
				
				if(origin.equals(destination)) {
					System.out.print("   Ø\t\t");
					continue;
				}
				
				ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(ArcInspectorFactory.TypeFiltre.NOFILTER.ordinal());
				
				ShortestPathData data = new ShortestPathData(graph, origin, destination, arcInspector);
				DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
				BellmanFordAlgorithm bellmanford = new BellmanFordAlgorithm(data);
				
				ShortestPathSolution solDijkstra = dijkstra.run();
				ShortestPathSolution solBellmanford = bellmanford.run();
				Path pathDijkstra = solDijkstra.getPath();
				Path pathBellmanford = solBellmanford.getPath();
				
				if(pathDijkstra != null && pathBellmanford != null) {
					assertEquals(pathDijkstra.getLength(), pathBellmanford.getLength(), 0.01f);
					System.out.print(pathDijkstra.getLength() + ", (x" + pathDijkstra.getArcs().get(pathDijkstra.getArcs().size() - 1).getOrigin().getId() +") \t");
				} else {
					assertNull(pathBellmanford);
					assertNull(pathDijkstra);
					System.out.print("   Ø\t\t");
				}
				
			}
			System.out.println();
		}
	}
	
	@Test
	public void testValiditeCarte() throws IOException {
        String mapName = "/home/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        GraphReader reader = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));
        Graph graph = reader.read();
        
        //metro rangueil
        Node origin = graph.get(1114);
        //metro fac de pharma
        Node destination = graph.get(420);
        
		ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(ArcInspectorFactory.TypeFiltre.NOFILTER.ordinal());
        
        ShortestPathData data = new ShortestPathData(graph, origin, destination, arcInspector);
		
	}

	
	@Test
	public void testValidechemin() {
		// Pour chaque combinaisons
		
		for (DijkstraAlgorithm algo : dijkstras) {
			
			ShortestPathSolution solution = algo.run();
			
			Path cheminSol = solution.getPath();
			assertTrue(cheminSol.isValid());
		}
	}
	

	
	@Test
	public void testCoutValide() {
		
		for (DijkstraAlgorithm algo : dijkstras) {
			
			ShortestPathSolution solution = algo.run();
			
			if(solution.getInputData().getMode() == Mode.LENGTH) {
				assertEquals(algo.getCost(), solution.getPath().getLength(), 0.1);

			}
			else if (solution.getInputData().getMode() == Mode.TIME) {
				assertEquals(algo.getCost(), solution.getPath().getMinimumTravelTime(), 0.1);
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
}
