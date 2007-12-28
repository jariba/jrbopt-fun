package org.ut.mh.tsp.grasp;

import java.util.List;

import org.ut.mh.tsp.LocalSearch;
import org.ut.mh.tsp.TSPSolution;

/*
 * Systematically perform all possible one swap operations
 * until no further improvement is found
 */
public class LSOneSwap 
    implements LocalSearch 
{    
    public LSOneSwap()
    {
    }
    
	public TSPSolution doLocalSearch(TSPSolution solution) 
	{
		double bestCost = solution.getCost();
		for (int nc=0;nc<solution.getTourCnt();nc++) {
			List cities = solution.getTour(nc);
			boolean keepRunning=true;
			while (keepRunning) {
				keepRunning=false;
				for (int i=0;i<cities.size()-1;i++) {
					for(int j=i+1;j<cities.size();j++) {
						//Log.debug("Swapped "+i+","+j);
						swap(cities,i,j);
						double cost = solution.getCost();
						if (cost < bestCost) { 
							bestCost = cost;
							keepRunning=true;
							//GraspTSPOptimizer.printSolution("LocalSearch improvement:",cities);
						}
						else {
							swap(cities,j,i);
						}
					}
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
