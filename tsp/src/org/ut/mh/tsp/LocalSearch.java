package org.ut.mh.tsp;


/**
 * Performs LocalSearch on a Candidate TSP Solution
 */
public interface LocalSearch 
{
	/*
	 * Performs local search and returns the best improving solution
	 * on the original TSP path provided 
	 */
    public TSPSolution doLocalSearch(TSPSolution solution);
}
