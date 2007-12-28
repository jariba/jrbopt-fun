package org.ut.mh.tsp.sa;

import java.util.*;

import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.grasp.CandidateBuilder1;
import org.ut.mh.tsp.grasp.LSOneSwap;
import org.ut.mh.tsp.util.Util;

/**
 */
public class SAProblemMapper_2TSP_JB1 
    implements SAProblemMapper 
{
	protected double initialT_;
	protected double tChangeRate_;
	protected int itPerStage_;
	
	public SAProblemMapper_2TSP_JB1(
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
		List tour1 =  cb.getCandidate(cities);
		List tour2 = new Vector();
		// Just split initial tour in half as initial solution
		int half = tour1.size()/2;
		for (int i=0;i<half;i++) {
			 Object obj = tour1.remove(tour1.size()-1);
             tour2.add(0,obj);			
		}

		solution.addTour(tour1);
		solution.addTour(tour2);
		
		return solution;
	}

	public TSPSolution getNextNeighbor(TSPSolution solution) 
	{
		TSPSolution s = solution.makeCopy();
		
		// Get a tour to reduce
		int idx = Util.randomPick(2);
		List tour1 = s.getTour(idx);
		List tour2 = s.getTour((idx+1)%2);
		
		if (tour1.size()>0) {
    		// Get number of elements to move
	    	int nMoves = Util.randomPick(Math.min(3,tour1.size()));
	    	for (int i=0;i<nMoves;i++) {
	    		Object obj = tour1.remove(tour1.size()-1);
	    		tour2.add(0,obj);			
	    	}
		}
		
        LSOneSwap ls = new LSOneSwap();
        s = ls.doLocalSearch(s);
        		
		return s;
	}
}
