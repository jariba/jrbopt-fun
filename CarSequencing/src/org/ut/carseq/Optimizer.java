package org.ut.carseq;

/**
 */
public interface Optimizer 
{
	public String getName();
    public void addOptimizerListener(OptimizerListener o);
    public void removeOptimizerListener(OptimizerListener o);
	public CarSeqSolution solve(CarSequence carsToSequence);
}
