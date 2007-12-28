package org.ut.carseq;

import java.util.Vector;
import org.ut.util.Util;


public class CarSeqSolution 
{
    Vector<Integer> seq_;
    double cost_;
    boolean violations_[][];
    String elapsedTimeStr_;
    
    public CarSeqSolution(CarSequence seq)
    {
        int nSlots = seq.size();
        int nOptions = seq.getHubs().size();

        violations_ = new boolean[nSlots][nOptions];		
    	for (int i=0;i<nSlots;i++) 
        	for (int j=0;j<nOptions;j++)
        		violations_[i][j] = false;

    	seq_ = new Vector<Integer>();
    	for (int i=0;i<nSlots;i++) {
    		Car c = seq.getCar(i);
    		seq_.add(c.getID());
    		DescCarClass carClass = c.getDesc();
    		for (int optIdx=0; optIdx<carClass.requiredOpts.length;optIdx++) {
    			if (carClass.requiredOpts[optIdx] != 0) {
    				violations_[i][optIdx] = seq.getHubs().get(optIdx).isInViolation(i);
    			}
    		}
    	}
    	
    	cost_ = seq.getCost();
    	elapsedTimeStr_ = "0 msecs";
    }
    
    public String getElapsedTimeStr() { return elapsedTimeStr_; }
    public void setElapsedTimeStr(String s) { elapsedTimeStr_ = s; }
    
    public boolean isInViolation(int slotIdx,int optionIdx)
    {
    	return violations_[slotIdx][optionIdx];
    }
    
    public double getCost() { return cost_; }
    
    public String detailStr()
    {
       StringBuffer buf = new StringBuffer();
       
       // First, set the slots on the car to match this solution
       int i=0;
       for (Integer id : seq_) {
   	       Car.getByID(id).setSlot(i);
   	       i++;
       }
       
       buf.append(toString()).append("\n");
       for (Integer id : seq_) {
    	    buf.append(Car.getByID(id).toString()).append("\n"); 
       }
       
       return buf.toString();
    }
    
    public Car getCar(int idx)
    {
    	if (idx<0 || idx>= seq_.size())
    		return null;
    	
    	Integer id = seq_.get(idx);
    	return Car.getByID(id);
    }
    
    public int size()
    {
    	return seq_.size();
    }
    
    public String toString()
    {
        return "Violations:"+cost_+" "+Util.listToString(seq_, ",");	
    }
}
