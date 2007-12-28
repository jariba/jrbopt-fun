/*
 * Created on Apr 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.mh.tsp.ts;

import java.util.*;
import org.ut.mh.tsp.TSPCity;
import org.ut.mh.tsp.util.Log;


/**
 * @author Javier
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TSPTWInsertMove 
    implements TSPTWMove, Comparable
{
    protected TSPCity city_;
    protected int fromPos_;
    protected int toPos_;
    protected double cost_;
    
    public TSPTWInsertMove(TSPCity city, 
    		               int fromPos, 
    		               int toPos, 
						   double cost)
    {
    	city_ = city;
    	fromPos_ = fromPos;
    	toPos_ = toPos;
    	cost_ = cost;
    }
    
    public TSPCity getCity() { return city_; }
    public int getFromPosition() { return fromPos_; }
    public int getToPosition() { return toPos_; }
    public double getCost() { return cost_; }
    
    public void apply(TSPTWSolution s)
    {
    	List nodes = s.getNodes();
    	TSPCity c = (TSPCity)nodes.get(fromPos_);
    	if (c != city_)
    		Log.debug("ERROR!: making insert move, expected city "+city_.id+" found city:"+c.id);
    	
        nodes.remove(fromPos_);
        nodes.add(toPos_,city_);
        //Log.debug("Applied Move: city-"+city_.id+" from:"+fromPos_+" to:"+toPos_+" "+s.getCost());
    }
    
    public void undo(TSPTWSolution s)
    {
    	List nodes = s.getNodes();
    	TSPCity c = (TSPCity)nodes.get(toPos_);

        nodes.remove(toPos_);
        nodes.add(fromPos_,city_);
        //Log.debug("Reversed Move: city-"+city_.id+" from:"+fromPos_+" to:"+toPos_+" "+s.getCost());    	
    }
    

	public int compareTo(Object obj) 
	{
		TSPTWInsertMove rhs = (TSPTWInsertMove)obj;

		if (getCost() < rhs.getCost())
			return -1;
		else if (getCost() > rhs.getCost())
			return 1;
		else 
			return 0;
	}
	
	public boolean equals(Object obj)
	{
		TSPTWInsertMove rhs = (TSPTWInsertMove)obj;
        return this == rhs; 			
	}
	
	public String toString()
	{
		return "Insert city-"+city_.id+" from:"+fromPos_+" to:"+toPos_+" cost:"+cost_;
	}
}
