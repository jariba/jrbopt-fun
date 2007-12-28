package org.ut.mh.tsp;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import org.ut.mh.tsp.ui.*;
import org.ut.mh.tsp.grasp.*;
import org.ut.mh.tsp.sa.*;
import org.ut.mh.tsp.ga.*;
import org.ut.mh.tsp.ts.*;
import org.ut.mh.tsp.util.*;


public class TSPSolver
{
	protected static JDesktopPane desktop_;
	protected static JComboBox optList_;
	protected static OptimizerArgValues optArgValues_;
	protected static Map optArgTextValues_;
	protected static JPanel optLauncherPanel_; 
	protected static JPanel optArgsPanel_; 
	protected static int windowCnt_=0;
	
    public static void main(String args[])
    {
    	List od=loadOptimizerDescriptors();
	    SwingUtilities.invokeLater(new UICreator(od));
    }   
    
    static List loadOptimizerDescriptors()
    {
        List l = new Vector();

        l.add(new OptimizerDescriptor(
        		"TSOptimizer",
				new OptimizerArg[] {
        				new OptimizerArg("DataFile","Data File","data/TSP-TW.txt"),						
        				new OptimizerArg("Replications","Replications","1"),						
        				new OptimizerArg("MoveTabuTenure","Keep performed moves in tabu list for this number of iterations","5"),						
        				new OptimizerArg("SolutionTabuTenure","Keep visited solutions in tabu list for this number of iterations","50"),						
        				new OptimizerArg("LatenessPenalty","Lateness penalty per time unit","0.5"),						
        				new OptimizerArg("LatenessHardThreshold","Don't allow lateness for a particular customer beyond this number of time units","60"),						
        				new OptimizerArg("MaxLatenessCount","Don't allow more than this number of late arrivals","500"),						
        				new OptimizerArg("MaxFailures","Max Consecutive Iterations without improvement before quitting","500"),
        		}
        	)
		);
        
        l.add(new OptimizerDescriptor(
        		"GAOptimizer",
				new OptimizerArg[] {
        				new OptimizerArg("DataFile","Data File","data/hwk2-TSP-data.txt"),						
        				new OptimizerArg("Replications","Replications","1"),						
        				new OptimizerArg("PopSize","Population Size","20"),						
        				new OptimizerArg("MutationRate","Mutation Rate","0.01"),						
        				new OptimizerArg("MaxFailures","Max Consecutive Generations without improvement before quitting","500"),
        		}
        	)
		);
        
        l.add(new OptimizerDescriptor(
        		"SA2TSPOptimizer",
				new OptimizerArg[] {
        				new OptimizerArg("DataFile","Data File","data/hwk2-TSP-data.txt"),						
        				new OptimizerArg("Replications","Replications","1"),						
        				new OptimizerArg("InitTemp","Initial Temperature","1000"),						
        				new OptimizerArg("TempChgRate","TemperatureChange Rate","0.9"),						
        				new OptimizerArg("StageSize","Number of iterations in a Stage","100"),						
        				new OptimizerArg("MaxFailures","Max Consecutive Failures to improve before quitting","50"),
        		}
        	)
		);
        
        l.add(new OptimizerDescriptor(
        		"SAOptimizer",
				new OptimizerArg[] {
        				new OptimizerArg("DataFile","Data File","data/hwk2-TSP-data.txt"),						
        				new OptimizerArg("Replications","Replications","1"),						
        				new OptimizerArg("InitTemp","Initial Temperature","100"),						
        				new OptimizerArg("TempChgRate","TemperatureChange Rate","0.9"),						
        				new OptimizerArg("StageSize","Number of iterations in a Stage","100"),						
        				new OptimizerArg("MaxFailures","Max Consecutive Failures to improve before quitting","50"),
        		}
        	)
		);
        
        l.add(new OptimizerDescriptor(
        		"GRASPOptimizer",
				new OptimizerArg[] {
        				new OptimizerArg("DataFile","Data File","data/hwk2-TSP-data.txt"),						
        				new OptimizerArg("Replications","Replications","1"),						
        				new OptimizerArg("CandidateCnt","Number Of Candidates to evaluate","1000"),						
        				new OptimizerArg("NeighborCnt","Number of closest neighbors to consider to build candidate","5"),						
        		}
        	)
		);
        
        return l;
    }
    
