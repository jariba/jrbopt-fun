/*
 * Created on Apr 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.carseq;

import java.util.*;
import org.ut.util.Log;
import org.ut.util.Util;


/**
 * @author Javier
 *
 */
public class CarSeqSwapMove 
    implements CarSeqMove, Comparable
{
    protected int fromPos_;
    protected int toPos_;
    protected double cost_;
    
    public CarSeqSwapMove(int fromPos, 
    		             int toPos,
    		             double cost)
    {
    	fromPos_ = fromPos;
    	toPos_ = toPos;
    	cost_ = cost;
    }
    
    public int getFromPosition() { return fromPos_; }
    public int getToPosition() { return toPos_; }
    public double getCost() { return cost_; }
    
    public void apply(CarSequence s)
    {
    	s.swapCars(toPos_, fromPos_);
    	
    	/*
    	if (cost_!=s.getCost()) {
    		Log.debug("ERROR applying swap move. orig cost:"+cost_+" new cost:"+s.getCost());
    	}
    	*/
        //Log.debug("Applied Swap Move from:"+fromPos_+" to:"+toPos_+" "+s.getCost());
    }

    public void undo(CarSequence s)
    {
    	s.swapCars(toPos_, fromPos_);
        //Log.debug("Reversed Swap Move from:"+fromPos_+" to:"+toPos_+" "+s.getCost());
    }
    
	public int compareTo(Object obj) 
	{
		CarSeqSwapMove rhs = (CarSeqSwapMove)obj;

		if (getCost() < rhs.getCost())
			return -1;
		else if (getCost() > rhs.getCost())
			return 1;
		else 
			return 0;
	}
	
	public boolean equals(Object obj)
	{
		CarSeqSwapMove rhs = (CarSeqSwapMove)obj;
        return this == rhs; 			
	}
	
	public String toString()
	{
		return "Swap from:"+fromPos_+" to:"+toPos_+" cost:"+cost_;
	}
}
