package battletech;

import java.awt.Color;
/*
 * JPanel.java
 *
 * Created on December 7, 2002, 2:24 PM
 */

/**
 *
 * @author  Fred
 */

/** This class represents the map display in the BattleTech GUI. */

public class mapPaneltype extends javax.swing.JPanel {

    int curX;
    int curY;
	int currentMech = 0;
	BattleTechRemote theGame;
	String myName;

	public mapPaneltype(BattleTechRemote game, String _name)
	{
			theGame = game;
			myName = _name;
			initComponents();
	}

	/** Takes in the number of the player this map display belongs to (1 for player 1, 2 for player 2). */
	public void setCurrentMech(int mech) 
	{
		currentMech = mech;
	}

	/** Initialize all components. */
	public void initComponents() {//GEN-BEGIN:initComponents
		//Check for Mouse Clicks on Map
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                changeMechPosition(evt);
            }
        });
    }//initComponents

	/** Translate current x and y into BattleTech map coordinates. */
	public Point getPoint() {
	//Get current clicked point
		return (new Point(curX/4,curY/4));
	}

	/** Sets the current x and y to the points of last mouse click. */
    public void changeMechPosition(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_changeMechPosition
        curX = evt.getX();
        curY = evt.getY();
		repaint();
    }//GEN-LAST:event_changeMechPosition

	/** Refresh all components. */
    public void paint(java.awt.Graphics graphics) { 		Map tempMap;
		try{
		//Clear Map
		graphics.clearRect(0,0,400,400);

		tempMap = theGame.getMap();

		for (int i=0; i < (tempMap.getWidth()-1); i++) {
			for (int j=0; j < (tempMap.getLength()-1); j++) {
				int tempElev = tempMap.getNearestGrid(i,j).getElevation();
				
				//Set maps 0=clear, 1=rough,2=light wood,3=heavy wood, 4=water
				if (tempMap.getNearestGrid(i,j).getType()==0) {
					graphics.setColor(new Color(125+tempElev*30,125+tempElev*30,50+tempElev*30));
					
				} else if (tempMap.getNearestGrid(i,j).getType()==1) {
					graphics.setColor(new Color(190+tempElev*10,190+tempElev*10,190+tempElev*10));
				} else if (tempMap.getNearestGrid(i,j).getType()==2) {
					graphics.setColor(new Color(100+tempElev*20,190+tempElev*20,100+tempElev*20));
				} else if (tempMap.getNearestGrid(i,j).getType()==3) {
					graphics.setColor(new Color(50+tempElev*20,100+tempElev*20,50+tempElev*20));
				} else if (tempMap.getNearestGrid(i,j).getType()==4) {
					graphics.setColor(new Color(0,0,255+tempElev*20));
				} else {
					graphics.setColor(Color.black);
				}

				graphics.fillRect(i*4,j*4,4,4);
			}
		}
		//Draw Friendly Mech
        graphics.setColor(Color.blue);
        graphics.fillOval(theGame.getOwnMech(myName).getLocation().getx()*4-5,theGame.getOwnMech(myName).getLocation().gety()*4-5,10,10);
		//Draw Friendly Torso arc
		graphics.drawArc(theGame.getOwnMech(myName).getLocation().getx()*4-10,theGame.getOwnMech(myName).getLocation().gety()*4-10,20,20,theGame.getOwnMech(myName).getTorsoFacing()-60,120);
		//Draw Friendly facing arc
		graphics.fillArc(theGame.getOwnMech(myName).getLocation().getx()*4-15,theGame.getOwnMech(myName).getLocation().gety()*4-15,30,30,theGame.getOwnMech(myName).getFacing()-5,10);

		//Check if can see other mech, if can, draw for enemy mech as well.
		if (tempMap.lineOfSight(theGame.getOwnMech(myName), theGame.getOtherMech(myName))) {
			graphics.setColor(new Color(255,102,102));
			graphics.fillOval(theGame.getOtherMech(myName).getLocation().getx()*4-5,theGame.getOtherMech(myName).getLocation().gety()*4-5,10,10);
			graphics.drawArc(theGame.getOtherMech(myName).getLocation().getx()*4-10,theGame.getOtherMech(myName).getLocation().gety()*4-10,20,20,theGame.getOtherMech(myName).getTorsoFacing()-60,120);
			graphics.fillArc(theGame.getOtherMech(myName).getLocation().getx()*4-15,theGame.getOtherMech(myName).getLocation().gety()*4-15,30,30,theGame.getOtherMech(myName).getFacing()-5,10);
		}

		graphics.setColor(Color.black);
		//Draw Clicked Point
		graphics.fillOval(curX-2,curY-2,4,4);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
