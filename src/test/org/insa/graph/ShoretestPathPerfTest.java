package org.insa.graph;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.insa.algo.AbstractInputData.Mode;
import org.insa.algo.ArcInspector;
import org.insa.algo.ArcInspectorFactory;
import org.insa.algo.ArcInspectorFactory.TypeFiltre;
import org.insa.algo.shortestpath.AStarAlgorithm;
import org.insa.algo.shortestpath.BellmanFordAlgorithm;
import org.insa.algo.shortestpath.DijkstraAlgorithm;
import org.insa.algo.shortestpath.ShortestPathData;
import org.insa.algo.shortestpath.ShortestPathSolution;
import org.insa.graph.RoadInformation.RoadType;
import org.insa.graph.io.BinaryGraphReader;
import org.insa.graph.io.GraphReader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ShoretestPathPerfTest {
	
        
    private static Map<Graph, List<Paire<Node, Node >>> scenarios;
    
    
    static class Paire <A,B> {
    	A a;
    	B b;
    	public Paire(A a, B b) {
    		this.a = a;
    		this.b = b;
    	}
    }
    
    @BeforeClass
    public static void initAll() throws IOException {
        
        scenarios = new HashMap<>();
        
        String basePath = "/home/phil/Téléchargements/";
        String[] graphPaths = {"haute-garonne", "french-polynesia", "carre", "toulouse"};
        
        List<Graph> graphs = new ArrayList<>();
        GraphReader reader;
        for(String path : graphPaths) {
        	//System.out.println(basePath + path + ".mapgr");
        	reader = new BinaryGraphReader(
                    new DataInputStream(new BufferedInputStream(new FileInputStream(basePath + path + ".mapgr"))));
        	
        	graphs.add(reader.read());
        }
        
        for(Graph g : graphs ) {
        	scenarios.put(g, new ArrayList<>());
        }
        
        scenarios.get(graphs.get(0)).add(new Paire<Node, Node>(graphs.get(0).get(10991), graphs.get(0).get(89149)) );// INSA -> Aeroport
        scenarios.get(graphs.get(1)).add(new Paire<Node, Node>(graphs.get(1).get(8654), graphs.get(1).get(9444)) ); // Ile 1 -> Ile 2
        scenarios.get(graphs.get(2)).add(new Paire<Node, Node>(graphs.get(2).get(9), graphs.get(2).get(9)) ); // Carré coin -> coin (null)
        
        
        // INSPECTORS
        
        
        
        // Application de dixtra
        
    
    }

    
    
    @Test
    public void testPerfCompare() {

        for(Graph g : scenarios.keySet() ) {
        	
        	System.out.println("Carte : " + g.getMapName());
        	 
        	for(Paire<Node, Node> p : scenarios.get(g)) {
        		System.out.println("\tfrom : " + p.a.getId() + " to : " + p.b.getId());
        		
        		for(TypeFiltre filtre : TypeFiltre.values()) {
        			System.out.println("\t\t Filtre :" + filtre);
        			
        			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(filtre.ordinal());
        			    				
    				ShortestPathData data = new ShortestPathData(g, p.a, p.b, arcInspector);

    				long [] tempsAstar = new long[3]; 
    				long [] tempsDiskstra = new long[3]; 
    				long [] tempsBellman = new long[3]; 
    				
    				long tDebut, tFin;
    				
    				for(int i = 0; i < 3; i++) {
    				
	    				AStarAlgorithm astar = new AStarAlgorithm(data);
	    				DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
	    				BellmanFordAlgorithm bellmanford = new BellmanFordAlgorithm(data);
	    				
	    				tDebut = System.currentTimeMillis();
	    				ShortestPathSolution solAStar = astar.run();
	    				tFin = System.
	    				
	    				ShortestPathSolution solDijkstra = dijkstra.run();
	    				ShortestPathSolution solBellmanford = bellmanford.run();
    				
    				}
    				
    				Path pathDijkstra = solDijkstra.getPath();
    				Path pathBellmanford = solBellmanford.getPath();
    				
    				if(pathDijkstra == null && pathBellmanford == null) {
    					System.out.println("0");
    					assertNull(pathBellmanford);
    					assertNull(pathDijkstra);

    				} else {
    					System.out.println(pathDijkstra.getLength() );
    					System.out.println(pathBellmanford.getLength());
    					assertEquals(pathDijkstra.getLength(), pathBellmanford.getLength(), 0.01f);
    				}   			
        			
        		}
        		
        		System.out.println("Scénario suivant");
        		
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
