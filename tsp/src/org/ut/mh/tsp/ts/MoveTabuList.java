/*
 * Created on Apr 12, 2005
 *
 */
package org.ut.mh.tsp.ts;

import java.util.*;
import org.ut.mh.tsp.TSPCity;
import org.ut.mh.tsp.util.Log;

/**
 * @author Javier
 */
public class MoveTabuList 
{
	protected int MAX_TABU_TENURE;
	protected int MIN_TABU_TENURE;	
	protected int tabuTenure_;
	protected double incFactor_;
	protected double decFactor_;
	protected int [][] moveTabuList_;
	
	public MoveTabuList(int size,int tabuTenure, double incFactor,double decFactor)
	{
		moveTabuList_ = new int[size][size];
        tabuTenure_ = tabuTenure;
        incFactor_ = incFactor;
        decFactor_ = decFactor;
    	MIN_TABU_TENURE=tabuTenure_;
    	MAX_TABU_TENURE=2000;
	}
	
	public void reset(int size)
	{
		moveTabuList_ = new int[size][size];
		for (int i=0;i<size;i++)		
			for (int j=0;j<size;j++)
				moveTabuList_[i][j]=-1;
	}
	
	public void add(TSPTWMove move, int currentIteration)
	{
	    moveTabuList_[getX(move)][getY(move)] = currentIteration;		
	}

	// checks for a city being moved directly to a repeated position
	boolean isTabu(TSPTWMove move, int currentIteration)
	{
    	if (currentIteration <= moveTabuList_[getX(move)][getY(move)]+tabuTenure_)
	    	return true;
    	
    	return false;
	}

	public void increaseTabuTenure(int currentIteration)
	{
		if (tabuTenure_==MAX_TABU_TENURE)
			return;
		
		tabuTenure_ = Math.min(MAX_TABU_TENURE,(int)(tabuTenure_*incFactor_));
		Log.debug("Iteration:"+currentIteration+" increased Move tabu tenure to:"+tabuTenure_);
	}

	public void decreaseTabuTenure(int currentIteration)
	{
		if (tabuTenure_==MIN_TABU_TENURE)
			return;
		
		tabuTenure_ = Math.max(MIN_TABU_TENURE,(int)(tabuTenure_*decFactor_));
		Log.debug("Iteration:"+currentIteration+" decreased Move tabu tenure to:"+tabuTenure_);
	}
	
	protected int getX(TSPTWMove move)
	{
		return getX(move.getCity());
	}
	
	protected int getX(TSPCity c) 
	{
		return c.id.intValue()-1;
	}
	
	protected int getY(TSPTWMove move)
	{
		return getY(move.getToPosition());
	}
	
	protected int getY(int pos)
	{
		return pos;
	}
}
