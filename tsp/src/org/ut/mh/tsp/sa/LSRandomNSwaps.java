package org.ut.mh.tsp.sa;

import java.util.List;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.util.Util;

/*
 * Does local search by swapping a 
 */
public class LSRandomNSwaps 
{
    // perform this number of random swaps
    protected int swaps_; 
    
    public LSRandomNSwaps(int swaps)
    {
    	swaps_=swaps;
    }
    
	/*
	 * randomly pick 2 cities to swap, swap them, repeat until predefined number 
	 * of swaps completed 
	 */
	public TSPSolution doSwaps(TSPSolution solution) 
	{
		double bestCost = solution.getCost();
		
		for (int i=0;i<solution.getTourCnt();i++) {
		    List cities = solution.getTour(i);
		    if (cities.size()==0)
		    	continue;
		    		    
		    for(int nSwaps=0;nSwaps<swaps_;nSwaps++) {
		    	int a = Util.randomPick(cities.size());
		    	int b = Util.randomPick(cities.size());
		    	swap(cities,a,b);
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
