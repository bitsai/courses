CREATE OR REPLACE PROCEDURE GetItemProperties (itemName IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT property
		FROM MagicPropertyTable
		WHERE name = '||chr(39)||itemName||chr(39);
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetMagicAffixes (lvl IN Number, type IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM MagicAffixTable
		WHERE .75 * lvl <= '||lvl||' and type = '||chr(39)||type||chr(39)||'
		ORDER BY effectType, lvl, effect';
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetCharmAffixes (lvl IN Number, type IN Varchar, charmSize IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM CharmAffixTable
		WHERE .75 * lvl <= '||lvl||' and type = '||chr(39)||type||chr(39)||' and charmSize = '||chr(39)||charmSize||chr(39)||'
		ORDER BY lvl, effect';
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetJewelAffixes (lvl IN Number, type IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM JewelAffixTable
		WHERE .75 * lvl <= '||lvl||' and type = '||chr(39)||type||chr(39)||'
		ORDER BY lvl, effect';
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetRunes (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM RuneTable
		WHERE lvl <= '||lvl||'
		ORDER BY lvl, name';
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetGems (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM GemTable
		WHERE lvl <= '||lvl;
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetUniqueRings (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM RingTable
		WHERE lvl <= '||lvl||'
		ORDER BY lvl, name';
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetUniqueAmulets (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM AmuletTable
		WHERE lvl <= '||lvl;
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetUniqueCharms (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM CharmTable
		WHERE lvl <= '||lvl;
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetUniqueJewels (lvl IN Number) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS 
		SELECT * 
		FROM JewelTable
		WHERE lvl <= '||lvl;
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetArmor (lvl IN Number, minStr IN Number, itemType IN Varchar, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN ArmorTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and itemType = '||chr(39)||itemType||chr(39)||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetWeapons (lvl IN Number, minStr IN Number, minDex IN Number, itemType IN Varchar, hands IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN WeaponTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and minDex <= '||minDex||' and itemType = '||chr(39)||itemType||chr(39)||' and hands = '||hands||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	output := owa_util.tablePrint('Temp', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestKickBoots (lvl IN Number, minStr IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT * 
		FROM MasterItemTable NATURAL INNER JOIN ArmorTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39)||' and itemType = '||chr(39)||'Boots'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE (minkickDamage + maxKickDamage) >= ALL (SELECT (minkickDamage + maxKickDamage) FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestSmiteShield (lvl IN Number, minStr IN Number, itemType IN Varchar, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT * 
		FROM MasterItemTable NATURAL INNER JOIN ArmorTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39)||' and itemType = '||chr(39)||itemType||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE (minSmiteDamage + maxSmiteDamage) >= ALL (SELECT (minSmiteDamage + maxSmiteDamage) FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestBlockShield (lvl IN Number, minStr IN Number, itemType IN Varchar, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN ArmorTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39)||' and itemType = '||chr(39)||itemType||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE block >= ALL (SELECT block FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestDefenseArmor (lvl IN Number, minStr IN Number, itemType IN Varchar, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN ArmorTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and itemType = '||chr(39)||itemType||chr(39)||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE (minDefense + maxDefense) >= ALL (SELECT (minDefense + maxDefense) FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetLongestWeapon (lvl IN Number, minStr IN Number, minDex IN Number, itemType IN Varchar, hands IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN WeaponTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and minDex <= '||minDex||' and itemType = '||chr(39)||itemType||chr(39)||' and hands = '||hands||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE range >= ALL (SELECT range FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetFastestWeapon (lvl IN Number, minStr IN Number, minDex IN Number, itemType IN Varchar, hands IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN WeaponTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and minDex <= '||minDex||' and itemType = '||chr(39)||itemType||chr(39)||' and hands = '||hands||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE speed <= ALL (SELECT speed FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestMeleeDamageWeapon (lvl IN Number, minStr IN Number, minDex IN Number, itemType IN Varchar, hands IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN WeaponTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and minDex <= '||minDex||' and itemType = '||chr(39)||itemType||chr(39)||' and hands = '||hands||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE (minDamage + maxDamage) >= ALL (SELECT (minDamage + maxDamage) FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/

CREATE OR REPLACE PROCEDURE GetBestThrowDamageWeapon (lvl IN Number, minStr IN Number, minDex IN Number, itemQuality IN Varchar) AUTHID CURRENT_USER
AS
	output Boolean;
BEGIN
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp AS
		SELECT *
		FROM MasterItemTable NATURAL INNER JOIN WeaponTable
		WHERE charLvl <= '||lvl||' and minStr <= '||minStr||' and minDex <= '||minDex||' and itemQuality = '||chr(39)||itemQuality||chr(39)||' and magicQuality = '||chr(39)||'None'||chr(39);
	EXECUTE IMMEDIATE 'CREATE OR REPLACE VIEW Temp2 AS
		SELECT *
		FROM Temp
		WHERE (minThrowDamage + maxThrowDamage) >= ALL (SELECT (minThrowDamage + maxThrowDamage) FROM Temp)';
	output := owa_util.tablePrint('Temp2', 'BORDER', owa_util.HTML_TABLE, '*');
END;
/