package battletech;

import java.sql.*;
import java.util.*;

/** This class can create default Mechs (BattleMasters) and load a specified Mech from a database. */

public class MechMaker implements java.io.Serializable
{
	static Statement stmt;
	static boolean debug = false;
	static int uniquenum = 0;

	/** Connect to database. */
	public static void connect() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException

	{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://peregrine.cs.jhu.edu:3306/oosgroup3?user=even&password=F@tC@t";
		Connection conn = DriverManager.getConnection(url);
		stmt = conn.createStatement();
	}

	/** Return a Vector of Mech names. */
	public static Vector getMechNames() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException
	{
		connect();
		Vector mechnames = new Vector(20);
		String qry = "select mechs.name from mechs";
		ResultSet rs = stmt.executeQuery(qry);
		while (rs.next())
		{
			mechnames.add(rs.getString("name"));
		}
		return mechnames;
	}

	/** Create and return a BattleMaster Mech. */
	public static MechRemote sampleMech(BattleTechRemote game) throws java.rmi.RemoteException
	{
		MechRemote mech = new Mech("Battlemaster", 85, 340, 18, game);

	// BodyParts
		BodyPart head = new BodyPart("Head", 9, 0, 3, mech);
		BodyPart cTorso = new BodyPart("CTorso", 40, 11, 27, mech);
		BodyPart rTorso = new BodyPart("RTorso", 28, 8, 18, mech);
		BodyPart lTorso = new BodyPart("LTorso", 28, 8, 18, mech);
		BodyPart rArm = new BodyPart("RArm", 24, 0, 14, mech);
		BodyPart lArm = new BodyPart("LArm", 24, 0, 14, mech);
		BodyPart rLeg = new BodyPart("RLeg", 26, 0, 18, mech);
		BodyPart lLeg = new BodyPart("LLeg", 26, 0, 18, mech);

	// Default components

		Cockpit cockpit = new Cockpit(head, mech);
		Component lifeSupport = new Component("LifeSupport", "LifeSupport", 2, head, mech);
		Sensors sensors = new Sensors(head, mech);
		Gyro gyro = new Gyro(cTorso, mech);
		Engine engine = new Engine(cTorso, mech);

	// Weapons
		EnergyWeapon ppc1 = new EnergyWeapon("PPC-1", "PPC", 10, 10, 3, 6, 12, 18, 3, rArm, mech);
		EnergyWeapon mlaser1 = new EnergyWeapon("MLaser-1", "MLaser", 3, 5, 0, 3, 6, 9, 1, rTorso, mech);
		EnergyWeapon mlaser2 = new EnergyWeapon("MLaser-2", "MLaser", 3, 5, 0, 3, 6, 9, 1, rTorso, mech);
		EnergyWeapon mlaser3 = new EnergyWeapon("(R)MLaser-3", "MLaser", 3, 5, 0, 3, 6, 9, 1, rTorso, mech);
		EnergyWeapon mlaser4 = new EnergyWeapon("MLaser-4", "MLaser", 3, 5, 0, 3, 6, 9, 1, lTorso, mech);
		EnergyWeapon mlaser5 = new EnergyWeapon("MLaser-5", "MLaser", 3, 5, 0, 3, 6, 9, 1, lTorso, mech);
		EnergyWeapon mlaser6 = new EnergyWeapon("(R)MLaser-6", "MLaser", 3, 5, 0, 3, 6, 9, 1, lTorso, mech);
		ProjectileWeapon mgun1 = new ProjectileWeapon("MGun-1", "MGun", 0, 2, 0, 1, 2, 3, 1, lArm, mech);
		ProjectileWeapon mgun2 = new ProjectileWeapon("MGun-2", "MGun", 0, 2, 0, 1, 2, 3, 1, lArm, mech);
		MissileWeapon srm61 = new MissileWeapon("SRM6-1", "SRM6", 6, 2, 0, 3, 6, 9, 1, lTorso, mech);

	// Ammo
		Ammo mgunammo1 = new Ammo("MGunAmmo-1", "MGun", 2, 200, lTorso, mech);
		Ammo srm6ammo1 = new Ammo("SRM6Ammo-1", "SRM6", 2, 15, lTorso, mech);
		Ammo srm6ammo2 = new Ammo("SRM6Ammo-2", "SRM6", 2, 15, lTorso, mech);

		return mech;
	}

