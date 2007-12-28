package org.ut.mh.tsp;

/**
 * 
 */
public interface OptimizerListener 
{
    public void newSolutionFound(int iteration,TSPSolution solution);
    public void iterationCompleted(int n);	
}
