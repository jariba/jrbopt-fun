package org.ut.carseq;

import java.util.*;
import org.ut.util.Log;

public class SolutionTabuHashTable 
{
	protected int MAX_TABU_TENURE;
	protected int MIN_TABU_TENURE;	
	protected int tabuTenure_;  // number of iterations before we go back to a previously visited solution 
	protected double hashValues_[];
    protected Map visitedSolutions_;
    protected Set costValues_;
	double incFactor_;
	double decFactor_;
	double movingAvg_;
	
	public SolutionTabuHashTable(int tabuTenure, double incFactor,double decFactor)
	{
	    tabuTenure_ = tabuTenure;	
        incFactor_ = incFactor;
        decFactor_ = decFactor;
    	MIN_TABU_TENURE=tabuTenure_;
    	MAX_TABU_TENURE=2000;
    	movingAvg_=50;
	}

	public void reset(int solutionSize)
	{
	    hashValues_ = new double[solutionSize];
	    for (int i=0;i<hashValues_.length;i++)
	    	hashValues_[i]=Math.random();
	    
	    costValues_ = new TreeSet();
	    visitedSolutions_ = new HashMap();
	}
	
	public void increaseTabuTenure(int currentIteration)
	{
		if (tabuTenure_==MAX_TABU_TENURE)
			return;
		
		tabuTenure_ = Math.min(MAX_TABU_TENURE,(int)(tabuTenure_*incFactor_));
		Log.debug("Iteration:"+currentIteration+" increased Solution tabu tenure to:"+tabuTenure_);
	}

	public void decreaseTabuTenure(int currentIteration)
	{
		if (tabuTenure_==MIN_TABU_TENURE)
			return;
		
		tabuTenure_ = Math.max(MIN_TABU_TENURE,(int)(tabuTenure_*decFactor_));
		Log.debug("Iteration:"+currentIteration+" decreased Solution tabu tenure to:"+tabuTenure_);
	}
	
	public void setTabuTenure(int t) { tabuTenure_=t; }

    public boolean isTabu(CarSequence s,int currentIteration)
    {
    	if (costValues_.contains(new Double(s.getCost()))) {
    	    Integer iteration = (Integer)visitedSolutions_.get(getHashKey(s));
    	    if (iteration==null)
    	        return false;
    	    
    	    int it = iteration.intValue();
    	    if (currentIteration <= it+tabuTenure_) {
    	    	movingAvg_ = (0.1*(currentIteration-it))+(0.9*movingAvg_);
    	    	Log.debug("Moving Avg is:"+movingAvg_);
    		    return true;
    	    }
    	}
    	
    	return false;
    }
    
    public double getMovingAvg() { return movingAvg_; }
    
    public void add(CarSequence s,int iteration)
    {
    	costValues_.add(new Double(s.getCost()));
    	visitedSolutions_.put(getHashKey(s),new Integer(iteration));    	
    }
    
    protected Object getHashKey(CarSequence s)
    {
    	return new Double(getTHV(s));
    }
    
	double getTHV(CarSequence s)
	{
		double thv = 0;
		int i = 0;
		for (Car c : s.getCars()) {
			thv += hashValues_[i]*(double)c.getID();
			i++;
		}
		
		return thv;
	}	
}
