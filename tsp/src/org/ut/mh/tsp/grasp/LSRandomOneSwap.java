package org.ut.mh.tsp.grasp;

import java.util.List;

import org.ut.mh.tsp.LocalSearch;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.util.Util;

/*
 * Does local search by swapping a 
 */
public class LSRandomOneSwap 
    implements LocalSearch 
{
    // Do local search until this number of consecutive
    // failures to improve the solution is reached
    protected int failureThreshold_; 
    
    public LSRandomOneSwap(int failureThreshold)
    {
    	failureThreshold_ = failureThreshold;
    }
    
	/*
	 * randomly pick 2 cities to swap, swap them, repeat until stuck on a local
	 * minimum for a number of iterations 
	 */
	public TSPSolution doLocalSearch(TSPSolution solution) 
	{
		double bestCost = solution.getCost();
		
		for (int i=0;i<solution.getTourCnt();i++) {
		    List cities = solution.getTour(i);
		    if (cities.size()==0)
		    	continue;
		    
		    int failures=0;
		    for(;failures<failureThreshold_;) {
		    	int a = Util.randomPick(cities.size());
		    	int b = Util.randomPick(cities.size());
		    	swap(cities,a,b);
		    	double cost = solution.getCost();
		    	if (cost < bestCost) { 
		    		bestCost = cost;
		    		failures=0;
		    		//solution.print("LocalSearch improvement:");
		    	}
		    	else {
		    		swap(cities,b,a);
		    		failures++;
		    	}
		    }
		}
		
		return solution;
	}
	
	protected void swap(List cities,int a,int b)
	{
		Object tmp = cities.get(a);
		cities.set(a,cities.get(b));
		cities.set(b,tmp);
	}
}
