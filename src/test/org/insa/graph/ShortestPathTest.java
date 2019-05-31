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

public class ShortestPathTest {
	
    // Small graph use for tests
    private static Graph graph;

    // List of nodes
    private static Node[] nodes;

    // List of arcs in the graph, a2b is the arc from node A (0) to B (1).
    @SuppressWarnings("unused")
    private static Arc a2b, a2c, b2d, b2e, b2f, c2a, c2b, c2f, e2d, e2f, e2c, f2e;
    
    
    private static List<DijkstraAlgorithm> dijkstras;
    
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
        
        //chemins valide
        scenarios.get(graphs.get(0)).add(new Paire<Node, Node>(graphs.get(0).get(10991), graphs.get(0).get(89149)) );// INSA -> Aeroport
        scenarios.get(graphs.get(3)).add(new Paire<Node, Node>(graphs.get(3).get(1840), graphs.get(3).get(3492)) ); // je sais plus
        scenarios.get(graphs.get(3)).add(new Paire<Node, Node>(graphs.get(3).get(15626), graphs.get(3).get(11286)) ); // Rangueille -> Jean Jores)
        
        // chemins nuls
        scenarios.get(graphs.get(0)).add(new Paire<Node, Node>(graphs.get(0).get(0), graphs.get(0).get(0)) );
        scenarios.get(graphs.get(1)).add(new Paire<Node, Node>(graphs.get(1).get(0), graphs.get(1).get(0)) );
        scenarios.get(graphs.get(2)).add(new Paire<Node, Node>(graphs.get(2).get(0), graphs.get(2).get(0)) );
        scenarios.get(graphs.get(3)).add(new Paire<Node, Node>(graphs.get(3).get(0), graphs.get(3).get(0)) ); 
        
        // non connexe
        scenarios.get(graphs.get(1)).add(new Paire<Node, Node>(graphs.get(1).get(8654), graphs.get(1).get(9444)) ); // Ile 1 -> Ile 2

        
    }

    
    
    @Test
    public void testDixtra() {

        for(Graph g : scenarios.keySet() ) {
        	
        	System.out.println("Carte : " + g.getMapName());
        	 
        	for(Paire<Node, Node> p : scenarios.get(g)) {
        		System.out.println("\tfrom : " + p.a.getId() + " to : " + p.b.getId());
        		
        		for(TypeFiltre filtre : TypeFiltre.values()) {
        			System.out.println("\t\t Filtre :" + filtre);
    				
        			long tDebut, tFin;

        			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(filtre.ordinal());
        			    				
    				ShortestPathData data = new ShortestPathData(g, p.a, p.b, arcInspector);
    				DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
    				BellmanFordAlgorithm bellmanford = new BellmanFordAlgorithm(data);
    				
    				tDebut = System.currentTimeMillis();
    				ShortestPathSolution solDijkstra = dijkstra.run();
    				tFin = System.currentTimeMillis();
    					
    				Path pathDijkstra = solDijkstra.getPath();
    				
    				double dureeParcour, distanceParcour;
    				
    				if(pathDijkstra == null) {
    					dureeParcour = 0;
    					distanceParcour = 0;
    				}
    				else {
    					dureeParcour = solDijkstra.getPath().getLength();
    					distanceParcour = solDijkstra.getPath().getMinimumTravelTime();
    				}
    				
    				
    				ShoretestPathPerfTest.ecrireResultat(g.getMapName(), "Dijkstra",filtre, p.a.getId(), p.b.getId(), distanceParcour, dureeParcour, tFin - tDebut);

    				
    				tDebut = System.currentTimeMillis();
    				ShortestPathSolution solBellmanford = bellmanford.run();
    				tFin = System.currentTimeMillis();
    				
    				
    				Path pathBellmanford = solBellmanford.getPath();

    				
    				
    				if(pathBellmanford == null) {
    					dureeParcour = 0;
    					distanceParcour = 0;
    				}
    				else {
    					dureeParcour = solBellmanford.getPath().getLength();
    					distanceParcour = solBellmanford.getPath().getMinimumTravelTime();
    				}
    				

    				
    				
    				ShoretestPathPerfTest.ecrireResultat(g.getMapName(), "Bellman",filtre, p.a.getId(), p.b.getId(), distanceParcour,	dureeParcour, tFin - tDebut);

    				

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
    public void testAStar() {

        for(Graph g : scenarios.keySet() ) {
        	
        	System.out.println("Carte : " + g.getMapName());
        	 
        	for(Paire<Node, Node> p : scenarios.get(g)) {
        		System.out.println("\tfrom : " + p.a.getId() + " to : " + p.b.getId());
        		
        		for(TypeFiltre filtre : TypeFiltre.values()) {
        			System.out.println("\t\t Filtre :" + filtre);
    				
        			long tDebut, tFin;

        			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(filtre.ordinal());
        			    				
    				ShortestPathData data = new ShortestPathData(g, p.a, p.b, arcInspector);
    				DijkstraAlgorithm astar = new AStarAlgorithm(data);
    				BellmanFordAlgorithm bellmanford = new BellmanFordAlgorithm(data);
    				
    				
    				tDebut = System.currentTimeMillis();
    				ShortestPathSolution solAstar = astar.run();
    				tFin = System.currentTimeMillis();
    					
    				Path pathAStar = solAstar.getPath();
    				
    				double dureeParcour, distanceParcour;
    				
    				if(pathAStar == null) {
    					dureeParcour = 0;
    					distanceParcour = 0;
    				}
    				else {
    					dureeParcour = solAstar.getPath().getLength();
    					distanceParcour = solAstar.getPath().getMinimumTravelTime();
    				}
    				
    				
    				ShoretestPathPerfTest.ecrireResultat(g.getMapName(), "AStar",filtre, p.a.getId(), p.b.getId(), distanceParcour, dureeParcour, tFin - tDebut);

    				
    				tDebut = System.currentTimeMillis();
    				ShortestPathSolution solBellmanford = bellmanford.run();
    				tFin = System.currentTimeMillis();
    				
    				
    				Path pathBellmanford = solBellmanford.getPath();

    				
    				
    				if(pathBellmanford == null) {
    					dureeParcour = 0;
    					distanceParcour = 0;
    				}
    				else {
    					dureeParcour = solBellmanford.getPath().getLength();
    					distanceParcour = solBellmanford.getPath().getMinimumTravelTime();
    				}
    				

    				
    				
    				ShoretestPathPerfTest.ecrireResultat(g.getMapName(), "Bellman",filtre, p.a.getId(), p.b.getId(), distanceParcour,	dureeParcour, tFin - tDebut);

    				

    				if(pathAStar == null && pathBellmanford == null) {
    					System.out.println("0");
    					assertNull(pathBellmanford);
    					assertNull(pathAStar);

    				} else {
    					System.out.println(pathAStar.getLength() );
    					System.out.println(pathBellmanford.getLength());
    					assertEquals(pathAStar.getLength(), pathBellmanford.getLength(), 0.01f);
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
