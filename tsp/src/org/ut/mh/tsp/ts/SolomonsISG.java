/*
 * Created on Apr 12, 2005
 *
 */
package org.ut.mh.tsp.ts;

import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import org.ut.mh.tsp.TSPCity;
import org.ut.mh.tsp.TSPSolution;

/**
 * @author Javier
 *
 * Implementation of Solomon's heuristic
 * Puslished in "Operations Research" 
 * Vol. 35 No. 2 pp. 254-265
 * This is the Insertion Heuristic called I1 in the paper
 */
public class SolomonsISG
    implements InitialSolutionGenerator
{
	/*
	 */
	// parameters
	double u = 1;
	double alpha1 = 1;
	double alpha2 = 0;
	double lambda = 1;
	TSPCity depot_=null;

	public TSPSolution getInitialSolution(List cities)
	{
		TSPSolution s = new TSPSolution();
		
		// Unrouted customers are sorted by distance to depot
        depot_ = (TSPCity)cities.get(0);
		SortedSet unroutedCities = sortUnroutedCities(cities);
		
		while (unroutedCities.size()>0) {
		    List newTour = new Vector();
		    // start with farthest unrouted customer
		    CityEntry e = (CityEntry)unroutedCities.first();
		    unroutedCities.remove(e);
		    //Log.debug("Dist to Depot:"+e.distToDepot);
		    newTour.add(e.city);
		    while (unroutedCities.size()>0) {
		    	e = getNextCityToInsert(newTour,unroutedCities);
		    	if (e!=null) {
		    		//Log.debug("Inserted city "+e.city.id+" in pos "+e.pos);
		    		newTour.add(e.pos,e.city);
		    		unroutedCities.remove(e);
		    	}
		    	else {
		            // There are no feasible inserts in new tour
		    	    break;		    		
		    	}
		    }
		    
		    newTour.add(0,depot_); // add depot as first city in every tou
		    //printTour(newTour);
		    s.addTour(newTour);
		}
		
		return s;
	}
	
	CityEntry getNextCityToInsert(List tour,SortedSet unroutedCities)
	{
		CityEntry bestCity=null;
		double bestCityCost=Double.MAX_VALUE;
		
	    Iterator it = unroutedCities.iterator();
	    while (it.hasNext()) {
	    	CityEntry e = (CityEntry)it.next();
	    	int bestPos = -1;
	    	double bestCost = Double.MAX_VALUE;
	    	for (int i=0;i<=tour.size();i++) {
	    		// see if city can be inserted before pos i
	    		if (isFeasibleInsert(e,i,tour)) {
	    			double cost = getInsertCost(e,i,tour);
	    			if (cost < bestCost) {
	    				bestCost = cost;
	    				bestPos = i;
	    			}
	    		} 		
	    	}
	    	
	    	if (bestPos != -1) {
	    		// We found a best feasible insert
	    		if (bestCost < bestCityCost) {
	    			bestCity=e;
	    			bestCity.pos=bestPos;
	    			bestCityCost=bestCost;
	    		}
	    	}
	    }
	    
		return bestCity;
	}
	
	boolean isFeasibleInsert(CityEntry e,int pos,List tour)
	{
		TSPCity before = (pos>0 ? (TSPCity)tour.get(pos-1) : depot_);
       
		double t = getElapsedTime(before,tour);
		t += before.distance(e.city);
		if (t > e.city.maxTime.intValue()) {
		    return false;	
		}
		t = Math.max(t,e.city.minTime.intValue());
		
		TSPCity lastCity = e.city;
		for (int i=pos; i<tour.size();i++) {
			TSPCity nextCity = (TSPCity)tour.get(i);
			t += lastCity.distance(nextCity);
			if (t > nextCity.maxTime.intValue())
				return false;
			lastCity = nextCity;
			t = Math.max(t,lastCity.minTime.intValue());
		}
					
		return true;
	}
	
	double getElapsedTime(TSPCity c, List tour)
	{
		// TODO: this info must be cached somehow
		if (c==depot_)
			return 0;
		
		TSPCity lastCity=depot_;
		double t = 0;
		for (int i=0;i<tour.size();i++) {
			TSPCity next = (TSPCity)tour.get(i);
			t += lastCity.distance(next);
			t = Math.max(t,next.minTime.intValue());
			lastCity = next;
			if (c==lastCity)
				return t;
		}
		
		throw new RuntimeException("Should've found city in Tour computing getElapsedTime");
	}
	
	double getInsertCost(CityEntry e,int pos,List tour)
	{
		TSPCity before = (pos>0 ? (TSPCity)tour.get(pos-1) : depot_);
		TSPCity after = (pos < tour.size() ? (TSPCity)tour.get(pos) : depot_);

		double distanceChange = 
			   before.distance(e.city) +
		       e.city.distance(after) -
			   before.distance(after);
		
		double origServiceTime=getElapsedTime(after,tour);
		double newServiceTime=origServiceTime+
		                      before.distance(e.city);
		newServiceTime = Math.max(newServiceTime,e.city.minTime.intValue());
		newServiceTime += e.city.distance(after);
		newServiceTime = Math.max(newServiceTime,after.minTime.intValue());
		
		double serviceTimeChange = newServiceTime-origServiceTime;
		
		double c1 = (alpha1*distanceChange) + (alpha2*serviceTimeChange);			   
		
		double cost = (lambda*depot_.distance(e.city)) - c1;
		
		return cost;
	}
	
	SortedSet sortUnroutedCities(List cities)
	{
        SortedSet s = new TreeSet();
        // First city is the depot
        for (int i=1;i<cities.size();i++) {
        	CityEntry e = new CityEntry(depot_,(TSPCity)cities.get(i));
		    //Log.debug(e.city.id+" Dist to Depot:"+e.distToDepot);
        	s.add(e);
        }
        
        return s;
	}
	
	private static class CityEntry
	    implements Comparable
	{
		public TSPCity city;
		public double distToDepot; // distance to depot
		public int pos; // Position to insert in tour
		
		public CityEntry(TSPCity depot,TSPCity c)
		{
			city = c;

			// smal disruption to avoid collisions
			double delta=Math.random()*1e-6;
			distToDepot = depot.distance(c)+delta;
		}

		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object o) 
		{
			if (this == o)
				return 0;
			
			CityEntry rhs=(CityEntry)o;
			if (distToDepot > rhs.distToDepot)
				return -1;
			else if (distToDepot < rhs.distToDepot)
				return 1;
			else 
				return 0;
		}
		
		public boolean equals(Object o)
		{
			CityEntry rhs=(CityEntry)o;
            return city == rhs.city; 			
		}
	}
}
