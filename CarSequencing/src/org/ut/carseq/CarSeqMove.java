/*
 * Created on Apr 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.carseq;

/**
 * @author Javier
 *
 * 
 */
public interface CarSeqMove 
{
    public int getFromPosition();
    public int getToPosition();
    public double getCost(); // New total solution cost after applying move
    public void apply(CarSequence s);
    public void undo(CarSequence s);
}
