package org.ut.carseq;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.SortedSet;
import java.util.TreeSet;

import org.ut.util.Log;

// Optimizer for a Car SequencingProblem using Tabu Search
public class CarSequencingSolverTS 
    extends OptimizerBase
{
	int failures_;
	int maxFailures_;
	int restarts_=0;
    int restartLimit_ = 300;
    
	protected int itSinceTTUpdate_;  // Iterations since tabu tenure update 
	int initMoveTabuTenure_;
	int initSolutionTabuTenure_;
	protected MoveTabuList moveTabuList_;
    protected SolutionTabuHashTable solutionTabuHT_;
	
	// Dynamic neighborhood selection
	protected int currentDNS_;
	protected int dnsCnt_;
	protected int maxDNSCnt_;
	protected int dnsThreshold_;           // number of failures to switch from insert negihborhood to

	protected final int DIVERSIFY_NEIGHBORHOOD=0;
	protected final int INTENSIFY_NEIGHBORHOOD=1;
	
    
	public CarSequencingSolverTS(String name,
			                     int moveTabuTenure,
			                     int solutionTabuTenure,
			                     int maxFailures)
	{
		super("Tabu Search");
	    maxFailures_ = maxFailures;	
	    initMoveTabuTenure_ = moveTabuTenure;
	    initSolutionTabuTenure_ = solutionTabuTenure;
	    itSinceTTUpdate_ = 0;
		solutionTabuHT_ = new SolutionTabuHashTable(
				initSolutionTabuTenure_,
				1.2,
				0.9
		);

		// Dynamic Neighborhood properties
		currentDNS_ = INTENSIFY_NEIGHBORHOOD;
        dnsThreshold_ = 100;    
        dnsCnt_ = 0;
        maxDNSCnt_ = 5;
	}
	
    public CarSeqSolution solve(CarSequence carsToSequence)
    {
    	resetBestSolution();
    	
		moveTabuList_ = new MoveTabuList(
				carsToSequence.size(),
				initMoveTabuTenure_,
				1.2,
				0.5);
		
		// TODO: use initTabuTenure_
		solutionTabuHT_.reset(carsToSequence.size());
		
    	CarSequence curSeq = getInitialSolution(carsToSequence);
    	CarSeqSolution bestSolution = new CarSeqSolution(curSeq);    	
		double bestCost = bestSolution.getCost();		
		
    	notifyNewSolution(0,bestSolution);
				
		int i=0;
		int maxRestarts = Math.max(1,maxFailures_/restartLimit_);
		for (;/*failures_ < maxFailures_*/restarts_<maxRestarts;i++) {
		    CarSequence nextSolution = getNextSolution(curSeq,i,bestCost);

		    double nextCost = nextSolution.getCost();
			if (nextCost<bestCost) {
    	    	bestSolution = new CarSeqSolution(nextSolution);
		    	bestCost = nextCost;
		    	bestSolution.setElapsedTimeStr(solutionTimer_.getElapsedString());
				notifyNewSolution(i+1,bestSolution);
				failures_=0;
				restarts_=0;
				moveTabuList_.tabuTenure_ = (int)bestCost;
				if (bestCost == 0)
					break;
		    }
		    else {
		    	failures_++;

		    	if ((failures_ % restartLimit_) == 0) {
		    		restart(curSeq,i);
		    	}		    	
		    }		
			curSeq=nextSolution;
   			notifyIterationCompleted(i);
   			//Log.debug(i+"-Violations:"+nextCost);
		}
					
		logBestSolution();		
        Log.debug("TabuSearch Car Sequencing Optimizer Finished. "+ 
        		i + " iterations in " + 
        		solutionTimer_.getElapsedString());
		return bestSolution;
    }
    
    protected void restart(CarSequence currentSolution,
    		               int currentIteration)
    {
    	// Re-start
    	int size = currentSolution.size();
    	for (int i=0; i<size; ) {
    		int fromPos = i;
    		int toPos = ((size-i)*3 + 5) % size;
    		currentSolution.swapCars(toPos, fromPos);
    		i+=2;
    	}
    	setCurrentNeighborhood(INTENSIFY_NEIGHBORHOOD,currentIteration);
    	restarts_++;
    	failures_ = 0;
    	Log.debug("Iteration "+currentIteration+": Re-start completed");    	
    }
    
    CarSequence getInitialSolution(CarSequence carsToSequence)
    {
    	// TODO: seed with heuristic?
    	return carsToSequence;
    }
    
    CarSequence getNextSolution(CarSequence currentSolution,
    		                    int currentIteration,
    		                    double bestCost)
    {
		// Dynamic neighborhood selection 
    	if (failures_ < dnsThreshold_) {
	    	// Set the main neighborhood
	    	setCurrentNeighborhood(INTENSIFY_NEIGHBORHOOD,currentIteration);
	    }
	    else {
	    	// If we get stuck, alternate between the neighborhoods until we find an improving move
	    	if (dnsCnt_==maxDNSCnt_) {
	    		setCurrentNeighborhood(currentDNS_ == DIVERSIFY_NEIGHBORHOOD ? INTENSIFY_NEIGHBORHOOD : DIVERSIFY_NEIGHBORHOOD,
	    				 currentIteration);	    		
	    	}
    		dnsCnt_ = ((dnsCnt_-1) < 0 ? maxDNSCnt_ : dnsCnt_-1);
	    }
	    

    	CarSequence s = currentSolution;
	    SortedSet moves = getMoves(s);    
	    CarSeqSwapMove bestMove = null;
	    Iterator it = moves.iterator();	    
	    while (it.hasNext()) {
	    	CarSeqSwapMove move = (CarSeqSwapMove)it.next();
	    	if (!moveTabuList_.isTabu(move,currentIteration)) {
	    		if (makeMove(move,s,currentIteration,false)) {
	    		    bestMove = move;
	        		break;
	    		}
	    	}
	    	else {
	    		// Apply aspiration criteria
	    		if (move.getCost()<bestCost) {
		    		makeMove(move,s,currentIteration,true);
  	    			bestMove = move;
        		    Log.debug("Iteration:"+currentIteration+" bestCost:"+bestCost+" applied aspiration criteria for move:"+move);
        		    break;
	    		}
	    	}
	    }	    
	    //Log.debug("Iteration:"+currentIteration+" rejected solutions-moves:"+rejectedSolutions+","+rejectedMoves);
	    	    
	    if (bestMove == null) {
     	    // all the moves are tabu
	    	bestMove = (CarSeqSwapMove)moves.first();
		    makeMove(bestMove,s,currentIteration,true); 
			Log.debug("Iteration:"+currentIteration+" Forced to make tabu move "+bestMove);
	    	//decreaseTabuTenure(currentIteration);
	    }	    

    	itSinceTTUpdate_++;	    
        return currentSolution;	
    }

	void setCurrentNeighborhood(int n,int currentIteration)
	{
		if (currentDNS_!=n) {
			currentDNS_ = n;		
    		String name = (currentDNS_ == DIVERSIFY_NEIGHBORHOOD ? "diversify" : "intensify");
		    Log.debug("Iteration:"+currentIteration+" switching to "+name+" neighborhood");
		}
	}
	    
	SortedSet getMoves(CarSequence s)
	{
		SortedSet moves = new TreeSet();

		int minIdx=0,maxIdx=s.size();
		int localIdx = maxIdx;
		if (currentDNS_ == INTENSIFY_NEIGHBORHOOD) {
			double maxConflict = 0;
			int idx = -1;
			for (int i=0;i<s.size();i++) {
				double flict = s.getCar(i).getConflict();
				if (flict > maxConflict) {
					maxConflict = flict;
					idx = i;
				}
			}		
			minIdx = Math.max(0,idx); maxIdx = idx+1; localIdx = 0;
		}
		
		double initCost = s.getCost();
		for (int i=minIdx;i<maxIdx;i++) {
	        moves.addAll(getSwapMoves(s,initCost,i,localIdx));
		}
		
		return moves;
	}
	
	boolean makeMove(
			CarSeqSwapMove move, 
			CarSequence s,
			int currentIteration, 
			boolean force)
	{
	    move.apply(s);
	    
	    if (!force) {
	    	if (solutionTabuHT_.isTabu(s,currentIteration)) {
	    		Log.debug("Iteration:"+currentIteration+" revisited tabu solution:"
	    		//+s
	    		);	    
	    		//increaseTabuTenure(currentIteration);
	    		move.undo(s);
	    		return false;	        	
	    	}	  
	    	else {
	    		if (itSinceTTUpdate_>solutionTabuHT_.getMovingAvg()) {
	    			//Log.debug("Decreasing for Moving avg");
	    			//decreaseTabuTenure(currentIteration);
	    		}
	    	}
	    }
	    
	    CarSeqSwapMove move1 = new CarSeqSwapMove(
	    		move.getToPosition(),
	    		move.getFromPosition(),
	    		move.getCost());

	    moveTabuList_.add(move,currentIteration); // update tabu list	
	    moveTabuList_.add(move1,currentIteration); // update tabu list	
	    
	    solutionTabuHT_.add(s,currentIteration);	
        //Log.debug("Iteration:"+currentIteration+" Made move:"+move);
		
		return true;
	}		

	void increaseTabuTenure(int currentIteration)
	{
    	itSinceTTUpdate_=0;
    	//solutionTabuHT_.increaseTabuTenure(currentIteration);
    	moveTabuList_.increaseTabuTenure(currentIteration);		
	}
	
	void decreaseTabuTenure(int currentIteration)
	{
    	itSinceTTUpdate_=0;
    	//solutionTabuHT_.decreaseTabuTenure(currentIteration);
    	moveTabuList_.decreaseTabuTenure(currentIteration);		
	}	
	
	List getSwapMoves(CarSequence s,
			          double initCost,
			          int pos,
			          int localIdx)
	{
		List retval = new Vector();
				
		Car carA = s.getCar(pos);
		double carAcost = carA.getConflict();
		// Only compute moves for cars with conflict
		if (carAcost == 0.0)
			return retval;
		
		int initPos = Math.min(pos+1,localIdx); 
		for (int i=initPos; i < s.size(); i++) {
		    if (i != pos) {
		        Car carB = s.getCar(i);
		        
		        // Swap positions
		        carA.setSlot(i);
		        carB.setSlot(pos);
		        		        
		        // TODO: keep track of cost incrementally, it'll be much faster
		        double newCost = s.getCost(); 
		        retval.add(new CarSeqSwapMove(pos,i,newCost));
		        
		        // undo swap
		        carA.setSlot(pos);
		        carB.setSlot(i);
		        
		        /*
		        double afterCost = s.getCost();
		        if (initCost != afterCost) {
		        	Log.debug("ERROR, original cost was not restored."+
		        			  " before:"+initCost+
		        			  " after:"+afterCost
		        	);
		        }
		        */
		    }
		}
				
		return retval;
	}
}
