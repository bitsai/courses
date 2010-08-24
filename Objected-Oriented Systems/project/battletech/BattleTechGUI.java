package battletech;

import java.awt.*;
import java.awt.Color;
import javax.swing.border.*;
import javax.swing.BorderFactory;
import java.awt.Container;
import java.awt.Insets;
import java.util.*;
import javax.swing.*;
import java.rmi.*;
import gameclient.ClientModule;
import gameserver.ServerModule;
import java.awt.GridLayout;

/*
 * battleGUI.java
 *
 * Created on October 20, 2002, 8:23 PM
 */

/**
 *
 * @author  Fred
 */

/** This class represents the BattleTech client-side GUI. */

public class BattleTechGUI extends javax.swing.JPanel implements ClientModule
{
	BattleTechRemote theGame;

	String myName;
	JFrame ownerFrame;
	Vector weaponButtons = new Vector();

	MechRemote ownMech;
	BodyPartsRemote bps;
	ComponentsRemote comps;
	PhysicalAttacksRemote pas;
	WeaponsRemote weaps;

	MechRemote otherMech;
	BodyPartsRemote otherbps;
	ComponentsRemote othercomps;

	boolean myTurn = false;

	/** Returns the name of the player of this BattleTech GUI. */
	public String getPlayerName() { return myName; }

