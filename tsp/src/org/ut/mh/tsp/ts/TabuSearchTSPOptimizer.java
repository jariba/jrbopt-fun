package org.ut.mh.tsp.ts;

import java.util.*;
import org.ut.mh.tsp.OptimizerBase;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.util.Log;
import org.ut.mh.tsp.util.Util;
import org.ut.mh.tsp.TSPCity;
//import org.ut.mh.tsp.grasp.CandidateBuilder1;

/**
 * Optimizer for the TSP problem using SA
 *
 */
public class TabuSearchTSPOptimizer 
    extends OptimizerBase
{
	protected int maxFailures_;            // max number of consecutive failures before we quit
	protected double latenessPenalty_;     // Penalty applied per unit of lateness
	protected int latenessHardThreshold_;  // Don't allow lateness greater than this
	protected int maxLatenessCount_;       // Don't allow more than this number of late arrivals 
	protected int extraAgents_=5;          // Number of extra traveling salesmen (or vehicles in the homework) 
 	                                       // to add to initial heuristic solution

	// Tabu data structures
	protected MoveTabuList moveTabuList_;
	int initSolutionTabuTenure_;
    protected SolutionTabuHashTable solutionTabuHT_;
	
	// Dynamic neighborhood selection
	protected int currentDNS_;
	protected int dnsCnt_;
	protected int maxDNSCnt_;
	protected int dnsThreshold_;           // number of failures to switch from insert negihborhood to
	                                       // swap neighborhood
	protected int itSinceTTUpdate_;  // Iterations since tabu tenure update 

	protected TSPCity depot_=null;
	
	protected final int INSERT_NEIGHBORHOOD=0;
	protected final int SWAP_NEIGHBORHOOD=1;
	
	public TabuSearchTSPOptimizer(String name,
			              int moveTabuTenure,
						  int solutionTabuTenure,
						  double latenessPenalty,
						  int latenessHardThreshold,
						  int maxLatenessCount,
			              int maxFailures)
	{
		super(name);
		moveTabuList_ = new MoveTabuList(0,moveTabuTenure,1.2,0.9);
		initSolutionTabuTenure_=solutionTabuTenure;
		solutionTabuHT_ = new SolutionTabuHashTable(solutionTabuTenure,1.2,0.9);
		latenessPenalty_ = latenessPenalty;
		latenessHardThreshold_ = latenessHardThreshold;
        maxLatenessCount_ = maxLatenessCount;
        maxFailures_ = maxFailures;
        dnsThreshold_ = 200;    
        maxDNSCnt_ = 200;
        itSinceTTUpdate_=0;
	}
	
	protected boolean isLatenessPenalized(List cities)
	{
		return !(latenessPenalty_==0 && 
				  latenessHardThreshold_ >=1000 && 
			 	  maxLatenessCount_>=cities.size());
	}
	
	protected TSPSolution getInitialSolution(List cities)
	{
		TSPSolution initSolution=null;
		
		InitialSolutionGenerator isg = new SolomonsISG();
		initSolution = isg.getInitialSolution(cities);

		/*
		if (isLatenessPenalized(cities)) {
			InitialSolutionGenerator isg = new SolomonsISG();
			initSolution = isg.getInitialSolution(cities);
		}
        else {
        	CandidateBuilder1 cb = new CandidateBuilder1(1);
			initSolution = new TSPSolution();
			initSolution.addTour(cb.getCandidate(cities));        
		}
		*/
		
		return initSolution;
	}
	
	public TSPSolution solve(List cities)
	{
		resetBestSolution();
		
		//Util.printList("Solving TSP for:",cities,"\n");
		TSPSolution initSolution=getInitialSolution(cities);
		
		
		// Move from multi-tour representation to single-tour
		// where agents (vehicles) are represented by depot nodes		
        depot_ = (TSPCity)cities.get(0);
	    TSPTWSolution bestSolution = new TSPTWSolution(
	    		depot_,
				initSolution,
				latenessPenalty_,
				latenessHardThreshold_,
				maxLatenessCount_,
				extraAgents_
		);

	    int solSize = bestSolution.getNodes().size();
		moveTabuList_.reset(solSize);
		solutionTabuHT_.reset(solSize);
	    		
		double bestCost = bestSolution.getCost();
    	notifyNewSolution(0,bestSolution.toTSPSolution());
				
	    TSPTWSolution currentSolution = bestSolution;
		int failures=0;		
		for (int i=0;failures<maxFailures_;i++) {
		    TSPTWSolution nextSolution = getNextSolution(currentSolution,i,bestCost,failures);

		    double nextCost = nextSolution.getCost();
			if (nextCost<bestCost) {
    	    	bestSolution = nextSolution;
		    	bestCost = nextCost;
				notifyNewSolution(i+1,bestSolution.toTSPSolution());
				failures=0;
		    }
		    else {
		    	failures++;
		    }		
			currentSolution=nextSolution;
   			notifyIterationCompleted(i);
		}
					
		logBestSolution();		
        Log.debug("BestSolution has:"+bestSolution.getNodes().size()+" nodes");
        Log.debug("TabuSearchTSPOptimizer done");
		return bestSolution.toTSPSolution();
	}
	
	void setCurrentNeighborhood(int n,int currentIteration)
	{
		if (currentDNS_!=n) {
			currentDNS_ = n;		
    		String name = (currentDNS_ == INSERT_NEIGHBORHOOD ? "insert" : "swap");
		    Log.debug("Iteration:"+currentIteration+" switching to "+name+" neighborhood");
		}
	}
	
	TSPTWSolution getNextSolution(TSPTWSolution currentSolution,
			                      int currentIteration,
			                      double bestCost,
								  int failures)
	{
		// Dynamic neighborhood selection 
	    if (failures < dnsThreshold_) {
	    	// Set the main neighborhood
	    	setCurrentNeighborhood(INSERT_NEIGHBORHOOD,currentIteration);
	    }
	    else {
	    	// If we get stuck, alternate between the neighborhoods until we find an improving move
	    	if (dnsCnt_==maxDNSCnt_) {
	    		setCurrentNeighborhood(currentDNS_ == INSERT_NEIGHBORHOOD ? SWAP_NEIGHBORHOOD : INSERT_NEIGHBORHOOD,
	    				 currentIteration);
	    	}
    		dnsCnt_ = ((dnsCnt_-1) < 0 ? maxDNSCnt_ : dnsCnt_-1);
	    }
	    
		TSPTWSolution s = currentSolution.copy();		
	    SortedSet moves = getMoves(s,currentDNS_);    
	    TSPTWMove bestMove = null;
	    Iterator it = moves.iterator();	    
	    while (it.hasNext()) {
	    	TSPTWMove move = (TSPTWMove)it.next();
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
	    	bestMove = (TSPTWMove)moves.first();
		    makeMove(bestMove,s,currentIteration,true); 
			Log.debug("Iteration:"+currentIteration+" Forced to make tabu move "+bestMove);
	    	decreaseTabuTenure(currentIteration);
	    }	    

    	itSinceTTUpdate_++;	    
	    return s;
	}
	
	boolean makeMove(TSPTWMove move, TSPTWSolution s,int currentIteration, boolean force)
	{
	    move.apply(s);
	    
	    if (!force) {
	    	if (solutionTabuHT_.isTabu(s,currentIteration)) {
	    		//Log.debug("Iteration:"+currentIteration+" revisited tabu solution:"+s);	    
	    		increaseTabuTenure(currentIteration);
	    		move.undo(s);
	    		return false;	        	
	    	}	  
	    	else {
	    		if (itSinceTTUpdate_>solutionTabuHT_.getMovingAvg()) {
	    			Log.debug("Decreasing for Moving avg");
	    			decreaseTabuTenure(currentIteration);
	    		}
	    	}
	    }
	    
	    moveTabuList_.add(move,currentIteration); // update tabu list		
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
	
	// TODO: abstract type out into separate classes
	SortedSet getMoves(TSPTWSolution s, int type)
	{
		SortedSet moves = new TreeSet();
		
		List nodes = s.getNodes();
		for (int i=1;i<nodes.size();i++) {
			if (type==this.INSERT_NEIGHBORHOOD)
			    moves.addAll(getNodeInsertMoves(s,i));
			else
			    moves.addAll(getNodeSwapMoves(s,i));
		}
		
		return moves;
	}
	
	List getNodeInsertMoves(TSPTWSolution s,int position)
	{
		// TODO: Implement restrictions to limit search
		List moves = new Vector();
		List nodes = s.getNodes();
		TSPCity city = (TSPCity)nodes.get(position);
		nodes.remove(position); // take city out to evaluate moves
        
		// evaluate later moves
		int newRoutes=0;
		for (int pos=position+1;pos<=nodes.size();pos++) {
        	TSPCity prev = (TSPCity)nodes.get(pos-1);
			TSPCity next = (pos<nodes.size() ? (TSPCity)nodes.get(pos) : depot_);

			if (city == depot_) {
            	// a "vehicle" node can't cross over another "vehicle" node
               	if (prev == depot_)
                		break;
            }
            else {
            	// All "vehicle" nodes are at the end of the tour,
            	// so trying more than one new route is redundant
                if (prev == depot_ && next == depot_) {
                	newRoutes++;
                	if (newRoutes > 1)
                		break;
                }
            }
			
			nodes.add(pos,city); // insert city into solution to eval move value

			moves.add(
			    new TSPTWInsertMove(
					city,
					position,  // old position
					pos,       // new position
					s.getCost()
			    )
			);
			
			nodes.remove(pos); // undo insert
		}
		
		// evaluate earlier moves
		for (int pos=position-1;pos>0;pos--) {			
        	TSPCity prev = (pos > 0 ? (TSPCity)nodes.get(pos-1) : depot_);
			TSPCity next = (TSPCity)nodes.get(pos);

			if (city == depot_) {
            	// a "vehicle" node can't cross over another "vehicle" node
				// or be inserted to the right
               	if (next == depot_)
                		break;
            }
			
			nodes.add(pos,city); // insert city into solution to eval move value

			moves.add(
			    new TSPTWInsertMove(
					city,
					position,  // old position
					pos,       // new position
					s.getCost()
			    )
			);
			
			nodes.remove(pos); // undo insert
		}
		
		nodes.add(position,city); // put city back after evaluating moves
		return moves;
	}

	List getNodeSwapMoves(TSPTWSolution s,int position)
	{
		// TODO: Implement restrictions to limit search
		List moves = new Vector();
		List nodes = s.getNodes();
		TSPCity city = (TSPCity)nodes.get(position);
		
		for (int pos=position+2;pos<nodes.size();pos++) {
			TSPCity swapCity = (TSPCity)nodes.get(pos);			
			if (city==depot_ && swapCity==depot_) // don't swap 2 agent nodes
				continue;
			
			Util.swap(nodes,position,pos); // do swap

			moves.add(
			    new TSPTWSwapMove(
					city,
					position,  // old position
					pos,       // new position
					s.getCost()
			    )
			);
			
			Util.swap(nodes,pos,position); // undo swap
		}
		
		return moves;
	}		
}
