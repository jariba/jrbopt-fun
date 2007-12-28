package org.ut.mh.tsp.grasp;

import java.util.List;
import org.ut.mh.tsp.*;
//import org.ut.mh.tsp.util.*;

/**
 * Optimizer for the TSP problem using GRASP
 *
 */
public class GraspTSPOptimizer 
    extends OptimizerBase
{
	protected int nc_; // number of candidates to try
	protected CandidateBuilder cb_;
	protected LocalSearch ls_;
    
	public GraspTSPOptimizer(String name,
			                 int nc,
			                 CandidateBuilder cb,
							 LocalSearch ls)
	{
		super(name);
		nc_ = nc;
		cb_ = cb;
		ls_=ls;
	}
	
	public TSPSolution solve(List cities)
	{
		resetBestSolution();
		
		//Util.printList("Solving TSP for:",cities,"\n");		
		TSPSolution bestSolution = null;
		double bestCost = Double.MAX_VALUE;
		
		for (int i=0;i<nc_;i++) {
			List candidate = cb_.getCandidate(cities);
			TSPSolution solution = new TSPSolution();
			solution.addTour(candidate);
			//solution.print("GMMOG Candidate:");
			solution = ls_.doLocalSearch(solution);
			//solution.print("GMMOG Candidate after Local Search:");
			double cost = solution.getCost();
			if (cost < bestCost) {
				bestSolution = solution;
				bestCost = cost;
				notifyNewSolution(i+1,bestSolution);
			}	
			notifyIterationCompleted(i+1);
		}
		
		logBestSolution();		
		return bestSolution;
	}
}
