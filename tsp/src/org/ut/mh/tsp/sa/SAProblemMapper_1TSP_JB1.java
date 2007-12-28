package org.ut.mh.tsp.sa;

import java.util.List;

import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.grasp.CandidateBuilder1;
import org.ut.mh.tsp.grasp.LSOneSwap;

/**
 */
public class SAProblemMapper_1TSP_JB1 
    implements SAProblemMapper 
{
	protected double initialT_;
	protected double tChangeRate_;
	protected int itPerStage_;
	
	public SAProblemMapper_1TSP_JB1(
			    double initialT,
				double tChangeRate,
				int iterations)
	{
		initialT_ = initialT;
		tChangeRate_ = tChangeRate;
		itPerStage_=iterations;
	}
	
	public double getInitialTemperature() 
	{
		return initialT_;
	}

	public int getIterationsAtTemperature(double temperature) 
	{
		return itPerStage_;
	}

	public double decreaseTemperature(double temperature) 
	{
		return temperature*tChangeRate_;
	}

	public TSPSolution getInitialSolution(List cities) 
	{
		// return tour where the closest city is picked for each step
		CandidateBuilder1 cb = new CandidateBuilder1(1);
		TSPSolution solution = new TSPSolution();
		solution.addTour(cb.getCandidate(cities));
		
		return solution;
	}

	public TSPSolution getNextNeighbor(TSPSolution solution) 
	{
		TSPSolution s = solution.makeCopy();
        LSRandomNSwaps ls = new LSRandomNSwaps(10);
        s = ls.doSwaps(s);
        LSOneSwap ls1 = new LSOneSwap();
        s = ls1.doLocalSearch(s);
        
		return s;
	}
}
