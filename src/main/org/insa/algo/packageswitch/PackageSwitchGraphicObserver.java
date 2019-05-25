package org.insa.algo.packageswitch;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import org.insa.graph.Node;
import org.insa.graphics.drawing.Drawing;
import org.insa.graphics.drawing.overlays.PointSetOverlay;

public class PackageSwitchGraphicObserver implements PackageSwitchObserver {
	
	List<PointSetOverlay> overlays;
	
	public PackageSwitchGraphicObserver(Drawing drawing) {
		this.drawing = drawing;
		psOverlay1 = drawing.createPointSetOverlay(1, Color.CYAN);
		psOverlay2 = drawing.createPointSetOverlay(1, Color.BLUE);
		psOverlay3 = drawing.createPointSetOverlay(1, Color.RED);
		psOverlay4 = drawing.createPointSetOverlay(1, Color.GREEN);
		
		this.overlays = Arrays.asList(psOverlay1, psOverlay2, psOverlay3, psOverlay4);
	}

    // Drawing and Graph drawing
    protected Drawing drawing;
    protected PointSetOverlay psOverlay1, psOverlay2, psOverlay3, psOverlay4;


    @Override
    public void notifyOriginProcessed(Node node) {
        // drawing.drawMarker(node.getPoint(), Color.RED);
    }
    
    @Override
    public void notifyNodeReached(Node node, Integer color) {
    	overlays.get(color).addPoint(node.getPoint());
    }

    @Override
    public void notifyNodeMarked(Node node) {
        psOverlay2.addPoint(node.getPoint());
    }

	@Override
	public void notifyDestinationReached(Node node) {
		// TODO Auto-generated method stub	
	}

}
