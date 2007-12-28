package org.ut.mh.tsp.ga;

import java.util.*;
import org.ut.mh.tsp.TSPSolution;
import org.ut.mh.tsp.TSPCity;
//import org.ut.mh.tsp.util.Util;
import org.ut.mh.tsp.grasp.CandidateBuilder1;
import org.ut.mh.tsp.grasp.LSOneSwap;
import org.ut.mh.tsp.sa.LSRandomNSwaps;
//import org.ut.mh.tsp.util.Log;

/**
 */
public class GAProblemMapper_1TSP_JB1 
    implements GAProblemMapper 
{
    protected int popSize_;
    protected double mutationRate_;
    
    public GAProblemMapper_1TSP_JB1(int popSize,
    		                        double mr)
    {
        popSize_=popSize;    	
        mutationRate_=mr;
    }
	
	public GAPopulation getInitialPopulation(List cities) 
	{
        GAPopulation population = new GATSPPopulation();
        
		CandidateBuilder1 cb = new CandidateBuilder1(1);
		TSPSolution solution = new TSPSolution();
		solution.addTour(cb.getCandidate(cities));
        population.addMember(solution);
        
        for (int i=1;i<popSize_;i++) {
    		TSPSolution s = solution.makeCopy();
            LSRandomNSwaps ls = new LSRandomNSwaps(10);
            s = ls.doSwaps(s);
            LSOneSwap ls1 = new LSOneSwap();
            s = ls1.doLocalSearch(s);
            population.addMember(s);
        }
        
		return population;
	}

	public TSPSolution makeNewMember(GAPopulation population) 
	{
		double rnd = Math.random();
		
		if (rnd <= mutationRate_)
	   	    return mutation(population);
		else 
     	    return grefenstetteCrossover(population);
	}

	protected TSPSolution mutation(GAPopulation population)
	{
		//Log.debug("Performed Mutation");
		List parents = population.getParentCandidates(1);
		TSPSolution parent = (TSPSolution)parents.get(0);
		TSPSolution newMember = parent.makeCopy();
        LSRandomNSwaps ls = new LSRandomNSwaps(10);
        newMember = ls.doSwaps(newMember);
        
		return newMember;
	}
	
	protected TSPSolution grefenstetteCrossover(GAPopulation population)
	{
		TSPSolution newMember=new TSPSolution();

		List parents = population.getParentCandidates(2);
		List a = new Vector();
		a.addAll(((TSPSolution)parents.get(0)).getTour(0));
		List b = new Vector();
		b.addAll(((TSPSolution)parents.get(1)).getTour(0));

		List newTour = new Vector();
		// for 1-tsp all tours start at city0
		TSPCity currentCity=null;
		for (int i=0;i<a.size();i++) {
			TSPCity nextCity=null;
			if (i==0) {
				// for 1-tsp all tours start at city0
				nextCity=(TSPCity)a.get(0);
			}
			else {
				TSPCity aNextCity=getNextCity(a,currentCity);
				TSPCity bNextCity=getNextCity(b,currentCity);
				double aDist = TSPCity.distance(aNextCity,currentCity);
				double bDist = TSPCity.distance(bNextCity,currentCity);
				if (aDist < bDist)
					nextCity=aNextCity;
				else
					nextCity=bNextCity;
			}
			if (newTour.contains(nextCity)) {
				for (int j=0;j<a.size();j++) {
					if (!newTour.contains(a.get(j))) {
						nextCity=(TSPCity)a.get(j);
						break;
					}
				}
			}
			newTour.add(nextCity);
			currentCity=nextCity;
		}
		
		newMember.addTour(newTour);		
        LSOneSwap ls1 = new LSOneSwap();
        //newMember = ls1.doLocalSearch(newMember);
 
		//newMember.print("Child:");        
		return newMember;
	}
	
	protected TSPCity getNextCity(List tour,TSPCity city)
	{
		for (int i=0;i<tour.size();i++) {
			if (tour.get(i).equals(city)) {
				if (i==tour.size()-1)
					return (TSPCity)tour.get(0);
				else
					return (TSPCity)tour.get(i+1);
			}
				
		}
		
		return null;
	}
	
	public void deleteMember(GAPopulation population) 
	{
		// Delete the worst one for now
		// TODO: make this more intelligent?
		List l = population.getDeleteCandidates(1);
		
		if (l.size()>0)
			population.deleteMember((TSPSolution)l.get(0));
	}

}
