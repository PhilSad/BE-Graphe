package org.insa.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.insa.graph.RoadInformation.RoadType;
import org.junit.After;
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

    // Some paths...
    private static Path emptyPath, singleNodePath, shortPath, longPath, loopPath, longLoopPath,
            invalidPath;

    @BeforeClass
    public static void initAll() throws IOException {

        // 10 and 20 meters per seconds
        RoadInformation speed10 = new RoadInformation(RoadType.MOTORWAY, null, true, 36, ""),
                speed20 = new RoadInformation(RoadType.MOTORWAY, null, true, 72, "");

        // Create nodes
        nodes = new Node[6];
        for (int i = 0; i < nodes.length; ++i) {
            nodes[i] = new Node(i, null);
        }

        // Add arcs...
        a2b = Node.linkNodes(nodes[0], nodes[1], 7, null, null);
        a2c = Node.linkNodes(nodes[0], nodes[2], 8, null, null);
        b2d = Node.linkNodes(nodes[1], nodes[3], 8, null, null);
        
        
        b2d = Node.linkNodes(nodes[0], nodes[3], 15, speed20, null);
        b2c = Node.linkNodes(nodes[1], nodes[2], 10, speed10, null);
        c2d_1 = Node.linkNodes(nodes[2], nodes[3], 20, speed10, null);
        c2d_2 = Node.linkNodes(nodes[2], nodes[3], 10, speed10, null);
        c2d_3 = Node.linkNodes(nodes[2], nodes[3], 15, speed20, null);
        d2a = Node.linkNodes(nodes[3], nodes[0], 15, speed10, null);
        d2e = Node.linkNodes(nodes[3], nodes[4], 22.8f, speed20, null);
        e2d = Node.linkNodes(nodes[4], nodes[0], 10, speed10, null);

        graph = new Graph("ID", "", Arrays.asList(nodes), null);

        emptyPath = new Path(graph, new ArrayList<Arc>());
        singleNodePath = new Path(graph, nodes[1]);
        shortPath = new Path(graph, Arrays.asList(new Arc[] { a2b, b2c, c2d_1 }));
        longPath = new Path(graph, Arrays.asList(new Arc[] { a2b, b2c, c2d_1, d2e }));
        loopPath = new Path(graph, Arrays.asList(new Arc[] { a2b, b2c, c2d_1, d2a }));
        longLoopPath = new Path(graph,
                Arrays.asList(new Arc[] { a2b, b2c, c2d_1, d2a, a2c, c2d_3, d2a, a2b, b2c }));
        
	
	
	
	
	
	
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
