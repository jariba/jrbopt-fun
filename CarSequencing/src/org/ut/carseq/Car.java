package org.ut.carseq;

import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

public class Car extends CObject
{
	static int idCnt=0;
	static Map<Integer,Car> idMap_ = new HashMap();
	
	int id_;
	DescCarClass carDesc_;
	CENode slot_;
	
	public Car(DescCarClass carDesc, Vector<CEHubXinY> xINyHubs)
	{
		id_ = idCnt++;
		carDesc_ = carDesc;
		slot_ = new CENode();
		
		// create constraints
		for (int i=0; i< carDesc_.requiredOpts.length;i++) {
		  	if (carDesc_.requiredOpts[i]==1) {
                CECXinY constraint = new CECXinY(xINyHubs.get(i),slot_);
		  		addConstraint(constraint);
		  	}
		}
		
		activateConstraints();
		idMap_.put(id_, this);
	}
	
	public int getID() { return id_; }
	
	public static Car getByID(int id) { return idMap_.get(id); }
	
	public Integer getSlot() { return (Integer) slot_.getValue(); }
	public void setSlot(Integer i) { slot_.setValue(i); }
	
	public DescCarClass getDesc()
	{
		return carDesc_;
	}
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("{")
		   .append("Slot:").append(slot_.getValue()).append(" ")
		   .append("carID:").append(id_).append(" ")
		   .append(carDesc_.toString())
		   .append("} ")
		   .append(getViolations());
		
		return buf.toString();
	}
}
