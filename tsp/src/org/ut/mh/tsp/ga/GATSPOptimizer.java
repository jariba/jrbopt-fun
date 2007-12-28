package org.ut.mh.tsp.ga;

import java.util.List;
import org.ut.mh.tsp.OptimizerBase;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.util.Log;

/**
 * Optimizer for the TSP problem using SA
 *
 */
public class GATSPOptimizer 
    extends OptimizerBase
{
	protected int maxFailures_; // max number of consecutive failures before we quit
	protected GAProblemMapper mapper_;
	
	public GATSPOptimizer(String name,
			              GAProblemMapper mapper,
			              int maxFailures)
	{
		super(name);
        mapper_ = mapper;
        maxFailures_ = maxFailures;
	}
	
	public TSPSolution solve(List cities)
	{
		resetBestSolution();
		
		//Util.printList("Solving TSP for:",cities,"\n");		
		GAPopulation population = mapper_.getInitialPopulation(cities);
		TSPSolution bestSolution = population.getBestMember();
		double bestCost = bestSolution.getCost();
    	notifyNewSolution(0,bestSolution);
				
		int failures=0;		
		for (int i=0;failures<maxFailures_;i++) {
		    TSPSolution nextSolution = mapper_.makeNewMember(population);
		    if (population.addMember(nextSolution))
				mapper_.deleteMember(population);
		    //else
		    //  TODO: Do something when we're stuck here for a number of iterations
		    //	Log.debug("duplicate new member at iteration "+i);

		    double nextCost = nextSolution.getCost();
			if (nextCost<bestCost) {
    	    	bestSolution = nextSolution;
		    	bestCost = nextCost;
				notifyNewSolution(i+1,bestSolution);
				failures=0;
		    }
		    else {
		    	failures++;
		    }			
   			notifyIterationCompleted(i);
		}
					
		logBestSolution();		
        Log.debug("GATSPOptimizer done");
		return bestSolution;
	}	
}
