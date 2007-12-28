package org.ut.mh.tsp;

import java.util.*;
import org.ut.mh.tsp.util.*;

/**
 * Stores a solution for the 
 */
public class TSPSolution 
{	
	protected List tours_;
	// Hack to support display for now
	public int totalLateness=0;
	public int latenessCnt=0;
	
	public TSPSolution()
	{
		tours_ = new Vector();
	}
	
	public void addTour(List l) { tours_.add(l); }
	public List getTour(int i)  { return (List)tours_.get(i); }
	
	public int getTourCnt() { return tours_.size(); }
	
	public void print(String msg)
	{
		Log.debug(msg+" "+toString());
	}
	
	
	public double getCost()
	{
	    double cost=0.0;
	    for (int i=0;i<tours_.size();i++) {
	    	cost += getCost(getTour(i));
	    }
	    
	    return cost;
	}
	
	public static double getCost(List cities)
	{
		double cost =0.0;
		TSPCity lastCity = null;
		for (int i=0;i<cities.size();i++) {
			TSPCity city = (TSPCity)cities.get(i);
			cost += TSPCity.distance(lastCity,city);    
			lastCity = city;    
		}	    

        // it's a tour, so add the arc from last to first
        if (cities.size()>1)
            cost += TSPCity.distance(
                (TSPCity)cities.get(0),
                (TSPCity)cities.get(cities.size()-1)
            );

		return cost;
	}		
	
	public TSPSolution makeCopy()
	{
		TSPSolution copy = new TSPSolution();
	    for (int i=0;i<tours_.size();i++) {
	    	List newTour = new Vector();
			newTour.addAll(getTour(i));
	    	copy.addTour(newTour);
	    }
		
		return copy;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getCost()).append(" ");
		for (int i=0;i<tours_.size();i++ ) {
			List cities = getTour(i);
			if (tours_.size()>1)
				buf.append("\n    ");
			buf.append("Path "+(i+1)+":");
		    for (int j=0;j<cities.size();j++) {
		    	TSPCity city = (TSPCity)cities.get(j);
		    	buf.append(city.id);
		    	if (j<cities.size()-1)
		    		buf.append("-");
		    }	    
		}
		
		return buf.toString();
	}
}
