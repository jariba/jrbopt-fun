package org.ut.carseq;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;


/**
 */
public class CarSeqGantt 
    extends JComponent 
{
	protected Dimension preferredSize = new Dimension(1200,200);

	protected int firstX_;
	protected int firstY_;
	protected int rectWidth_ = 4;
	protected int rectHeight_ =8;
	protected int gridSize_=10;
	protected Color gridColor;
	protected double zoomFactor_;

	protected OptimizerCarSeqDisplay dialog_;
	
	public CarSeqSolution solution_;
	
	public CarSeqGantt(OptimizerCarSeqDisplay dialog,
			           CarSeqSolution solution) 
	{		
		dialog_ = dialog;
		solution_=solution;
		zoomFactor_=1.0;
		//Add a border of 5 pixels at the left and bottom,
		//and 1 pixel at the top and right.
		setBorder(BorderFactory.createMatteBorder(1,5,5,1,
				Color.BLUE));
		
		setBackground(Color.WHITE);
		setOpaque(true);
		this.addMouseMotionListener(new TGDMouseMotionAdapter());
	}
	
	public double getZoomFactor() { return zoomFactor_; }
	public void setZoomFactor(double f) { zoomFactor_=f; }
	
	public Dimension getPreferredSize() 
	{
		return preferredSize;
	}
	
	protected void paintComponent(Graphics g) 
	{
		Insets insets = getInsets();
		firstX_ = insets.left+20;
		firstY_ = insets.top+20;

		//Paint background if we're opaque.
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		g.setColor(Color.GRAY);
		
		// Draw option descriptions
		FontMetrics metrics = g.getFontMetrics();		 
		int maxWidth = 0;
		DescOption options[] = this.dialog_.problem_.options_;
		for (int i=0; i<options.length;i++) {
			DescOption opt = options[i];
			String label = opt.toString();
		    maxWidth = Math.max(maxWidth,metrics.stringWidth(label));
		    g.drawString(label, 
		    		getXCoord(0), 
		    		getYCoord(i+1));
		}
		firstX_ += maxWidth; 
			
		drawGrid(g,gridSize());
		drawCars(g);
	}

	protected void drawCars(Graphics g)
	{
		Color colors[] = {
				Color.BLUE,
				Color.RED,
				Color.GREEN,
				Color.BLACK,
				Color.ORANGE,
				Color.DARK_GRAY,
				Color.YELLOW,
		};
		
		g.setPaintMode();
		g.setColor(Color.BLUE);
		for (int i=0;i<solution_.size();i++) {
			Car c = solution_.getCar(i);
			//g.setColor(colors[i % colors.length]);
			drawCar(g,i,c);
		}
		//g.setColor(Color.GRAY);
	}
	
	protected void drawCar(Graphics g,
			               int seqIdx,
			               Car c)
	{
		DescCarClass carClass = c.getDesc();
		int rectWidth = (int)(rectWidth_*zoomFactor_);
		int rectHeight = (int)(rectHeight_*zoomFactor_);
		
		for (int optIdx=0; optIdx<carClass.requiredOpts.length;optIdx++) {
			if (carClass.requiredOpts[optIdx] != 0) {
				int xCorner = getXCoord(seqIdx);
				int yCorner = getYCoord(optIdx);
				
				if (solution_.isInViolation(seqIdx,optIdx)) {
					g.setColor(Color.RED);
					g.fillRect(
							xCorner, 
							yCorner, 
							gridSize(), 
							gridSize()
					);					
					g.setColor(Color.BLUE);
				}
				
				int xCoord = xCorner+(gridSize()/2)-(rectWidth/2);
				int yCoord = yCorner+(gridSize()/2)-(rectHeight/2);
				g.fillRect(
						xCoord, 
						yCoord, 
						rectWidth, 
						rectHeight
				);
			}
		}
	}
	
	protected int getXCoord(Integer i)
	{
		int value = i.intValue();
		return (int)(firstX_+(value*gridSize()));
	}
	
	protected int getYCoord(Integer i)
	{
		int value = i.intValue();
		return (int)(firstY_+(value*gridSize()));
	}
	
	//Draws a gridSpace x gridSpace grid using the current color.
	private void drawGrid(Graphics g, int gridSpace) 
	{
		Insets insets = getInsets();
		// hack! : harcoded lastX and lastY for now
		int lastX = firstX_+(solution_.size()*gridSpace); //getWidth() - insets.right;
		int lastY = firstY_+(5*gridSpace); //getHeight() - insets.bottom;
		
		//Draw vertical lines.
		int x = firstX_;
		while (x <= lastX) {
			g.drawLine(x, firstY_, x, lastY);
			x += gridSpace;
		}
		
		//Draw horizontal lines.
		int y = firstY_;
		while (y <= lastY) {
			g.drawLine(firstX_, y, lastX, y);
			y += gridSpace;
		}
	}	
	
	protected int gridSize() { return (int)(gridSize_*zoomFactor_); }
	
	private class TGDMouseMotionAdapter
	    extends MouseMotionAdapter
	{
		 public void mouseMoved(MouseEvent e)
		 {
		 	int x = e.getX();
		 	int y = e.getY();
		 	
		 	// Find closest car to x,y
		 	Car closestCar=null;
		 	/*
		 	double minDist=Double.MAX_VALUE;
            for (int i=0;i<solution_.getTourCnt();i++) {
            	List tour = solution_.getTour(i);
            	for (int j=0;j<tour.size();j++) {
            		TSPCity c = (TSPCity)tour.get(j);
                    double dist = getDist(x,y,getXCoord(c.x),getYCoord(c.y)); 
                    if (dist<5 && dist<minDist) {
                    	minDist = dist;
                    	closestCity=c;
                    }
            	}
            }
		 	*/
		 	if (closestCar==null) {
		 		dialog_.setMouseInfo("");
		 	}
		 	else {
		 		dialog_.setMouseInfo(closestCar.toString());
		 	}
		 }
		 
		 protected double getDist(int x1,int y1,int x2,int y2)
		 {
		     return Math.pow(Math.pow(x1-x2,2)+Math.pow(y1-y2,2),0.5);	
		 }
	}
}
