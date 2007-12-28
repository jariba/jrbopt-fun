package org.ut.mh.tsp.sa;

import java.util.List;
import org.ut.mh.tsp.OptimizerBase;
import org.ut.mh.tsp.TSPSolution;
//import org.ut.mh.tsp.util.Log;

/**
 * Optimizer for the TSP problem using SA
 *
 */
public class SATSPOptimizer 
    extends OptimizerBase
{
	protected int maxStageFailures_; // max number of consecutive stage failures before we quit
	protected SAProblemMapper mapper_;
	
	public SATSPOptimizer(String name,
			              SAProblemMapper mapper,
			              int maxStageFailures)
	{
		super(name);
        mapper_ = mapper;
        maxStageFailures_ = maxStageFailures;
	}
	
	public TSPSolution solve(List cities)
	{
		resetBestSolution();
		
		//Util.printList("Solving TSP for:",cities,"\n");		
		TSPSolution bestSolution = mapper_.getInitialSolution(cities);
    	notifyNewSolution(0,bestSolution);
		double bestCost = bestSolution.getCost();
		double t = mapper_.getInitialTemperature();
		
		TSPSolution curSolution = bestSolution;
		double curCost = curSolution.getCost();
		
		int totalIterations=0;
		int stageFailures=0;		
		while (stageFailures<maxStageFailures_) {
			double initialCost=bestCost;
			int maxIter = mapper_.getIterationsAtTemperature(t);
			for (int i=0;i<maxIter;i++) {
			    TSPSolution nextSolution = mapper_.getNextNeighbor(curSolution);
			    //nextSolution.print("Next Neighbor:");
			    double nextCost = nextSolution.getCost();
			    if (nextCost<curCost) {
			    	curSolution = nextSolution;
			    	curCost = nextCost;
				    if (curCost < bestCost) {
				    	bestSolution = curSolution;
				    	bestCost = curCost;
				    	notifyNewSolution(totalIterations+1,bestSolution);
				    }				    				    	
			    }
			    else {
			    	if (makeStep(curCost-nextCost,t)) {
				    	curSolution = nextSolution;
				    	curCost = nextCost;
				    	//Log.debug("Accepted disimproving move, cost:"+curCost);
			    	}
			    	else {
				    	//Log.debug("Rejected disimproving move, cost:"+curCost);			    		
			    	}
			    }
			    totalIterations++;
    			notifyIterationCompleted(totalIterations);
			}
			
			t = mapper_.decreaseTemperature(t);
			
			if (bestCost < initialCost)
				stageFailures=0;
			else 
				stageFailures++;
		}
		
		logBestSolution();		
		return bestSolution;
	}
	
	/*
	 * delta is < 0
	 * if exp(delta/t) is less that a generated uniform(0,1)
	 * the we will make a disimproving step
	 */
	protected boolean makeStep(double delta,double t)
	{		
		double uniform = Math.random();
		double expValue = Math.exp(delta/t);
		return expValue>=uniform;
	}
}
