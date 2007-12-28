package org.ut.mh.tsp.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;

import org.ut.mh.tsp.*;

/**
 */
public class TSPGridDisplay 
    extends JComponent 
{
	protected Dimension preferredSize = new Dimension(500,400);
	protected Color gridColor;
	protected int gridSize_=10;
	protected int firstX_;
	protected int firstY_;
	protected int minX_;
	protected int minY_;
	protected double zoomFactor_;
	protected OptimizerTSPDisplay dialog_;
	
	public TSPSolution solution_;
	
	public TSPGridDisplay(OptimizerTSPDisplay dialog,TSPSolution solution) 
	{		
		dialog_ = dialog;
		solution_=solution;
		zoomFactor_=1.0;
		//Add a border of 5 pixels at the left and bottom,
		//and 1 pixel at the top and right.
		setBorder(BorderFactory.createMatteBorder(1,5,5,1,
				Color.RED));
		
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
		firstX_ = insets.left;
		firstY_ = insets.top;

		//Paint background if we're opaque.
		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		g.setColor(Color.GRAY);
		//drawGrid(g,gridSize_);
		drawCities(g);
	}

	protected void drawCities(Graphics g)
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
		
		minX_=Integer.MAX_VALUE;
		minY_=Integer.MAX_VALUE;

		for (int i=0;i<solution_.getTourCnt();i++) {
			List tour = (List)solution_.getTour(i);
			for (int j=0;j<tour.size();j++) {
			    TSPCity c = (TSPCity)tour.get(j);
			    if (c.x.intValue() < minX_)
			    	minX_ = c.x.intValue();
			    if (c.y.intValue() < minY_)
			    	minY_ = c.y.intValue();
			}
		}
		minX_-=2;
		minY_-=2;

		g.setPaintMode();
		for (int i=0;i<solution_.getTourCnt();i++) {
			List tour = (List)solution_.getTour(i);
			g.setColor(colors[i % colors.length]);
			drawTour(g,tour);
		}
		g.setColor(Color.GRAY);
	}
	
	protected void drawTour(Graphics g,List cities)
	{
		if (cities.size()==0)
			return;
		
		TSPCity lastCity=null;
		TSPCity currentCity=null;
		for (int i=0;i<cities.size();i++) {
		    currentCity = (TSPCity)cities.get(i);
			//g.setColor(getForeground());
			
			int rectHalfSize=2,rectSize=(rectHalfSize*2)+1;
			g.fillRect(getXCoord(currentCity.x) - rectHalfSize, 
					   getYCoord(currentCity.y) - rectHalfSize, 
					   rectSize, 
					   rectSize
		    );
		    
			
			g.drawString(currentCity.id.toString(),
					     getXCoord(currentCity.x),
						 getYCoord(currentCity.y)
			);
			
			if (lastCity != null) {
				g.drawLine(getXCoord(lastCity.x),
						   getYCoord(lastCity.y),
						   getXCoord(currentCity.x),
						   getYCoord(currentCity.y)
				);
			}
			lastCity=currentCity;
		}

		// Finish the tour
		currentCity = (TSPCity)cities.get(0);
        if (lastCity != null) {
                g.drawLine(getXCoord(lastCity.x),
                                   getYCoord(lastCity.y),
                                   getXCoord(currentCity.x),
                                   getYCoord(currentCity.y)
                );
        }	
    }
	
	protected int getXCoord(Integer i)
	{
		// Hack : subtract min(city.x) so that it shows up near the
		// corner
		int value = i.intValue()-minX_;
		return (int)(firstX_+(value*gridSize_*zoomFactor_));
	}
	
	protected int getYCoord(Integer i)
	{
		// Hack : subtract min(city.y) so that it shows up near the
		// corner
		int value = i.intValue()-minY_;
		return (int)(firstY_+(value*gridSize_*zoomFactor_));
	}
	
	//Draws a gridSpace x gridSpace grid using the current color.
	private void drawGrid(Graphics g, int gridSpace) 
	{
		Insets insets = getInsets();
		int lastX = getWidth() - insets.right;
		int lastY = getHeight() - insets.bottom;
		
		//Draw vertical lines.
		int x = firstX_;
		while (x < lastX) {
			g.drawLine(x, firstY_, x, lastY);
			x += gridSpace;
		}
		
		//Draw horizontal lines.
		int y = firstY_;
		while (y < lastY) {
			g.drawLine(firstX_, y, lastX, y);
			y += gridSpace;
		}
	}	
	
	private class TGDMouseMotionAdapter
	    extends MouseMotionAdapter
	{
		 public void mouseMoved(MouseEvent e)
		 {
		 	int x = e.getX();
		 	int y = e.getY();
		 	
		 	// Find closest coty to x,y
		 	TSPCity closestCity=null;
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
		 	
		 	if (closestCity==null) {
		 		dialog_.setMouseInfo("");
		 	}
		 	else {
		 		int lateness=Math.max(0,closestCity.visitTime.intValue()-closestCity.maxTime.intValue());
		 		int wait=Math.max(0,closestCity.minTime.intValue()-closestCity.visitTime.intValue());
		 		dialog_.setMouseInfo("Time Window for City:"+closestCity.id
		 				+" ("+closestCity.minTime+","+closestCity.maxTime+")"
						+" ArrivalTime:"+closestCity.visitTime
						+" Lateness:"+lateness
						+" Wait:"+wait
			    );
		 	}
		 }
		 
		 protected double getDist(int x1,int y1,int x2,int y2)
		 {
		     return Math.pow(Math.pow(x1-x2,2)+Math.pow(y1-y2,2),0.5);	
		 }
	}
}
