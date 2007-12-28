/**
 * 
 */
package org.ut.carseq;

class DescCarClass
{
	public int id;
	public int nCars;
	public int requiredOpts[];
	
	public DescCarClass(int theId, int nCarsInClass, int nOptions)
	{
		id  = theId;
		nCars = nCarsInClass;
		requiredOpts = new int[nOptions];
	}   
	
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("{")
		   .append("carClassID:").append(id)
		   .append(" NCars:").append(nCars)
		   .append(" Opts:{");
		
		for (int i=0; i< requiredOpts.length;i++) {
		  	if (requiredOpts[i]==1)
		  		buf.append(i).append(",");
		}
		
		buf.append("}} ");
		
		return buf.toString();
	}
}