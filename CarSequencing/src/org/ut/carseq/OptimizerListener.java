package org.ut.carseq;

/**
 * 
 */
public interface OptimizerListener 
{
    public void newSolutionFound(int iteration,CarSeqSolution solution);
    public void iterationCompleted(int n);	
}
