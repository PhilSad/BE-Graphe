package org.insa.graph;

import static org.junit.Assert.*;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
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
        String[] graphPaths = {"carre-dense"};
        
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
        
        scenarios.get(graphs.get(0)).add(new Paire<Node, Node>(graphs.get(0).get(341598), graphs.get(0).get(304768)) );// INSA -> Aeroport
        //scenarios.get(graphs.get(1)).add(new Paire<Node, Node>(graphs.get(1).get(8654), graphs.get(1).get(9444)) ); // Ile 1 -> Ile 2
        //scenarios.get(graphs.get(2)).add(new Paire<Node, Node>(graphs.get(2).get(9), graphs.get(2).get(9)) ); // Carré coin -> coin (null)
        
        
        // INSPECTORS
        
        
        
        // Application de dixtra
        
    
    }

    
    private long moyenneTab(long [] tab) {
    	long somme = 0;
    	for(long e : tab) {
    		somme += e;
    	}
    	return somme / tab.length;
    }
    
    public static void ecrireResultat(String map, String algo, TypeFiltre filtre, int orig, 
    		int dest, double distance, double duree, long temps) {
    	
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("./out.txt", true));
			
			String content = map + ","+ algo + ","+ filtre + "," + orig + "," + dest +","+distance+","+ duree + "," + temps + "\n";
			
			writer.write(content);
			
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
    
    
    @Test
    public void testPerfCompare() {
    	
    	
    	
    	
    	
        for(Graph g : scenarios.keySet() ) {
        	
        	System.out.println("Carte : " + g.getMapName());
        	 
        	for(Paire<Node, Node> p : scenarios.get(g)) {
        		System.out.println("\tfrom : " + p.a.getId() + " to : " + p.b.getId());
        		
        			TypeFiltre filtre = TypeFiltre.values()[0];
        			System.out.println("\t\t Filtre :" + filtre);
        			
        			ArcInspector arcInspector = ArcInspectorFactory.getAllFilters().get(filtre.ordinal());
        			    				
    				ShortestPathData data = new ShortestPathData(g, p.a, p.b, arcInspector);

    				long [] tempsAstar = new long[3]; 
    				long [] tempsDiskstra = new long[3]; 
    				long [] tempsBellman = new long[3]; 
    				
    				long tDebut, tFin;
    				
    				for(int i = 0; i < 3; i++) {
    					
    					System.out.println("\t\t\tTest numero " + i);
    					
	    				AStarAlgorithm astar = new AStarAlgorithm(data);
	    				DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
	    				BellmanFordAlgorithm bellmanford = new BellmanFordAlgorithm(data);
	    				
	    				tDebut = System.currentTimeMillis();
	    				ShortestPathSolution solAStar = astar.run();
	    				tFin = System.currentTimeMillis();	    				
	    				tempsAstar[i] = tFin - tDebut;
	    				
	    				
	    				tDebut = System.currentTimeMillis();
	    				ShortestPathSolution solDijkstra = dijkstra.run();
	    				tFin = System.currentTimeMillis();    				
	    				tempsDiskstra[i] = tFin - tDebut;
	    				
	    				tDebut = System.currentTimeMillis();
	    				ShortestPathSolution solBellmanford = bellmanford.run();
	    				tFin = System.currentTimeMillis();
	    				
	    				tempsBellman[i] = tFin - tDebut;
	    				
	    				ecrireResultat(g.getMapName(), "AStar",filtre, p.a.getId(), p.b.getId(), solAStar.getPath().getLength(), solAStar.getPath().getMinimumTravelTime(), tempsAstar[i]);
	    				ecrireResultat(g.getMapName(), "Dijkstra",filtre, p.a.getId(), p.b.getId(), solDijkstra.getPath().getLength(), solDijkstra.getPath().getMinimumTravelTime(), tempsDiskstra[i]);
	    				ecrireResultat(g.getMapName(), "Bellman", filtre, p.a.getId(), p.b.getId(), solBellmanford.getPath().getLength(), solBellmanford.getPath().getMinimumTravelTime(), tempsBellman[i]);
	    				    				
    				}
    				
    				System.out.println("\t\t\t\t  Temps Bellman " + moyenneTab(tempsBellman));
    				System.out.println("\t\t\t\t  Temps Dijkstra " + moyenneTab(tempsDiskstra));
    				System.out.println("\t\t\t\t  Temps Astar " + moyenneTab(tempsAstar));
    				    				
			
        			
        		
        		
        		System.out.println("Scénario suivant");
        		
        	}
        }
    
	}
	
	
	
	
	
	
	
	
	
	
}
