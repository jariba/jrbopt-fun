package org.ut.carseq;

import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.ut.util.Util;

public class CarSequence 
{
    List<Car> cars_;
    List<CEHubXinY> xInYHubs_;
    
    public CarSequence(Vector<CEHubXinY> xInYHubs)
	{	
    	cars_ = new Vector<Car>();
    	xInYHubs_ = xInYHubs;
	}
    
    public List<CEHubXinY> getHubs() { return xInYHubs_; }
    public Car getCar(int idx) { return cars_.get(idx); }

    public void addCar(Car c) 
    { 
    	c.setSlot(cars_.size());
    	cars_.add(c); 
    }
    
    public List<Car> getCars() { return cars_; }
    
    public void swapCars(int a,int b)
    {
    	cars_.get(a).setSlot(b);
    	cars_.get(b).setSlot(a);
    	Util.swap(cars_,a,b);
    }
    
    public int getViolationCnt()
    {
		return (int) getTotalConflict();
    }
    
    public double getTotalConflict()
    {
    	double total = 0.0;
    	
		for (int i=0;i<cars_.size();i++) {
			total += cars_.get(i).getConflict();
		}      
		
		return total;
    }
    
    public double getCost()
    {
    	return getTotalConflict();
    }
    
    public int size()
    {
    	return cars_.size();
    }
    
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("Violations:").append(getViolationCnt()).append(" - ");
		for (int i=0;i<cars_.size();i++) {
			buf.append(cars_.get(i).toString()).append("\n");
		}
		
		return buf.toString();
	}
}
