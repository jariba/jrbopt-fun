/**
 * 
 */
package org.ut.carseq;

class DescOption
{
	public int maxCars;
	public int blockSize;
	
	String toStr_=null;
	
	public String toString()
	{
		if (toStr_ == null) {
			toStr_ = maxCars+" out of "+blockSize;
		}
		
		return toStr_;
	}
}