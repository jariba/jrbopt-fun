package org.ut.mh.tsp.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.ut.mh.tsp.*;

public class OptimizerTSPDisplay 
    extends MouseAdapter
    implements OptimizerListener
{
	protected List solutions_;
	protected int curSolution_;
	protected JButton nextBtn_,prevBtn_,zoomBtn_;
	protected JTextField zoomValue_;
	protected JLabel costLabel_;
	protected JLabel iterationLabel_;
	protected JLabel mouseInfoLabel_;
	protected TSPGridDisplay tspGrid_=null;
	protected JPanel uiPanel_;
	
    public OptimizerTSPDisplay()
    {
		solutions_=new Vector();
		curSolution_=-1;
		uiPanel_=null; // lasy creation
    }

    void createUIPanel()
    {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		nextBtn_ = new JButton("Next Solution");
		nextBtn_.addMouseListener(this);
		prevBtn_ = new JButton("Prev Solution");
		prevBtn_.addMouseListener(this);
		costLabel_=new JLabel("Cost:");
		JPanel cbPanel = new JPanel();
		cbPanel.add(costLabel_);
		cbPanel.add(nextBtn_);
		cbPanel.add(prevBtn_);		
		mouseInfoLabel_ = new JLabel("");
		JPanel tbPanel = new JPanel();
		tbPanel.add(mouseInfoLabel_);
		JPanel bbPanel = new JPanel();
		bbPanel.add(nextBtn_);
		bbPanel.add(prevBtn_);		
		bottomPanel.add(tbPanel,BorderLayout.NORTH);
		bottomPanel.add(cbPanel,BorderLayout.CENTER);
		bottomPanel.add(bbPanel,BorderLayout.SOUTH);
		
		
		JPanel topPanel = new JPanel();
		zoomBtn_ = new JButton("Zoom");
		zoomBtn_.addMouseListener(this);
		zoomValue_ = new JTextField("1.0  ");
		iterationLabel_=new JLabel("Iteration:");
		topPanel.add(zoomValue_);
		topPanel.add(zoomBtn_);
		topPanel.add(iterationLabel_);
		
		tspGrid_=new TSPGridDisplay(this,new TSPSolution());

		JPanel optPanel = new JPanel(new BorderLayout());
		optPanel.add(topPanel,BorderLayout.NORTH);
		optPanel.add(tspGrid_,BorderLayout.CENTER);
		optPanel.add(bottomPanel,BorderLayout.SOUTH);

		uiPanel_ = optPanel;		
    }    
    
    public JPanel getUIPanel() 
    {
    	if (uiPanel_==null)
    		createUIPanel();
    	
    	return uiPanel_; 
    }
    
    public void displayCurrentSolution()
    {
    	if (solutions_.size()==0)
    		return;
    	
    	Integer iteration=(Integer)((Object[])solutions_.get(curSolution_))[0];   	
    	TSPSolution solution=(TSPSolution)((Object[])solutions_.get(curSolution_))[1]; 
    	costLabel_.setText(
    	    "Solution "+(curSolution_+1)+" of "+solutions_.size()+
			" Iteration:"+iteration+
		    " TravelCost:"+solution.getCost()+
		    " LatenessCnt:"+solution.latenessCnt+
		    " TotalLateness:"+solution.totalLateness
    	);
    	
        tspGrid_.solution_=solution;
    	tspGrid_.repaint();
    }

    // This method must always be called within the Swing thread
    public void setCurrentSolution(int i)
    {
    	if (i >= solutions_.size() || i < 0)
    		return;
    	
    	curSolution_=i;

    	if (curSolution_==solutions_.size()-1)
    		nextBtn_.setEnabled(false);
    	else
    		nextBtn_.setEnabled(true);

    	if (curSolution_==0)
    		prevBtn_.setEnabled(false);
    	else
    		prevBtn_.setEnabled(true);
    	
	    displayCurrentSolution();
    }
    
    public void setMouseInfo(String txt)
    {
    	mouseInfoLabel_.setText(txt);
    }
    
   	public void mouseClicked(MouseEvent e)
    {
        if (e.getSource()==nextBtn_) {
            setCurrentSolution(curSolution_+1);
       	}
        if (e.getSource()==prevBtn_) {
            setCurrentSolution(curSolution_-1);
        }
        if (e.getSource()==zoomBtn_) {
        	double zf = new Double(this.zoomValue_.getText()).doubleValue();
            tspGrid_.setZoomFactor(zf);
        	tspGrid_.repaint();
        }
    }
    
	public void newSolutionFound(int iteration,TSPSolution solution) 
	{
		solutions_.add(new Object[]{new Integer(iteration),solution});		
	    SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        	    setCurrentSolution(solutions_.size()-1);
            }
        });
	}

	public void iterationCompleted(final int n) 
	{
	    SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		iterationLabel_.setText("Iteration:"+n);
            }
	    });
	}    
}