	/** Initializes this BattleTech GUI. */
	public void setGameInfo(JFrame myOwner, ServerModule myServerModule, String playerName)
	{
		ownerFrame = myOwner;
		myName = playerName;
		theGame = (BattleTechRemote) myServerModule;

		try 
		{
			MechDialog dialog = new MechDialog(ownerFrame);

			theGame.initialize(dialog.getMapName(),myName,dialog.getMechName());

			while (!theGame.isInitialized()) 
			{
				Thread.sleep(1000);
			}

			ownMech = theGame.getOwnMech(myName);
			bps = ownMech.getBodyParts();
			comps = ownMech.getComponents();
			pas = ownMech.getPhysicalAttacks();
			weaps = ownMech.getWeapons();

			otherMech = theGame.getOtherMech(myName);
			otherbps = otherMech.getBodyParts();
			othercomps = otherMech.getComponents();

			initComponents();
			BattleTechThread updateThread = new BattleTechThread(this, theGame);
			updateThread.start();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void initComponents() throws java.rmi.RemoteException 
	{//GEN-BEGIN:initComponents
		setSize(650,500);
		setMinimumSize(new java.awt.Dimension(650,500));
		setLayout(null);

        moveButton = new javax.swing.JButton();
        endTurnButton = new javax.swing.JButton();
        tabPanel = new javax.swing.JTabbedPane();
        mechPanel = new javax.swing.JPanel();

        mech1Panel = new javax.swing.JPanel();
        panelmech1Rleg = new javax.swing.JPanel();
        panelmech1RRtorso = new javax.swing.JPanel();
        panelmech1RLtorso = new javax.swing.JPanel();
        panelmech1RCtorso = new javax.swing.JPanel();
        panelmech1Head = new javax.swing.JPanel();
        panelmech1Larm = new javax.swing.JPanel();
        panelmech1Rarm = new javax.swing.JPanel();
        panelmech1Lleg = new javax.swing.JPanel();
        panelmech1FLtorso = new javax.swing.JPanel();
        panelmech1FCtorso = new javax.swing.JPanel();
        panelmech1FRtorso = new javax.swing.JPanel();

        mech2Panel = new javax.swing.JPanel();
        panelmech2Head = new javax.swing.JPanel();
        panelmech2Larm = new javax.swing.JPanel();
        panelmech2FLtorso = new javax.swing.JPanel();
        panelmech2FCtorso = new javax.swing.JPanel();
        panelmech2FRtorso = new javax.swing.JPanel();
        panelmech2Rarm = new javax.swing.JPanel();
        panelmech2Lleg = new javax.swing.JPanel();
        panelmech2Rleg = new javax.swing.JPanel();
        panelmech2RLtorso = new javax.swing.JPanel();
        panelmech2RCtorso = new javax.swing.JPanel();
        panelmech2RRtorso = new javax.swing.JPanel();

        weapon1Panel = new javax.swing.JPanel();
	weapon1Panel.setBounds(0,0,220,400);

        weapon2Panel = new javax.swing.JPanel();
	weapon2Panel.setBounds(0,0,220,400);

        statusText = new javax.swing.JTextArea();
        statusScrollPane = new javax.swing.JScrollPane(statusText);
        mech1ScrollPane = new javax.swing.JScrollPane(weapon1Panel);
        mech2ScrollPane = new javax.swing.JScrollPane(weapon2Panel);

        moveButton.setText("Move");
        moveButton.setAlignmentX(5.0F);
        moveButton.setAlignmentY(5.0F);
        moveButton.addActionListener(new java.awt.event.ActionListener() 
	{
		public void actionPerformed(java.awt.event.ActionEvent evt) 
		{
			try 
			{
				moveButtonActionPerformed(evt);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
        });

        add(moveButton);
        moveButton.setBounds(400, 430, 110, 30);

        endTurnButton.setText("End Turn");
        endTurnButton.addActionListener(new java.awt.event.ActionListener() 
	{
		public void actionPerformed(java.awt.event.ActionEvent evt) 
		{
			try
			{
		                endTurnButtonActionPerformed(evt);
		                endTurnButton.setEnabled(false);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
        });
        add(endTurnButton);
        endTurnButton.setBounds(510, 430, 110, 30);

		//Add Status Tab Panel
        mechPanel.setLayout(null);
		status1Add();
        tabPanel.addTab("STAT", mech1Panel);
		mech1Panel.setBounds(0,0,200,360);
		status2Add();
		tabPanel.addTab("ENEMY", mech2Panel);
		mech2Panel.setBounds(0,0,200,360);

		//Add Mech1 Tab Panel
		weapon1Panel.setLayout(null);
		mech1moveAdd();
		mech1weaponsAdd();
		mech1ScrollPane.setBounds(0,0,200,360);
        tabPanel.addTab("ACT", mech1ScrollPane);

		//Add Mech2 Tab Panel
		weapon2Panel.setLayout(null);
		//mech2moveAdd();
		mech2weaponsAdd();
		mech2ScrollPane.setBounds(0,0,200,360);
        tabPanel.addTab("WEAP", mech2ScrollPane);


        this.add(tabPanel);
        tabPanel.setBounds(400, 0, 220, 430);

        statusText.setText("");
	statusText.setEditable(false);
        this.add(statusScrollPane);
        statusScrollPane.setBounds(0, 400, 400, 60);

		mapPanel = new mapPaneltype(theGame, myName);
		this.add(mapPanel);
		mapPanel.setBounds(0,0,400,400);

		setVisible(true);

        //pack();
    }//GEN-END:initComponents

	/** Creates the status displays for player's own Mech. */
    public void status1Add() throws java.rmi.RemoteException{
		Container contentPane = this;
		Insets insets = contentPane.getInsets();

        mech1Panel.setLayout(null);

        mech1Panel.setBackground(new java.awt.Color(204, 204, 255));

		JLabel mech1RearLabel = new JLabel("Rear/IS");
		mech1Panel.add(mech1RearLabel);
		mech1RearLabel.setBounds(130,0,100,15);

		insets = mech1Panel.getInsets();

		panelmech1RlegIS = new JPanel();
        panelmech1RlegIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RlegIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RlegIS);
        panelmech1RlegIS.setBounds(75+insets.left, 105+insets.top, 10, 40);
        panelmech1Rleg.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1Rleg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1Rleg);
        panelmech1Rleg.setBounds(70+insets.left, 100+insets.top, 20, 50);

		panelmech1RRtorsoIS= new JPanel();
        panelmech1RRtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RRtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RRtorsoIS);
        panelmech1RRtorsoIS.setBounds(175+insets.left, 25+insets.top, 10, 40);
        panelmech1RRtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RRtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RRtorso);
        panelmech1RRtorso.setBounds(170+insets.left, 20+insets.top, 20, 50);

		panelmech1RLtorsoIS= new JPanel();
        panelmech1RLtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RLtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RLtorsoIS);
        panelmech1RLtorsoIS.setBounds(135+insets.left, 25+insets.top, 10, 40);
        panelmech1RLtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RLtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RLtorso);
        panelmech1RLtorso.setBounds(130+insets.left, 20+insets.top, 20, 50);

        panelmech1RCtorsoIS= new JPanel();
 		panelmech1RCtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RCtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RCtorsoIS);
        panelmech1RCtorsoIS.setBounds(155+insets.left, 25+insets.top, 10, 40);
        panelmech1RCtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RCtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RCtorso);
        panelmech1RCtorso.setBounds(150+insets.left, 20+insets.top, 20, 50);

		panelmech1HeadIS = new JPanel();
        panelmech1HeadIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1HeadIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1HeadIS);
        panelmech1HeadIS.setBounds(55+insets.left, 15+insets.top, 20, 20);
        panelmech1Head.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1Head.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1Head);
        panelmech1Head.setBounds(50+insets.left, 10+insets.top, 30, 30);

		panelmech1LarmIS = new JPanel();
        panelmech1LarmIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1LarmIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1LarmIS);
        panelmech1LarmIS.setBounds(25+insets.left, 45+insets.top, 10, 40);
        panelmech1Larm.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1Larm.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1Larm);
        panelmech1Larm.setBounds(20+insets.left, 40+insets.top, 20, 50);

		panelmech1RarmIS = new JPanel();
        panelmech1RarmIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1RarmIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1RarmIS);
        panelmech1RarmIS.setBounds(95+insets.left, 45+insets.top, 10, 40);
        panelmech1Rarm.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1Rarm.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1Rarm);
        panelmech1Rarm.setBounds(90+insets.left, 40+insets.top, 20, 50);

		panelmech1LlegIS = new JPanel();
        panelmech1LlegIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1LlegIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1LlegIS);
        panelmech1LlegIS.setBounds(45+insets.left, 105+insets.top, 10, 40);
        panelmech1Lleg.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1Lleg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1Lleg);
        panelmech1Lleg.setBounds(40+insets.left, 100+insets.top, 20, 50);

        panelmech1FLtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1FLtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1FLtorso);
        panelmech1FLtorso.setBounds(50+insets.left, 40+insets.top, 10, 50);

        panelmech1FCtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1FCtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1FCtorso);
        panelmech1FCtorso.setBounds(60+insets.left, 40+insets.top, 10, 50);

        panelmech1FRtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech1FRtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech1Panel.add(panelmech1FRtorso);
        panelmech1FRtorso.setBounds(70+insets.left, 40+insets.top, 10, 50);

	        mechWarrior1GunneryLabel = new JLabel("Gunnery: " + ownMech.getMW().getGunnery());
		mech1Panel.add(mechWarrior1GunneryLabel);
		mechWarrior1GunneryLabel.setBounds(0,355,80,15);
		mechWarrior1PilotingLabel = new JLabel("Piloting: " + ownMech.getMW().getPiloting());
		mech1Panel.add(mechWarrior1PilotingLabel);
		mechWarrior1PilotingLabel.setBounds(0,370,80,15);
		mechWarrior1DamageLabel = new JLabel("Damage: " + ownMech.getMW().getDamage());
		mech1Panel.add(mechWarrior1DamageLabel);
		mechWarrior1DamageLabel.setBounds(0,385,80,15);

		mech1EngineLabel = new JLabel("Engine Hits: " + comps.getEngineHits());
		mech1Panel.add(mech1EngineLabel);
		mech1EngineLabel.setBounds(80,340,100,15);
		mech1GyroLabel = new JLabel("Gyro Hits: " + comps.getGyroHits());
		mech1Panel.add(mech1GyroLabel);
		mech1GyroLabel.setBounds(80,355,100,15);
		mech1SensorsLabel = new JLabel("Sensors Hits: " + comps.getSensorsHits());
		mech1Panel.add(mech1SensorsLabel);
		mech1SensorsLabel.setBounds(80,370,100,15);
		mech1LifeSupportLabel = new JLabel("Life Sup Hits: " + comps.getLifeSupportHits());
		mech1Panel.add(mech1LifeSupportLabel);
		mech1LifeSupportLabel.setBounds(80,385,100,15);

		mech1HeatLabel = new JLabel("Heat: " + ownMech.getCurrentHeat());
		mech1Panel.add(mech1HeatLabel);
		mech1HeatLabel.setBounds(0,325,80,15);
		mech1HeatSinkLabel = new JLabel("HeatSink: " + ownMech.getHeatSinks());
		mech1Panel.add(mech1HeatSinkLabel);
		mech1HeatSinkLabel.setBounds(0,340,80,15);

		mech1ProneLabel = new JLabel("Not Prone");
		if (ownMech.isProne()) {
			mech1ProneLabel.setText("Prone");
		} else {
			mech1ProneLabel.setText("Not Prone");
		}
		mech1Panel.add(mech1ProneLabel);
		mech1ProneLabel.setBounds(0,310,80,15);

		mech1ShutdownLabel = new JLabel();
		if (ownMech.isShutdown()) {
			mech1ShutdownLabel.setText("ShutDown");
		} else {
			mech1ShutdownLabel.setText("Not ShutDown");
		}
		mech1Panel.add(mech1ShutdownLabel);
		mech1ShutdownLabel.setBounds(0,295,80,15);


		mech1ActiveLabel = new JLabel();
		if (ownMech.isActive()) {
			mech1ActiveLabel.setText("Active");
		} else {
			mech1ActiveLabel.setText("Not Active");
		}
		mech1Panel.add(mech1ActiveLabel);
		mech1ActiveLabel.setBounds(0,280,80,15);

		mech1WhoseTurnLabel = new JLabel();
		if (theGame.getWhoseTurn().equalsIgnoreCase(myName)) {
			mech1WhoseTurnLabel.setText("Your Turn");
		} else {
			mech1WhoseTurnLabel.setText("Their Turn");
		}
		mech1Panel.add(mech1WhoseTurnLabel);
		mech1WhoseTurnLabel.setBounds(0,265,80,15);

		mech1ElevationLabel = new JLabel("Elevation: " + ownMech.getElevation());
		mech1Panel.add(mech1ElevationLabel);
		mech1ElevationLabel.setBounds(0,250,80,15);

		mech1MovementPointsLabel = new JLabel("Move Left: " + ownMech.getMovementPoints());
		mech1Panel.add(mech1MovementPointsLabel);
		mech1MovementPointsLabel.setBounds(80,325,100,15);


	}

	/** Creates the status displays for player's opponent's Mech. */
	public void status2Add() throws java.rmi.RemoteException{
		Container contentPane = this;
		Insets insets = contentPane.getInsets();

        mech2Panel.setLayout(null);

        mech2Panel.setBackground(new java.awt.Color(255, 204, 204));

		JLabel mech2RearLabel = new JLabel("Rear/IS");
		mech2Panel.add(mech2RearLabel);
		mech2RearLabel.setBounds(130,0,100,15);


		insets = mech2Panel.getInsets();

		panelmech2RlegIS = new JPanel();
        panelmech2RlegIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RlegIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RlegIS);
        panelmech2RlegIS.setBounds(75+insets.left, 105+insets.top, 10, 40);
        panelmech2Rleg.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2Rleg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2Rleg);
        panelmech2Rleg.setBounds(70+insets.left, 100+insets.top, 20, 50);

		panelmech2RRtorsoIS= new JPanel();
        panelmech2RRtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RRtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RRtorsoIS);
        panelmech2RRtorsoIS.setBounds(175+insets.left, 25+insets.top, 10, 40);
        panelmech2RRtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RRtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RRtorso);
        panelmech2RRtorso.setBounds(170+insets.left, 20+insets.top, 20, 50);

		panelmech2RLtorsoIS= new JPanel();
        panelmech2RLtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RLtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RLtorsoIS);
        panelmech2RLtorsoIS.setBounds(135+insets.left, 25+insets.top, 10, 40);
        panelmech2RLtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RLtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RLtorso);
        panelmech2RLtorso.setBounds(130+insets.left, 20+insets.top, 20, 50);

        panelmech2RCtorsoIS= new JPanel();
 		panelmech2RCtorsoIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RCtorsoIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RCtorsoIS);
        panelmech2RCtorsoIS.setBounds(155+insets.left, 25+insets.top, 10, 40);
        panelmech2RCtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RCtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RCtorso);
        panelmech2RCtorso.setBounds(150+insets.left, 20+insets.top, 20, 50);

		panelmech2HeadIS = new JPanel();
        panelmech2HeadIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2HeadIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2HeadIS);
        panelmech2HeadIS.setBounds(55+insets.left, 15+insets.top, 20, 20);
        panelmech2Head.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2Head.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2Head);
        panelmech2Head.setBounds(50+insets.left, 10+insets.top, 30, 30);

		panelmech2LarmIS = new JPanel();
        panelmech2LarmIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2LarmIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2LarmIS);
        panelmech2LarmIS.setBounds(25+insets.left, 45+insets.top, 10, 40);
        panelmech2Larm.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2Larm.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2Larm);
        panelmech2Larm.setBounds(20+insets.left, 40+insets.top, 20, 50);

		panelmech2RarmIS = new JPanel();
        panelmech2RarmIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2RarmIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2RarmIS);
        panelmech2RarmIS.setBounds(95+insets.left, 45+insets.top, 10, 40);
        panelmech2Rarm.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2Rarm.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2Rarm);
        panelmech2Rarm.setBounds(90+insets.left, 40+insets.top, 20, 50);

		panelmech2LlegIS = new JPanel();
        panelmech2LlegIS.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2LlegIS.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2LlegIS);
        panelmech2LlegIS.setBounds(45+insets.left, 105+insets.top, 10, 40);
        panelmech2Lleg.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2Lleg.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2Lleg);
        panelmech2Lleg.setBounds(40+insets.left, 100+insets.top, 20, 50);

        panelmech2FLtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2FLtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2FLtorso);
        panelmech2FLtorso.setBounds(50+insets.left, 40+insets.top, 10, 50);

        panelmech2FCtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2FCtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2FCtorso);
        panelmech2FCtorso.setBounds(60+insets.left, 40+insets.top, 10, 50);

        panelmech2FRtorso.setBackground(new java.awt.Color(0, 0, 0));
        panelmech2FRtorso.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        mech2Panel.add(panelmech2FRtorso);
        panelmech2FRtorso.setBounds(70+insets.left, 40+insets.top, 10, 50);

        mechWarrior2GunneryLabel = new JLabel("Gunnery: " + otherMech.getMW().getGunnery());
        mech2Panel.add(mechWarrior2GunneryLabel);
		mechWarrior2GunneryLabel.setBounds(0,355,80,15);
		mechWarrior2PilotingLabel = new JLabel("Piloting: " + otherMech.getMW().getPiloting());
		mech2Panel.add(mechWarrior2PilotingLabel);
		mechWarrior2PilotingLabel.setBounds(0,370,80,15);
		mechWarrior2DamageLabel = new JLabel("Damage: " + otherMech.getMW().getDamage());
		mech2Panel.add(mechWarrior2DamageLabel);
		mechWarrior2DamageLabel.setBounds(0,385,80,15);



		mech2EngineLabel = new JLabel("Engine Hits: " + othercomps.getEngineHits());
		mech2Panel.add(mech2EngineLabel);
		mech2EngineLabel.setBounds(80,340,100,15);
		mech2GyroLabel = new JLabel("Gyro Hits: " + othercomps.getGyroHits());
		mech2Panel.add(mech2GyroLabel);
		mech2GyroLabel.setBounds(80,355,100,15);
		mech2SensorsLabel = new JLabel("Sensors Hits: " + othercomps.getSensorsHits());
		mech2Panel.add(mech2SensorsLabel);
		mech2SensorsLabel.setBounds(80,370,100,15);
		mech2LifeSupportLabel = new JLabel("Life Sup Hits: " + othercomps.getLifeSupportHits());
		mech2Panel.add(mech2LifeSupportLabel);
		mech2LifeSupportLabel.setBounds(80,385,100,15);

		mech2HeatLabel = new JLabel("Heat: " + otherMech.getCurrentHeat());
		mech2Panel.add(mech2HeatLabel);
		mech2HeatLabel.setBounds(0,325,80,15);
		mech2HeatSinkLabel = new JLabel("HeatSink: " + otherMech.getHeatSinks());
		mech2Panel.add(mech2HeatSinkLabel);
		mech2HeatSinkLabel.setBounds(0,340,80,15);

		mech2ProneLabel = new JLabel();
		if (otherMech.isProne()) {
			mech2ProneLabel.setText("Prone");
		} else {
			mech2ProneLabel.setText("Not Prone");
		}
		mech2Panel.add(mech2ProneLabel);
		mech2ProneLabel.setBounds(0,310,80,15);

		mech2ShutdownLabel = new JLabel();
		if (otherMech.isShutdown()) {
			mech2ShutdownLabel.setText("ShutDown");
		} else {
			mech2ShutdownLabel.setText("Not ShutDown");
		}
		mech2Panel.add(mech2ShutdownLabel);
		mech2ShutdownLabel.setBounds(0,295,80,15);


		mech2ActiveLabel = new JLabel();
		if (otherMech.isActive()) {
			mech2ActiveLabel.setText("Active");
		} else {
			mech2ActiveLabel.setText("Not Active");
		}
		mech2Panel.add(mech2ActiveLabel);
		mech2ActiveLabel.setBounds(0,280,80,15);

		mech2ElevationLabel = new JLabel("Elevation: " + otherMech.getElevation());
		mech2Panel.add(mech2ElevationLabel);
		mech2ElevationLabel.setBounds(0,250,80,15);


		mech2MovementPointsLabel = new JLabel("Move Left: " + otherMech.getMovementPoints());
		mech2Panel.add(mech2MovementPointsLabel);
		mech2MovementPointsLabel.setBounds(80,325,100,15);
	}


    private void mech1moveAdd() throws java.rmi.RemoteException{
		javax.swing.JButton tempButton;

		String[] moveStrings = {"Set Movement Mode","Standing","Walking","Running","Jumping"};


		mech1movement = new javax.swing.JComboBox(moveStrings);
		mech1movement.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = "";
					javax.swing.JComboBox cb = (javax.swing.JComboBox)evt.getSource();
					String moveMode = (String) cb.getSelectedItem();

					if (moveMode.equals("Standing")) 
					{
						output = ownMech.setMovementMode(0);
					} 
					else if (moveMode.equals("Walking")) 
					{
						output = ownMech.setMovementMode(1);
					} 
					else if (moveMode.equals("Running")) 
					{
						output = ownMech.setMovementMode(2);
					} 
					else if (moveMode.equals("Jumping")) 
					{
						output = ownMech.setMovementMode(3);
					}

					statusText.append(output + "\n");

					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));

					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

		});

		JLabel mech1turnMech = new JLabel("Turn Mech");
		weapon1Panel.add(mech1turnMech);
		mech1turnMech.setBounds(10,35,100,15);

		mech1turnToward = new JButton();
		mech1turnToward.setText("Toward");
		mech1turnToward.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					statusText.append(ownMech.turnToward(mapPanel.getPoint())+"\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1turnToward);
		mech1turnToward.setBounds(10,50,100,20);

		mech1turnAway = new JButton();
		mech1turnAway.setText("Away");
		mech1turnAway.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					statusText.append(ownMech.turnAway(mapPanel.getPoint())+"\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1turnAway);
		mech1turnAway.setBounds(10,70,100,20);

		JLabel mech1torso = new JLabel("Torso");
		weapon1Panel.add(mech1torso);
		mech1torso.setBounds(10,90,100,15);

		mech1torsoLeft = new JButton();
		mech1torsoLeft.setText("<");
		mech1torsoLeft.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					statusText.append(ownMech.turnTorso(15)+"\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1torsoLeft);
		mech1torsoLeft.setBounds(10,105,50,20);

		mech1torsoRight = new JButton();
		mech1torsoRight.setText(">");
		mech1torsoRight.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					statusText.append(ownMech.turnTorso(-15)+"\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1torsoRight);
		mech1torsoRight.setBounds(60,105,50,20);

		JLabel mech1SD = new JLabel("Stand/Drop");
		weapon1Panel.add(mech1SD);
		mech1SD.setBounds(110,35,100,15);

		mech1stand = new JButton();
		mech1stand.setText("stand");
		mech1stand.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = ownMech.stand();
					statusText.append(output + "\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1stand);
		mech1stand.setBounds(110,50,90,20);

		mech1drop = new JButton();
		mech1drop.setText("drop");
		mech1drop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = ownMech.drop();
					statusText.append(output + "\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(mech1drop);
		mech1drop.setBounds(110,70,90,20);


		weapon1Panel.add(mech1movement);
		mech1movement.setBounds(10,10,190,20);

	}

	private void mech1weaponsAdd() throws java.rmi.RemoteException{
		final int weaponoffset = 140;

		javax.swing.JButton tempButton;
		JLabel mech1weaponLabel = new JLabel("Weapons");
		weapon1Panel.add(mech1weaponLabel);
		mech1weaponLabel.setBounds(10,125,100,15);


		for (int i=0; i < weaps.getSize(); i++) 
		{
			tempButton = new javax.swing.JButton();
			tempButton.setText(weaps.getWeapon(i).toString());
			weaponButtons.add(tempButton);
			final JButton tempWeaponButton = tempButton;
			final WeaponsRemote tempWeapons = weaps;
			final int weaponNum = i;
			tempButton.addActionListener(new java.awt.event.ActionListener() 
			{
				public void actionPerformed(java.awt.event.ActionEvent evt)
				{
					try
					{
						String output = tempWeapons.fireWeapon(weaponNum, otherMech);
						statusText.append(output + "\n");
						
						Dimension boundary = statusText.getSize(null);
						statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
						
						updateEverything();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			});

			weapon1Panel.add(tempButton);
			tempButton.setBounds(10,weaponoffset+i*20,190,20);
		}
		tempButton = new JButton();
		tempButton.setText("Punch with Right Arm!");
		weaponButtons.add(tempButton);
		final JButton tempRPunchButton = tempButton;
		tempButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = pas.punch(otherMech, 0);
					statusText.append(output + "\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
		});
		weapon1Panel.add(tempButton);
		tempButton.setBounds(10,0+weaponoffset+weaps.getSize()*20,190,20);

		tempButton = new JButton();
		tempButton.setText("Punch with Left Arm!");
		weaponButtons.add(tempButton);
		final JButton tempLPunchButton = tempButton;
		tempButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = pas.punch(otherMech, 1);
					statusText.append(output + "\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
		});
		weapon1Panel.add(tempButton);
		tempButton.setBounds(10,20+weaponoffset+weaps.getSize()*20,190,20);

		tempButton = new JButton();
		tempButton.setText("Kick!");
		weaponButtons.add(tempButton);
		final JButton tempKickButton = tempButton;
		tempButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt){
				try
				{
					String output = pas.kick(otherMech);
					statusText.append(output + "\n");
					
					Dimension boundary = statusText.getSize(null);
					statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
					
					updateEverything();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		weapon1Panel.add(tempButton);
		tempButton.setBounds(10,40+weaponoffset+weaps.getSize()*20,190,20);

	}
	private void mech2weaponsAdd() throws java.rmi.RemoteException{
		final int weaponoffset = 130;

		javax.swing.JLabel tempButton;

		JLabel mech2weaponLabel = new JLabel("Weapons");
		weapon2Panel.add(mech2weaponLabel);
		mech2weaponLabel.setBounds(10,100,100,20);

		for (int i=0; i < weaps.getSize(); i++) 
		{
			tempButton = new javax.swing.JLabel();
			tempButton.setText(weaps.getWeapon(i).getName() + " Heat: " + weaps.getWeapon(i).getHeat());
			weapon2Panel.add(tempButton);
			tempButton.setBounds(10,weaponoffset+i*20,190,20);
		}
	}

	/** Refreshes the entire GUI. */
	public synchronized void updateEverything() throws java.rmi.RemoteException
	{
		if (!theGame.getWhoseTurn().equalsIgnoreCase(myName))
		{
			String input = theGame.getOutput();
			if (!input.equalsIgnoreCase(""))
			{ statusText.append(input); }

			Dimension boundary = statusText.getSize(null);
			statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
		}

		if (theGame.getWhoseTurn().equalsIgnoreCase(myName) && !myTurn)
		{
			myTurn = true;

			statusText.append("Start of turn!\n");

			Dimension boundary = statusText.getSize(null);
			statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
			
			mech1movement.setSelectedIndex(0);
		}

		updateButtons();
		updateStatusMech1();
		updateStatusMech2();
		updateDamageMech1();
		updateDamageMech2();
		updateLabelMech1();
		updateLabelMech2();
		mapPanel.repaint();
	}

	/** Refreshes all buttons. */
	public void updateButtons() throws java.rmi.RemoteException 
	{
		for (int i=0; i < (weaponButtons.size()-3); i++) 
		{
			if (i >= weaps.getSize())
			{ ((JButton)weaponButtons.get(i)).setText("Weapon Destroyed!"); }
			else
			{ ((JButton)weaponButtons.get(i)).setText(weaps.getWeapon(i).toString()); }
		}

		if (theGame.getWhoseTurn().equalsIgnoreCase(myName)) 
		{
			endTurnButton.setEnabled(true);
		} 
		else 
		{
			endTurnButton.setEnabled(false);
		}
	}

	/** Refreshes status display for player's own Mech. */
	public void updateStatusMech1() throws java.rmi.RemoteException {
		if (ownMech.isProne()) {
			mech1ProneLabel.setForeground(Color.red);
			mech1ProneLabel.setText("Prone");
		} else {
			mech1ProneLabel.setForeground(Color.black);
			mech1ProneLabel.setText("Not Prone");
		}
		if (ownMech.isShutdown()) {
			mech1ShutdownLabel.setForeground(Color.red);
			mech1ShutdownLabel.setText("ShutDown");
		} else {
			mech1ShutdownLabel.setForeground(Color.black);
			mech1ShutdownLabel.setText("Not ShutDown");
		}
		if (ownMech.isActive()) {

			mech1ActiveLabel.setText("Active");
		} else {
			mech1ActiveLabel.setText("Not Active");
		}
		if (theGame.getWhoseTurn().equals(myName)) {
			mech1WhoseTurnLabel.setText("Your Turn");
		} else {
			mech1WhoseTurnLabel.setText("Their Turn");
		}
	}

	/** Refreshes status display for player's opponent's Mech. */
	public void updateStatusMech2() throws java.rmi.RemoteException {
		if (otherMech.isProne()) {
			mech2ProneLabel.setForeground(Color.red);
			mech2ProneLabel.setText("Prone");
		} else {
			mech2ProneLabel.setForeground(Color.black);
			mech2ProneLabel.setText("Not Prone");
		}
		if (otherMech.isShutdown()) {
			mech2ShutdownLabel.setForeground(Color.red);
			mech2ShutdownLabel.setText("ShutDown");
		} else {
			mech2ShutdownLabel.setForeground(Color.black);
			mech2ShutdownLabel.setText("Not ShutDown");
		}
		if (otherMech.isActive()) {
			mech2ActiveLabel.setText("Active");
		} else {
			mech2ActiveLabel.setText("Not Active");
		}
	}

	private void updateLabelMech1() throws java.rmi.RemoteException
	{
	        mechWarrior1GunneryLabel.setText("Gunnery: "+ownMech.getMW().getGunnery());
		mechWarrior1PilotingLabel.setText("Piloting: "+ownMech.getMW().getPiloting());
		mechWarrior1DamageLabel.setText("Damage: "+ownMech.getMW().getDamage());
		mech1EngineLabel.setText("Engine Hits: "+comps.getEngineHits());
		mech1GyroLabel.setText("Gyro Hits: "+comps.getGyroHits());
		mech1SensorsLabel.setText("Sensors Hits: "+comps.getSensorsHits());
		mech1LifeSupportLabel.setText("Life Sup Hits: "+comps.getLifeSupportHits());
		mech1HeatLabel.setText("Heat: "+ownMech.getCurrentHeat());
		mech1HeatSinkLabel.setText("HeatSink: "+ownMech.getHeatSinks());
		mech1MovementPointsLabel.setText("Move Left: "+ownMech.getMovementPoints());
		mech1ElevationLabel.setText("Elevation: "+ownMech.getElevation());
	}
	private void updateLabelMech2() throws java.rmi.RemoteException{
        mechWarrior2GunneryLabel.setText("Gunnery: "+otherMech.getMW().getGunnery());
		mechWarrior2PilotingLabel.setText("Piloting: "+otherMech.getMW().getPiloting());
		mechWarrior2DamageLabel.setText("Damage: "+otherMech.getMW().getDamage());
		mech2EngineLabel.setText("Engine Hits: "+otherMech.getComponents().getEngineHits());
		mech2GyroLabel.setText("Gyro Hits: "+otherMech.getComponents().getGyroHits());
		mech2SensorsLabel.setText("Sensors Hits: "+otherMech.getComponents().getSensorsHits());
		mech2LifeSupportLabel.setText("Life Sup Hits: "+otherMech.getComponents().getLifeSupportHits());
		mech2HeatLabel.setText("Heat: "+otherMech.getCurrentHeat());
		mech2HeatSinkLabel.setText("HeatSink: "+otherMech.getHeatSinks());
		mech2MovementPointsLabel.setText("Move Left: "+otherMech.getMovementPoints());
		mech2ElevationLabel.setText("Elevation: "+otherMech.getElevation());
	}

	private void updateDamageMech1() throws java.rmi.RemoteException
	{
		panelmech1Head.setBackground(getColor(bps.getArmor("Head","Front"),bps.getMaxArmor("Head","Front")));
		panelmech1HeadIS.setBackground(getColor(bps.getIS("Head"),bps.getMaxIS("Head")));

		panelmech1FRtorso.setBackground(getColor(bps.getArmor("RTorso","Front"),bps.getMaxArmor("RTorso","Front")));
		panelmech1RRtorso.setBackground(getColor(bps.getArmor("RTorso","Rear"),bps.getMaxArmor("RTorso","Rear")));
		panelmech1RRtorsoIS.setBackground(getColor(bps.getIS("RTorso"),bps.getMaxIS("RTorso")));
		panelmech1FCtorso.setBackground(getColor(bps.getArmor("CTorso","Front"),bps.getMaxArmor("CTorso","Front")));
		panelmech1RCtorso.setBackground(getColor(bps.getArmor("CTorso","Rear"),bps.getMaxArmor("CTorso","Rear")));
		panelmech1RCtorsoIS.setBackground(getColor(bps.getIS("CTorso"),bps.getMaxIS("CTorso")));
		panelmech1FLtorso.setBackground(getColor(bps.getArmor("LTorso","Front"),bps.getMaxArmor("LTorso","Front")));
		panelmech1RLtorso.setBackground(getColor(bps.getArmor("LTorso","Rear"),bps.getMaxArmor("LTorso","Rear")));
		panelmech1RLtorsoIS.setBackground(getColor(bps.getIS("LTorso"),bps.getMaxIS("LTorso")));
		panelmech1Rarm.setBackground(getColor(bps.getArmor("RArm","Front"),bps.getMaxArmor("RArm","Front")));
		panelmech1RarmIS.setBackground(getColor(bps.getIS("RArm"),bps.getMaxIS("RArm")));
		panelmech1Larm.setBackground(getColor(bps.getArmor("LArm","Front"),bps.getMaxArmor("LArm","Front")));
		panelmech1LarmIS.setBackground(getColor(bps.getIS("LArm"),bps.getMaxIS("LArm")));
		panelmech1Rleg.setBackground(getColor(bps.getArmor("RLeg","Front"),bps.getMaxArmor("RLeg","Front")));
		panelmech1RlegIS.setBackground(getColor(bps.getIS("RLeg"),bps.getMaxIS("RLeg")));
		panelmech1Lleg.setBackground(getColor(bps.getArmor("LLeg","Front"),bps.getMaxArmor("LLeg","Front")));
		panelmech1LlegIS.setBackground(getColor(bps.getIS("LLeg"),bps.getMaxIS("LLeg")));
	}
	private void updateDamageMech2() throws java.rmi.RemoteException
	{
		panelmech2Head.setBackground(getColor(otherbps.getArmor("Head","Front"),otherbps.getMaxArmor("Head","Front")));
		panelmech2HeadIS.setBackground(getColor(otherbps.getIS("Head"),otherbps.getMaxIS("Head")));

		panelmech2FRtorso.setBackground(getColor(otherbps.getArmor("RTorso","Front"),otherbps.getMaxArmor("RTorso","Front")));
		panelmech2RRtorso.setBackground(getColor(otherbps.getArmor("RTorso","Rear"),otherbps.getMaxArmor("RTorso","Rear")));
		panelmech2RRtorsoIS.setBackground(getColor(otherbps.getIS("RTorso"),otherbps.getMaxIS("RTorso")));
		panelmech2FCtorso.setBackground(getColor(otherbps.getArmor("CTorso","Front"),otherbps.getMaxArmor("CTorso","Front")));
		panelmech2RCtorso.setBackground(getColor(otherbps.getArmor("CTorso","Rear"),otherbps.getMaxArmor("CTorso","Rear")));
		panelmech2RCtorsoIS.setBackground(getColor(otherbps.getIS("CTorso"),otherbps.getMaxIS("CTorso")));
		panelmech2FLtorso.setBackground(getColor(otherbps.getArmor("LTorso","Front"),otherbps.getMaxArmor("LTorso","Front")));
		panelmech2RLtorso.setBackground(getColor(otherbps.getArmor("LTorso","Rear"),otherbps.getMaxArmor("LTorso","Rear")));
		panelmech2RLtorsoIS.setBackground(getColor(otherbps.getIS("LTorso"),otherbps.getMaxIS("LTorso")));
		panelmech2Rarm.setBackground(getColor(otherbps.getArmor("RArm","Front"),otherbps.getMaxArmor("RArm","Front")));
		panelmech2RarmIS.setBackground(getColor(otherbps.getIS("RArm"),otherbps.getMaxIS("RArm")));
		panelmech2Larm.setBackground(getColor(otherbps.getArmor("LArm","Front"),otherbps.getMaxArmor("LArm","Front")));
		panelmech2LarmIS.setBackground(getColor(otherbps.getIS("LArm"),otherbps.getMaxIS("LArm")));
		panelmech2Rleg.setBackground(getColor(otherbps.getArmor("RLeg","Front"),otherbps.getMaxArmor("RLeg","Front")));
		panelmech2RlegIS.setBackground(getColor(otherbps.getIS("RLeg"),otherbps.getMaxIS("RLeg")));
		panelmech2Lleg.setBackground(getColor(otherbps.getArmor("LLeg","Front"),otherbps.getMaxArmor("LLeg","Front")));
		panelmech2LlegIS.setBackground(getColor(otherbps.getIS("LLeg"),otherbps.getMaxIS("LLeg")));
	}

	private Color getColor(int nowAmount, int maxAmount) throws java.rmi.RemoteException
	{
		if (maxAmount == 0) 
		{
			return new Color(0,0,0);
		}

		int tempAmount = java.lang.Math.round(255*3*nowAmount/maxAmount);
		if (tempAmount < 255) 
		{
			return new Color(tempAmount,0,0);
		} 
		else if (tempAmount < (255*2)) 
		{
			return new Color(255, tempAmount-255, 0);
		} 
		else 
		{
			return new Color((255*3)-tempAmount,255,0);
		}
	}

	private void endTurnButtonActionPerformed(java.awt.event.ActionEvent evt) throws java.rmi.RemoteException
	{//GEN-FIRST:event_endTurnButtonActionPerformed
        // Add your handling code here:
	    theGame.endTurn();
		myTurn = false;
		
		statusText.append("End of turn!\n");

		Dimension boundary = statusText.getSize(null);
		statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));

		for (int i=0; i < weaponButtons.size(); i++)
		{ 
			((JButton)weaponButtons.get(i)).setEnabled(true); 
		}
	}//GEN-LAST:event_endTurnButtonActionPerformed

	private void moveButtonActionPerformed(java.awt.event.ActionEvent evt) throws java.rmi.RemoteException
	{//GEN-FIRST:event_moveButtonActionPerformed
		try 
		{
	       	String output = ownMech.moveMech(mapPanel.getPoint());
			statusText.append(output + "\n");
			
			Dimension boundary = statusText.getSize(null);
			statusText.scrollRectToVisible(new Rectangle(1, boundary.height - 1, 1, 1));
			
			updateEverything();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}//GEN-LAST:event_moveButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea statusText;
    private javax.swing.JScrollPane statusScrollPane;

    private javax.swing.JScrollPane mech1ScrollPane;
    private javax.swing.JComboBox mech1movement;
    private JButton mech1turnToward;
    private JButton mech1turnAway;
    private JButton mech1torsoLeft;
    private JButton mech1torsoRight;
    private JButton mech1stand;
    private JButton mech1drop;

    private	JLabel mechWarrior1GunneryLabel;
	private JLabel mechWarrior1PilotingLabel;
	private JLabel mechWarrior1DamageLabel;
	private	JLabel mech1EngineLabel;
	private	JLabel mech1GyroLabel;
	private	JLabel mech1SensorsLabel;
	private	JLabel mech1LifeSupportLabel;
	private	JLabel mech1HeatLabel;
	private	JLabel mech1HeatSinkLabel;
	private	JLabel mech1MovementPointsLabel;
	private JLabel mech1ProneLabel;
	private JLabel mech1ShutdownLabel;
	private JLabel mech1ActiveLabel;
	private JLabel mech1WhoseTurnLabel;
	private JLabel mech1ElevationLabel;

    private javax.swing.JScrollPane mech2ScrollPane;
    private javax.swing.JComboBox mech2movement;
    private JButton mech2turnLeft;
    private JButton mech2turnRight;
    private JButton mech2torsoLeft;
    private JButton mech2torsoRight;
    private JButton mech2stand;
    private JButton mech2drop;

    private	JLabel mechWarrior2GunneryLabel;
	private JLabel mechWarrior2PilotingLabel;
	private JLabel mechWarrior2DamageLabel;
	private	JLabel mech2EngineLabel;
	private	JLabel mech2GyroLabel;
	private	JLabel mech2SensorsLabel;
	private	JLabel mech2LifeSupportLabel;
	private	JLabel mech2HeatLabel;
	private	JLabel mech2HeatSinkLabel;
	private	JLabel mech2MovementPointsLabel;
	private JLabel mech2ProneLabel;
	private JLabel mech2ShutdownLabel;
	private JLabel mech2ActiveLabel;
	private JLabel mech2ElevationLabel;

    private javax.swing.JPanel panelmech2RCtorso;
	private javax.swing.JPanel panelmech2RCtorsoIS;
    private javax.swing.JPanel panelmech1FLtorso;
    private javax.swing.JPanel panelmech1FRtorso;
    private javax.swing.JPanel panelmech2Larm;
    private javax.swing.JPanel panelmech2LarmIS;
    private javax.swing.JPanel panelmech1RCtorso;
    private javax.swing.JPanel panelmech1RCtorsoIS;
    private javax.swing.JPanel panelmech2FCtorso;
    private javax.swing.JPanel mech2Panel;
    private javax.swing.JTabbedPane tabPanel;
    private javax.swing.JPanel panelmech2Rarm;
    private javax.swing.JPanel panelmech2RarmIS;
    private javax.swing.JPanel panelmech1FCtorso;
    private javax.swing.JPanel panelmech2Lleg;
    private javax.swing.JPanel panelmech2LlegIS;
    private javax.swing.JPanel panelmech2Head;
    private javax.swing.JPanel panelmech2HeadIS;
    private javax.swing.JButton moveButton;
    private javax.swing.JPanel panelmech2Rleg;
    private javax.swing.JPanel panelmech2RlegIS;
    private javax.swing.JPanel panelmech1Larm;
    private javax.swing.JPanel panelmech1LarmIS;
    private javax.swing.JPanel panelmech1Rarm;
    private javax.swing.JPanel panelmech1RarmIS;
    private javax.swing.JPanel panelmech2RLtorso;
    private javax.swing.JPanel panelmech2RLtorsoIS;
    private javax.swing.JPanel panelmech1Lleg;
    private javax.swing.JPanel panelmech1LlegIS;
    private javax.swing.JPanel mech1Panel;
    private javax.swing.JPanel panelmech2RRtorso;
    private javax.swing.JPanel panelmech2RRtorsoIS;
    private javax.swing.JPanel panelmech1Head;
    private javax.swing.JPanel panelmech1HeadIS;
    private javax.swing.JPanel panelmech1Rleg;
    private javax.swing.JPanel panelmech1RlegIS;
    private javax.swing.JPanel panelmech1RLtorso;
    private javax.swing.JPanel panelmech1RLtorsoIS;
    private javax.swing.JButton endTurnButton;
    private javax.swing.JPanel panelmech2FLtorso;
    private javax.swing.JPanel weapon1Panel;
    private javax.swing.JPanel weapon2Panel;
    private javax.swing.JPanel mechPanel;
    private javax.swing.JPanel panelmech1RRtorso;
    private javax.swing.JPanel panelmech1RRtorsoIS;
    private javax.swing.JButton fireWeaponButton;
    private javax.swing.JPanel panelmech2FRtorso;
    private mapPaneltype mapPanel;
    // End of variables declaration//GEN-END:variables

}


class BattleTechThread extends Thread
{
	BattleTechGUI myGUI;
	BattleTechRemote myServer;

	public BattleTechThread(BattleTechGUI aGUI, BattleTechRemote aServer)
		{
		myGUI = aGUI;
		myServer = aServer;
		}


	public void run()
		{
		try
		{
			while (!myServer.isGameOver())
			{
				myGUI.updateEverything();
				Thread.sleep(2500);
			}
			myGUI.updateEverything();
		}

		catch ( Exception excep )
			{
				excep.printStackTrace();
			//--> make this more complex
			}
		}

}