    static JInternalFrame makeOptFrame(
    		String name,
    		OptimizerTSPDisplay optDialog)
    {
        JInternalFrame frame = new JInternalFrame(name);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(optDialog.getUIPanel(),BorderLayout.CENTER);
	    desktop_.add(frame);
	    int offset=windowCnt_*15;
	    windowCnt_++;
	    frame.setLocation(offset,offset);
	    frame.setSize(500,500);
	    frame.setResizable(true);
	    frame.setClosable(true);
	    frame.setMaximizable(true);
	    frame.setIconifiable(true);
        frame.setVisible(true);
        return frame;
    }
    
    public static Optimizer makeGRASPOptimizer(OptimizerArgValues args)
    {
    	int nCandidates=args.getInt("CandidateCnt");
    	CandidateBuilder candidateBuilder=new CandidateBuilder1(args.getInt("NeighborCnt"));
    	LocalSearch localSearch=new LSOneSwap();
    		    
    	return new GraspTSPOptimizer(
    			"GRASP1TSP",
	    		nCandidates,      // Number of candidates to look at
				candidateBuilder, // GMMOG candidate builder
				localSearch       // LocalSearchRoutine
		);	    
    }
    
    public static Optimizer makeSAOptimizer(OptimizerArgValues args)
    {
    	return new SATSPOptimizer(
    		"SA1TSP",	
    	    new SAProblemMapper_1TSP_JB1(
    			    args.getInt("InitTemp"),       // initial temperature
					args.getDouble("TempChgRate"), // temperature change rate,
					args.getInt("StageSize")       // iterations at each temperature level
    	    ),
			args.getInt("MaxFailures") // number of iterations without improvement before quitting
    	);
    }
    
    public static Optimizer makeSA2TSPOptimizer(OptimizerArgValues args)
    {
    	return new SATSPOptimizer(
    			"SA2TSP",
        	    new SAProblemMapper_2TSP_JB1(
        			    args.getInt("InitTemp"),       // initial temperature
    					args.getDouble("TempChgRate"), // temperature change rate,
    					args.getInt("StageSize")       // iterations at each temperature level
        	    ),
    			args.getInt("MaxFailures") // number of iterations without improvement before quitting
        	);
    }
    
    public static Optimizer makeGAOptimizer(OptimizerArgValues args)
    {
    	return new GATSPOptimizer(
    		"GA1TSP",	
    	    new GAProblemMapper_1TSP_JB1(
    	    		args.getInt("PopSize"), // Population Size
					args.getDouble("MutationRate")
    	    ),
			args.getInt("MaxFailures") // number of iterations without improvement before quitting
    	);
    }

    public static Optimizer makeTSOptimizer(OptimizerArgValues args)
    {
    	return new TabuSearchTSPOptimizer(
    		"TabuSearchTSP",	
			args.getInt("MoveTabuTenure"), 
			args.getInt("SolutionTabuTenure"), 
			args.getDouble("LatenessPenalty"), 
			args.getInt("LatenessHardThreshold"), 
			args.getInt("MaxLatenessCount"), 
			args.getInt("MaxFailures") // number of iterations without improvement before quitting
    	);
    }
        
