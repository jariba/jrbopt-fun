/*
 * Created on Apr 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.mh.tsp.ts;

import org.ut.mh.tsp.TSPCity;

/**
 * @author Javier
 *
 * TODO: this only models an insert move for now, generalize later
 * to be able to represent any kind of move
 */
public interface TSPTWMove 
{
    public TSPCity getCity();
    public int getFromPosition();
    public int getToPosition();
    public double getCost(); // New total solution cost after applying move
    public void apply(TSPTWSolution s);
    public void undo(TSPTWSolution s);
}
