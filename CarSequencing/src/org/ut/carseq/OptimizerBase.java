package org.ut.carseq;

import java.util.*;
import java.io.*;
import org.ut.util.SimpleTimer;
import org.ut.util.Log;


/**
 * @author Javier
 */
public abstract class OptimizerBase
    implements Optimizer
{
	protected String name_;
	protected List listeners_ = new Vector();
	protected SolutionInfo bestSolutionInfo_;
	protected SimpleTimer solutionTimer_; // TODO: this is not a good place for this
	
    public OptimizerBase(String name)
    {
    	name_=name;
    	listeners_ = new Vector();
    }
    
    public String getName() { return name_; }
    
    public void addOptimizerListener(OptimizerListener o)
    {
    	listeners_.add(o);
    }
    
    public void removeOptimizerListener(OptimizerListener o)
    {
    	listeners_.remove(o);    	
    }
    
    protected void notifyNewSolution(int iteration,CarSeqSolution solution)
    {
    	long elapsedTime = solutionTimer_.getElapsed();
    	for (int i=0;i<listeners_.size();i++) {
    		OptimizerListener listener = (OptimizerListener)listeners_.get(i);
    		listener.newSolutionFound(iteration,solution);
    	}
    	//logNewSolution(iteration,elapsedTime,solution);
    	bestSolutionInfo_ = new SolutionInfo(iteration,elapsedTime,solution);
    }

    protected void notifyIterationCompleted(int n)	
    {
    	for (int i=0;i<listeners_.size();i++) {
    		OptimizerListener listener = (OptimizerListener)listeners_.get(i);
    		listener.iterationCompleted(n);
    	}
    } 
    
    protected void logNewSolution(int iteration,long elapsedTime,CarSeqSolution solution)
    {
    	try {
    		String msg = "Improving Solution Found at (iteration,time in msecs) "+
  			             iteration+" "+
			             elapsedTime+" "+
			             solution;
    		Log.debug(msg);
    		
    		PrintWriter out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-delta.txt",true)));
    		out.println(msg+"\n");
    		out.close();
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    protected void logBestSolution()
    {
    	try {
    		String msg="Best Solution Found at (iteration,time in msecs) "+
  			    bestSolutionInfo_.iteration+" "+
			    bestSolutionInfo_.msecs+"\n"+
			    bestSolutionInfo_.solution.detailStr();
    		
    		Log.debug(msg);
    		PrintWriter out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-delta.txt",true)));
    		out.println(msg+"\n");
    		out.close();
    		
    		out = new PrintWriter(new BufferedWriter(
    				new FileWriter("output/"+getName()+"-best.txt",true)));
    		out.println(msg);
    		out.close();
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }
    
    protected void resetBestSolution()
    {
    	bestSolutionInfo_=null;
    	solutionTimer_=new SimpleTimer();  
    	solutionTimer_.start();
    }

    protected static class SolutionInfo
	{
    	public int iteration;
    	public long msecs;
    	public CarSeqSolution solution;
    	
    	public SolutionInfo(int it,long t, CarSeqSolution s)
    	{
    		iteration = it;
    		msecs = t;
    		solution =s;
    	}
	}
}