    static void runOptimizer(OptimizerDescriptor optDesc,
    		                 OptimizerArgValues args)
    {
    	try {
    		Class c = Class.forName("org.ut.mh.tsp.TSPSolver");
    		Class argTypes[]=new Class[]{OptimizerArgValues.class};
    		Method m = c.getMethod("make"+optDesc.getName(),argTypes);
    		final Optimizer opt = (Optimizer)m.invoke(null,new Object[]{args});

    		// hook in UI
    		OptimizerTSPDisplay optDialog = new OptimizerTSPDisplay();  
    	    opt.addOptimizerListener(optDialog);
            makeOptFrame(optDesc.getName(),optDialog);
            
            final int replications = args.getInt("Replications");
    		final String dataFile = (String)args.get("DataFile");
            
            Thread handler = new Thread() {
            	public void run() {
            		List cities = readTSPCities(dataFile);
            		for (int i=0;i<replications;i++) {
            			opt.solve(cities);  
            			Log.debug("Finished replication "+(i+1)+" out of "+replications);
            		}
            	}
            };
            handler.start();
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }

    static void fakeSleep(long msecs)
        throws Exception
    {
        String s = new String("");
        synchronized (s) {
            s.wait(msecs);
        }    	
    }
    
    private static class UICreator
        implements Runnable
    {
    	List optDescriptors_;
    	
	    public UICreator(List od) 
	    { 
	    	optDescriptors_=od;
	    }
	    
	    public void run() 
	    {	
	    	createAndShowGUI(optDescriptors_); 
	    }    	
    }

    static private void createAndShowGUI(List optDescriptors) 
    {
    	try {
    		//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
    		//UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    		JFrame.setDefaultLookAndFeelDecorated(true);
    		
    		//Create and set up the window.
    		JFrame frame = new JFrame("TSP Optimizers");
    		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		frame.getContentPane().setLayout(new BorderLayout());    		
    		desktop_ = new JDesktopPane();
    		frame.getContentPane().add(makeOptLauncherPanel(optDescriptors),BorderLayout.NORTH);
    		frame.getContentPane().add(desktop_,BorderLayout.CENTER);

    		//Display the window.
    		frame.pack();
    		frame.setSize(700,700);
    		frame.setVisible(true);
    	}
    	catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }    
    
    static JPanel makeOptLauncherPanel(List optDescriptors)
    {
    	JPanel panel = new JPanel(new FlowLayout());
    	
    	optList_ = new JComboBox(optDescriptors.toArray());
    	optList_.addItemListener(new ItemListener() {
    		public void itemStateChanged(ItemEvent e)  
    		{
    			if (e.getStateChange()==ItemEvent.SELECTED)
    		        resetArgsPanel();	
    		} 
    	});
    	

    	panel.add(new JScrollPane(optList_));
    	JButton optBtn = new JButton("Run Optimizer");
    	optBtn.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) 
    		{
    			optArgValues_ = new OptimizerArgValues();
    	        OptimizerDescriptor od = (OptimizerDescriptor)optList_.getSelectedItem();
    	        OptimizerArg args[]=od.getArgs();
    	        for (int i=0;i<args.length;i++) {
    	        	JTextField tf = (JTextField)optArgTextValues_.get(args[i].name);
    	        	optArgValues_.put(args[i].name,tf.getText());
    	        }
    			
    			runOptimizer(
    					(OptimizerDescriptor)optList_.getSelectedItem(),
						optArgValues_);
    		} 
    	});
    	
    	panel.add(optBtn);
    	
    	optLauncherPanel_ = new JPanel(new BorderLayout());
    	optLauncherPanel_.add(BorderLayout.NORTH,panel);
    	optArgsPanel_=new JPanel(new BorderLayout());
    	optLauncherPanel_.add(BorderLayout.CENTER,optArgsPanel_);
    	
    	resetArgsPanel();
    	
    	return optLauncherPanel_;
    }
    
    static void resetArgsPanel()
    {
    	optArgTextValues_ = new HashMap();
        OptimizerDescriptor od = (OptimizerDescriptor)optList_.getSelectedItem();
        OptimizerArg args[]=od.getArgs();
        JPanel p = new JPanel(new GridLayout(args.length,2));
        for (int i=0;i<args.length;i++) {
        	p.add(new JLabel(args[i].description));
        	JTextField tf =new JTextField(args[i].defaultValue); 
        	p.add(tf);
        	optArgTextValues_.put(args[i].name,tf);
        }
        optArgsPanel_.removeAll();
        optArgsPanel_.add(BorderLayout.CENTER,p);
        optLauncherPanel_. revalidate();
    }
    
    static List readTSPCities(String filename)
    {
	    try {
	        List retval = new Vector();
	        BufferedReader in  = new BufferedReader(new FileReader(filename));
	        String line = null;
            while ((line=in.readLine())!=null) {
	            StringTokenizer tok = new StringTokenizer(line," \t");
	            Integer id = getTokenAsInteger(tok);
	            Integer x  = getTokenAsInteger(tok);
	            Integer y  = getTokenAsInteger(tok);
	            Integer minTime=new Integer(0);
	            Integer maxTime=new Integer(Integer.MAX_VALUE);
	            if (tok.hasMoreTokens()) {
	            	minTime  = getTokenAsInteger(tok);
	            	maxTime  = getTokenAsInteger(tok);
	            }
	            
	            retval.add(new TSPCity(id,x,y,minTime,maxTime));
            }
            return retval;
        } 
        catch (Exception e) {
	        throw new RuntimeException(e);
        } 
    }
    
    static int getInt(String s)
    {
    	return (new Integer(s)).intValue();
    }

    static Integer getTokenAsInteger(StringTokenizer tok)
    {
	    return new Integer(tok.nextToken());
    }	
}