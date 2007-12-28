package org.ut.mh.tsp.grasp;

import java.util.*;
import org.ut.mh.tsp.TSPCity;
import org.ut.mh.tsp.util.Util;

/**
 * A specific Candidate Builder, builds tour one step at a time
 * randomizes selection for each city
 *
 */
public class CandidateBuilder1 
    implements CandidateBuilder 
{
	protected int nSize_; // number of closest cities considered
	                      // at each step when building a candidate

	public CandidateBuilder1(int nSize)
	{
		nSize_=nSize;
	}
	
	/*
	 * The candidate is built one step at a time, for each city, the
	 * set of n closest remaining cities is built and from this set
	 * the next city is picked at random
	 */
	public List getCandidate(List cities)
	{
		List remainingCities = new Vector();
		remainingCities.addAll(cities);

		List candidate = new Vector();
		TSPCity lastCity = null;
		
		while (remainingCities.size()>0) {
			TSPCity currentCity = nextCity(lastCity,remainingCities);
    		candidate.add(currentCity);
    		remainingCities.remove(currentCity);
    		lastCity=currentCity;
		}
		
        return candidate;		
	}
	
	protected TSPCity nextCity(TSPCity lastCity, List remainingCities)
	{
		if (lastCity==null) {
			// Origin is city #1    		
			return (TSPCity)remainingCities.get(0);
		}
		else {
            List nextCities = getNClosestCities(nSize_,lastCity,remainingCities);
			return (TSPCity)nextCities.get(Util.randomPick(nextCities.size()));
		}			
	}
	
	public static List getNClosestCities(int n,TSPCity city,List cities)
	{		
        if (cities.size()<n) {
    		List retval = new Vector();
        	retval.addAll(cities);
        	return retval;
        }
        
       	SortedSet distances = new TreeSet();
       	for (int i=0;i<cities.size();i++) {
       		TSPCity nextCity = (TSPCity)cities.get(i);
       		double distance = TSPCity.distance(city,nextCity);
       		distances.add(new DistanceEntry(nextCity,distance));
       	}

       	//if (distances.size()<n)
       		//Log.debug("ERROR:distances < n");       	

       	List retval=new Vector();
       	Iterator it = distances.iterator();  
       	for(int i=0;i<n && i<distances.size();i++) {
       		DistanceEntry de = (DistanceEntry)it.next();
       		retval.add(de.city);
       	}
       	
	    return retval;	
	}
	
	private static class DistanceEntry
	    implements Comparable
	{		
		public TSPCity city;
		public double distance;
	 
		public DistanceEntry(TSPCity c,double d)
		{
			city=c;
			distance=d;
		}

		public int compareTo(Object obj) 
		{
			DistanceEntry rhs=(DistanceEntry)obj;

			if (distance < rhs.distance)
				return -1;
			if (distance > rhs.distance)
				return 1;
			
			return 0;
		}		
	}
}
