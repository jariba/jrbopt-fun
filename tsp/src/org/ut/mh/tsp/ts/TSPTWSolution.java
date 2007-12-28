/*
 * Created on Apr 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.mh.tsp.ts;

import java.util.*;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.TSPCity;
import org.ut.mh.tsp.util.Log;

/**
 * @author Javier
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TSPTWSolution 
{
	protected List nodes_;
	protected TSPCity depot_;
	protected double latenessPenalty_;
	protected int latenessHardThreshold_; 
	protected int maxLatenessCount_; 
	
	// This is to support copy()
    protected TSPTWSolution(TSPTWSolution orig)
    {
    	depot_ = orig.depot_;
    	latenessPenalty_ = orig.latenessPenalty_;
    	latenessHardThreshold_ = orig.latenessHardThreshold_;
    	maxLatenessCount_ = orig.maxLatenessCount_;
        nodes_= new Vector();
       	nodes_.addAll(orig.nodes_);
    }
    
    public TSPTWSolution(TSPCity depot,
    		             TSPSolution s,
						 double latenessPenalty,
						 int latenessHardThreshold,
						 int maxLatenessCount,
						 int extraAgents)
    {
    	depot_ = depot;
    	latenessPenalty_ = latenessPenalty;
    	latenessHardThreshold_ = latenessHardThreshold;
    	maxLatenessCount_ = maxLatenessCount;
        nodes_= new Vector();
        for (int i=0;i<s.getTourCnt();i++) {
        	List tour=(List)s.getTour(i);
        	nodes_.addAll(tour);
        }
        
        // add more agent nodes at the end
        for (int i=0;i<extraAgents;i++)
        	nodes_.add(depot_);
    }
    
    public TSPSolution toTSPSolution()
    {
    	TSPSolution s = new TSPSolution();
    	
    	List tour=new Vector();
    	for (int i=0;i<nodes_.size();i++) {
            TSPCity c = (TSPCity)nodes_.get(i);
            if (c==depot_) {
            	if (tour.size()>1)
            		s.addTour(tour);
            	tour = new Vector();
            }
            tour.add(c);
    	}
    	
    	if (tour.size()>1)
    		s.addTour(tour);
    	
    	return s;
    }
    
    public double getCost()
    {
    	double travelTime = TSPSolution.getCost(nodes_);
    	
    	// Don't penalize wait time separately?
    	// by minimizing travel time, wait time is minimized too?
    	
    	// add lateness penalty
    	double totLatenessPenalty=0;
		double t=0;
		int latenessCount=0;
		TSPCity lastCity = depot_;
		for (int i=0;i<nodes_.size();i++) {
			TSPCity c = (TSPCity)nodes_.get(i);
			if (lastCity==depot_)
				t=0;
			t += lastCity.distance(c);
			double wait = Math.max(0,c.minTime.intValue()-t);
			double lateness = Math.max(0,t-c.maxTime.intValue());
			if (lateness > latenessHardThreshold_)
				return Double.MAX_VALUE;
			
		    if (lateness > 0) {
		    	latenessCount++;
		    	if (latenessCount > maxLatenessCount_)
		    		return Double.MAX_VALUE;
		    }
			
			t = Math.max(t,c.minTime.intValue());			
			//Log.debug(c+" "+" "+(int)t+" "+(int)wait+" "+(int)lateness+" "+lastCity.distance(c));
			lastCity=c;
			
			totLatenessPenalty += lateness*latenessPenalty_;
		}			
    	
    	return travelTime+totLatenessPenalty;
    }
    
    public TSPTWSolution copy()
    {
    	return new TSPTWSolution(this);
    }
    
    public String toString()
    {
    	StringBuffer buf = new StringBuffer();
    	
    	for (int i=0;i<nodes_.size();i++) {
    		TSPCity c = (TSPCity) nodes_.get(i);
    		buf.append(c.id).append("-");
    	}
    	return buf.toString();
    	
    }
    
    protected List getNodes() { return nodes_; }       
}
