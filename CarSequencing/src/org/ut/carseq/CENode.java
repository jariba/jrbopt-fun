/*
 * CENode.java
 *
 * Created on October 29, 2003, 6:07 PM
 */

package org.ut.carseq;

import java.util.List;
import java.util.Vector;

/**
 *
 * @author  Javier
 */
public class CENode 
{
    private Object value_;
    private List<CEConstraint> constraints_;
    
    public CENode() 
    {
        value_ = null;
        constraints_ = new Vector<CEConstraint>();
    }    
    
    public Object getValue() { return value_; }
    
    public void setValue(Object o) 
    { 
    	Object oldValue = value_;
        value_=o; 
        for (int i=0;i<constraints_.size();i++) {
            CEConstraint c = (CEConstraint)constraints_.get(i);
            c.nodeChanged(this,oldValue);
        }
    }
    
    protected void addConstraint(CEConstraint c)
    {
        constraints_.add(c);
    }
    
    public List<CEConstraint> getConstraints()
    {
        return constraints_;
    }
    
    public String toString()
    {
        if (getValue()!=null)
            return getValue().toString();
        else
            return null;
    }
    
    public double getConflict() 
    { 
    	double conflict = 0;
        for (int i=0;i<constraints_.size();i++) {
            conflict += constraints_.get(i).getConflict();
        }
        
        return conflict;
    }    
}