	/** Create and return a Mech based on specified Mech name. */
	public static MechRemote loadMech(String name, BattleTechRemote game) throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, java.rmi.RemoteException
	{
		MechRemote mech;
		BodyPart head,cTorso,rTorso,lTorso,rArm,lArm,rLeg,lLeg;


		connect();
		ResultSet rs = stmt.executeQuery("select * from mechs where name = '" + name + "'");

		// one result returned since name is a primary key

		if (!rs.first())
		{
			//System.err.println(name + " not found");
			throw new IllegalAccessException(name + " not found");
		}


		mech = new Mech("Battlemaster", 85, 340, 18, game);

		head = new BodyPart("Head",rs.getInt("armorHeadFront"),rs.getInt("armorHeadBack"), rs.getInt("armorHeadIS"),mech);
		lTorso = new BodyPart("LTorso",rs.getInt("armorSideTorsoFront"),rs.getInt("armorSideTorsoBack"),rs.getInt("armorSideTorsoIS"),mech);
		cTorso = new BodyPart("CTorso",rs.getInt("armorCenterTorsoFront"),rs.getInt("armorCenterTorsoBack"),rs.getInt("armorCenterTorsoIS"),mech);
		rTorso = new BodyPart("RTorso",rs.getInt("armorSideTorsoFront"),rs.getInt("armorSideTorsoBack"),rs.getInt("armorSideTorsoIS"),mech);
		lArm = new BodyPart("LArm",rs.getInt("armorArmFront"),rs.getInt("armorArmBack"),rs.getInt("armorArmIS"),mech);
		rArm = new BodyPart("RArm",rs.getInt("armorArmFront"),rs.getInt("armorArmBack"),rs.getInt("armorArmIS"),mech);
		lLeg = new BodyPart("LLeg",rs.getInt("armorLegFront"),rs.getInt("armorLegBack"),rs.getInt("armorLegIS"),mech);
		rLeg = new BodyPart("RLeg",rs.getInt("armorLegFront"),rs.getInt("armorLegBack"),rs.getInt("armorLegIS"),mech);

		Cockpit cockpit = new Cockpit(head, mech);
		Component lifeSupport = new Component("LifeSupport", "LifeSupport", 2, head, mech);
		Sensors sensors = new Sensors(head, mech);
		Gyro gyro = new Gyro(cTorso, mech);
		Engine engine = new Engine(cTorso, mech);

		BodyPart [] bodyParts = {head,lTorso,cTorso,rTorso,lArm,rArm,lLeg,rLeg};
		String [] names = {"objectsHead","objectsLT","objectsCT","objectsRT","objectsLA","objectsRA","objectsLL","objectsRL"};
		String [] componentStr = new String[8];

		for (int i=0;i<names.length;i++)
		{
			componentStr[i] = rs.getString(names[i]);
		}

		String locationStr;
		StringTokenizer st;
		String qry;
		String token;
		int tokennum;

		for (int i=0;i<names.length;i++)
		{
			st = new StringTokenizer(componentStr[i],",");

			while (st.hasMoreTokens())
			{
				token = st.nextToken();
				tokennum = Integer.parseInt(token);

				qry = "select category from objects where id = '" + token + "'";
				rs = stmt.executeQuery(qry);
				if (rs.first())
				{
					if (debug) System.out.println("loading weapon" + token);
					if (rs.getString("category").equalsIgnoreCase("weapon"))
					{
						loadWeapon(token,mech,bodyParts[i]);
					}
					else if (rs.getString("category").equalsIgnoreCase("ammo"))
					{
						loadAmmo(token,mech,bodyParts[i]);
					}
					else if (rs.getString("category").equalsIgnoreCase("misc"))
					{
						JumpJet jj = new JumpJet(bodyParts[i],mech);
					}
					else System.err.println("Unknown object type: " + rs.getString("category"));
				}
				else System.err.println("Object" + token + " not found");
			}
		}

		return mech;
	}

	/** Load a specified weapon. */
	public static void loadWeapon(String id,MechRemote mech,BodyPart bodypart) throws SQLException
	{
		String qry = 	"select weapons.name,weapons.type,weapons.category,weapons.heat,weapons.damage," +
						"weapons.rangeMin,weapons.rangeShort,weapons.rangeMedium,weapons.rangeLong, " +
						"weapons.criticalSlots " +
						"from weapons,mechs " +
						"where weapons.id = '" + id + "'";
		ResultSet rs = stmt.executeQuery(qry);
		String typeStr,name;

		if (rs.first())
		{
			typeStr = rs.getString("category");
			name = rs.getString("name") + "-" + uniquenum++;

			if (typeStr.equalsIgnoreCase("energy")) new EnergyWeapon(
										name,rs.getString("type"),
										rs.getInt("heat"),rs.getInt("damage"),
										rs.getInt("rangeMin"),rs.getInt("rangeShort"),
										rs.getInt("rangeMedium"),rs.getInt("rangeLong"),
										rs.getInt("criticalSlots"),bodypart,mech);
			else if (typeStr.equalsIgnoreCase("projectile")) new ProjectileWeapon(
										name,rs.getString("type"),
										rs.getInt("heat"),rs.getInt("damage"),
										rs.getInt("rangeMin"),rs.getInt("rangeShort"),
										rs.getInt("rangeMedium"),rs.getInt("rangeLong"),
										rs.getInt("criticalSlots"),bodypart,mech);
			else if (typeStr.equalsIgnoreCase("missile")) new MissileWeapon(
										name,rs.getString("type"),
										rs.getInt("heat"),rs.getInt("damage"),
										rs.getInt("rangeMin"),rs.getInt("rangeShort"),
										rs.getInt("rangeMedium"),rs.getInt("rangeLong"),
										rs.getInt("criticalSlots"),bodypart,mech);
			else System.err.println("Unknown Weapon Type: " + typeStr);
			if (debug) System.err.println(name + " loaded");
		}
		else System.err.println("Weapon" + id + " not found");
	}

	/** Return a Vector of map names. */
	public static Vector loadMapNames() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, java.rmi.RemoteException
	{
		connect();

		String qry = "select name from maps";
		ResultSet rs = stmt.executeQuery(qry);
		Vector v = new Vector();


		while (rs.next()) v.add(rs.getString("name"));
		return v;

	}

	/** Load a specified ammunition item. */
	public static void loadAmmo(String id,MechRemote mech,BodyPart bodypart) throws SQLException
	{
		String qry = 	"select ammos.name,ammos.type,ammos.damage,ammos.shots " +
						"from ammos,mechs " +
						"where ammos.id = '" + id + "'";
		ResultSet rs = stmt.executeQuery(qry);
		String typeStr,name;

		if (rs.first())
		{
			name  = rs.getString("name") + "Ammo-" + uniquenum++;

			Ammo ammo = new Ammo(name,rs.getString("type"),
							rs.getInt("damage"),rs.getInt("shots"),bodypart,mech);
			if (debug) System.err.println(name + " loaded");
		}
		else System.err.println("Ammo" + id + " not found");
	}
}