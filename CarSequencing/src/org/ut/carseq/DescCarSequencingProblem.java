/**
 * 
 */
package org.ut.carseq;

class DescCarSequencingProblem
{
	public int nCars_;
	public int nOptions_;
	public int nClasses_;
	public DescOption options_[];
	public DescCarClass carClasses_[];
	
	public DescCarSequencingProblem(int cars,int options,int classes)
	{
		nCars_ = cars;
		nOptions_ = options;
		nClasses_ = classes;
		
        options_ = new DescOption[nOptions_];
        for (int i=0;i<options_.length;i++)
        	options_[i] = new DescOption();
        
        carClasses_ = new DescCarClass[nClasses_];
	}
}