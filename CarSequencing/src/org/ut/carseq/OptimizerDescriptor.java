/*
 * Created on Mar 3, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.ut.carseq;

/**
 * @author Javier
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class OptimizerDescriptor 
{
    protected String name_;
    protected OptimizerArg args_[];
	
	public OptimizerDescriptor(String name,
			                   OptimizerArg args[])
	{
		name_ = name;
		args_ = args;
	}
	
	public String getName() { return name_; }
	public OptimizerArg[] getArgs() { return args_; }
	
	public String toString() { return getName(); }
}
