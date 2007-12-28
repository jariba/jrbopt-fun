/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.mh.tsp.ga;

import java.util.*;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.util.*;


/**
 * @author Javier
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GATSPPopulation 
    implements GAPopulation 
{
	protected SortedSet members_;
	protected double totalCost_;
	
	public GATSPPopulation()
	{
		members_ = new TreeSet(new SolutionComparator());
		totalCost_ = 0.0;
	}

	public TSPSolution getBestMember() 
	{
		return (TSPSolution)members_.first();
	}

	// returns true if the new element didn't already exist in the population
	public boolean addMember(TSPSolution m) 
	{
        boolean isNew = members_.add(m);
        if (isNew) {
    	    //m.print("Added new member:");
        	totalCost_+=m.getCost();
        }
        
        return isNew;
	}

	public boolean deleteMember(TSPSolution m) 
	{
		boolean removed = members_.remove(m);
        if (removed) {     		
	        //m.print("Deleted member:");
	        totalCost_ -= m.getCost();
        }
        
        return removed;
	}

	public List getParentCandidates(int cnt) 
	{
		//Log.debug("Population size:"+members_.size());
		List retval = new Vector();
		
		for (int i=0;i<cnt;i++) {
			TSPSolution parent=rouletteWheel(retval);
			//parent.print("Parent"+i+":");
		    retval.add(parent);
		}
		//printFitness();
		return retval;
	}
	
	protected void printFitness()
	{
		Log.debug("Total cost:"+totalCost_);
        Iterator it = members_.iterator();
        double accFitness = 0.0;
        for (int i=1;it.hasNext();i++) {
        	TSPSolution s = (TSPSolution)it.next();
        	accFitness += getFitness(s);
        	Log.debug(i+" : "+getFitness(s)+" : "+accFitness+" : "+s.getCost());
        }        
	}
	
	protected double getFitness(TSPSolution s)
	{
	    return (1.0 - (s.getCost()/totalCost_))/(double)(members_.size()-1);
	}
	
	// Parent is selected with probability proportional to its fitness
	private TSPSolution rouletteWheel(List alreadySelected)
	{
		// TODO: make this more efficient		
		double rnd = Math.random();
		
		TSPSolution retval=null;
		double accFitness=0.0;
        Iterator it = members_.iterator();
        while (it.hasNext()) {
        	TSPSolution s = (TSPSolution)it.next();
        	accFitness += getFitness(s);
        	if (accFitness >= rnd) {
        	    if (alreadySelected.contains(s))
        	    	// Don't return identical parent if we can avoid it
        	    	retval = s;
        	    else
        		    return s;
        	}
        }
        
        Log.debug("returning identical parent");
        return retval;
	}

	public List getDeleteCandidates(int cnt) 
	{
		List retval = new Vector();
		
		for (int i=0;i<cnt;i++) {
			// TODO : remove worst one for now
		    retval.add(members_.last());
		}
		
		return retval;
	}

	static class SolutionComparator
	    implements Comparator
	{
		public int compare(Object a,Object b) 
		{
			TSPSolution lhs=(TSPSolution)a;
			TSPSolution rhs=(TSPSolution)b;
			
			return new Double(lhs.getCost()-rhs.getCost()).compareTo(new Double(0));
		}
	}
}
