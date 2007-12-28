/*
 * CEConstraint.java
 *
 * Created on October 29, 2003, 6:08 PM
 */

package org.ut.carseq;


/**
 *
 * @author  Javier
 */
public interface CEConstraint 
{
    public final static double NO_CONFLICT=0;
    public final static double MAX_CONFLICT=1e20;
    
    public boolean isActive();    
    public void activate(); 
    public void deactivate();
    
    public double getConflict();
    public String getConflictStr();
    public String getViolationInfo();
    public void setViolationInfo(String s);
    
    public void nodeChanged(CENode node, Object oldValue);
    
    public void addObserver(CEConstraintObserver o);
    public void removeObserver(CEConstraintObserver o);    
}
