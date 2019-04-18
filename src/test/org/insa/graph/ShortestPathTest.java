package org.insa.graph;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

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

}
