package org.ut.mh.tsp;

import java.util.List;

/**
 */
public interface Optimizer 
{
	public String getName();
    public void addOptimizerListener(OptimizerListener o);
    public void removeOptimizerListener(OptimizerListener o);
	public TSPSolution solve(List cities);
}
