package org.insa.algo.packageswitch;

import org.insa.algo.AbstractAlgorithm;
import org.insa.graph.Node;

public abstract class PackageSwitchAlgorithm extends AbstractAlgorithm<PackageSwitchObserver> {

    /**
     * Create a new PackageSwitchAlgorithm with the given data.
     *
     * @param data
     */
    protected PackageSwitchAlgorithm(PackageSwitchData data) {
        super(data);
    }

    @Override
    public PackageSwitchSolution run() {
        return (PackageSwitchSolution) super.run();
    }

    @Override
    protected abstract PackageSwitchSolution doRun();

    @Override
    public PackageSwitchData getInputData() {
        return (PackageSwitchData) super.getInputData();
    }

    public void notifyOriginProcessed(Node node) {
        for (PackageSwitchObserver obs : getObservers()) {
            obs.notifyOriginProcessed(node);
        }
    }
    
    public void notifyNodeReached(Node node, Integer nbMarked) {
        for (PackageSwitchObserver obs : getObservers()) {
            obs.notifyNodeReached(node, nbMarked);
        }
    }

    public void notifyNodeMarked(Node node) {
        for (PackageSwitchObserver obs : getObservers()) {
            obs.notifyNodeMarked(node);
        }
    }

}