package org.ut.carseq;

public class CECXinY 
    extends CEConstraintBase 
{	
	CEHubXinY hub_;
	
	public CECXinY(CEHubXinY hub,CENode idx)
	{
		hub_ = hub;
		addNode(idx);
	}
	
    public void activate() 
    { 
        super.activate();
    }
	
	protected void onNodeChanged(CENode node,Object oldValue) 
	{
		if (oldValue != null)
			hub_.freeCapacity(((Integer)oldValue).intValue());
		
		Object newValue = node.getValue();
		if (newValue != null)
			hub_.useCapacity(((Integer)newValue).intValue());		
	}
	
	protected void checkConstraint()
	{
		Object value = getNode(0).getValue();
		if (value!=null) {
			int idx = ((Integer)value).intValue();
	        if (hub_.isInViolation(idx)) {	
			    conflict_ = hub_.getConflict();
			    setViolationInfo(hub_.getId()+"-{"+hub_.x_+","+hub_.y_+"} XinY violated");
			    return;
	        }
		}
		
		conflict_ = NO_CONFLICT;
	}
	
    public double getConflict() 
    { 
        checkConstraint();    	
    	return conflict_; 
    }
    
    /* 
     * violation info. TODO: make this more sophisticated later
     */
    public String getViolationInfo() 
    {
        checkConstraint();    	
    	return vinfo_; 
    }	
}
