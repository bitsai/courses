SET AUTOCOMMIT ON

DROP TABLE MasterItemTable;
CREATE TABLE MasterItemTable
(
	name		Varchar(26),
	itemQuality	Varchar(26),
	magicQuality	Varchar(26),
	usableClass	Varchar(26),
	PRIMARY KEY (name)
);

DROP TABLE BaseItemTable;
CREATE TABLE BaseItemTable
(
	name		Varchar(26),
	baseItem	Varchar(26)
);

DROP TABLE MagicPropertyTable;
CREATE TABLE MagicPropertyTable
(
	name		Varchar(30),
	property	Varchar(60)
);

DROP TABLE ArmorTable;
CREATE TABLE ArmorTable
(
	itemType	Varchar(26),
	name		Varchar(26),
	minDefense	Number,
	maxDefense	Number,
	charLvl		Number,
	minStr		Number,
	block		Number,
	durability	Number,
	minkickDamage	Number,
	maxkickDamage	Number,
	boxes		Number,
	maxSockets	Number,
	type		Varchar(26),
	minSmiteDamage	Number,
	maxSmiteDamage	Number,
	magicLvl	Number,
	PRIMARY KEY (name)
);

DROP TABLE BlockingTable;
CREATE TABLE BlockingTable
(
	name	Varchar(26),
	class	Varchar(26),
	block	Number
);

DROP TABLE WeaponTable;
CREATE TABLE WeaponTable
(
	itemType	Varchar(26),
	name		Varchar(26),
	hands		Number,
	minThrowDamage	Number,
	maxThrowDamage	Number,
	minDamage	Number,
	maxDamage	Number,
	charLvl		Number,
	minStr		Number,
	minDex		Number,
	range		Number,
	durability	Number,
	maxSockets	Number,
	speed		Number,
	stackSize	Number,
	magicLvl	Number,
	plusSkills	Varchar(26),
	PRIMARY KEY (name)
);

DROP TABLE WeaponSpeedTable;
CREATE TABLE WeaponSpeedTable
(
	name	Varchar(26),
	class	Varchar(26),
	speed	Number
);

DROP TABLE GemTable;
CREATE TABLE GemTable
(
	quality		Varchar(10),
	type		Varchar(10),
	lvl		Number,
	weapon		Varchar(60),
	shield		Varchar(60),
	armor		Varchar(60),
	PRIMARY KEY (quality, type)
);

DROP TABLE JewelAffixTable;
CREATE TABLE JewelAffixTable
(
	type	Varchar(10),
	name	Varchar(20),
	effect	Varchar(70),
	lvl	Number,
	PRIMARY KEY (name)
);

DROP TABLE RuneTable;
CREATE TABLE RuneTable
(
	name		Varchar(10),
	weapon		Varchar(60),
	armor		Varchar(60),
	shield		Varchar(60),
	lvl		Number,
	PRIMARY KEY (name)
);

DROP TABLE RunewordTable;
CREATE TABLE RunewordTable
(
	name		Varchar(26),
	sockets		Number,
	rune1		Varchar(26),
	rune2		Varchar(26),
	rune3		Varchar(26),
	rune4		Varchar(26),
	rune5		Varchar(26),
	rune6		Varchar(26),
	PRIMARY KEY (name)
);

DROP TABLE MagicAffixTable;
CREATE TABLE MagicAffixTable
(
	type		Varchar(10),
	effectType	Varchar(60),
	name		Varchar(30),
	effect		Varchar(120),
	lvl		Number,
	PRIMARY KEY (type, effectType, name)
);

DROP TABLE MagicAffixLevelTable;
CREATE TABLE MagicAffixLevelTable
(
	affixName	Varchar(26),
	baseItem	Varchar(26),
	lvl		Number
);

DROP TABLE CraftedItemTable;
CREATE TABLE CraftedItemTable
(
	itemGroup	Varchar(26),
	itemType	Varchar(26),
	name		Varchar(26),
	rune		Varchar(26),
	gem		Varchar(26),
	jewel		Varchar(26),
	PRIMARY KEY (name)
);

DROP TABLE SetTable;
CREATE TABLE SetTable
(
	name		Varchar(26),
	class		Varchar(26),
	PRIMARY KEY (name)
);

DROP TABLE SetPropertyTable;
CREATE TABLE SetPropertyTable
(
	name		Varchar(26),
	pieces		Number,
	property	Varchar(26)
);

DROP TABLE RingTable;
CREATE TABLE RingTable
(
	name		Varchar(30),
	lvl		Number,
	PRIMARY KEY (name)
);

DROP TABLE AmuletTable;
CREATE TABLE AmuletTable
(
	name		Varchar(30),
	lvl		Number,
	PRIMARY KEY (name)
);

DROP TABLE CharmTable;
CREATE TABLE CharmTable
(
	charm_size	Varchar(10),
	name		Varchar(20),
	lvl		Number,
	PRIMARY KEY (name)
);

DROP TABLE JewelTable;
CREATE TABLE JewelTable
(
	name		Varchar(30),
	lvl		Number,
	PRIMARY KEY (name)
);

DROP TABLE CharmAffixTable;
CREATE TABLE CharmAffixTable
(
	type		Varchar(10),
	name		Varchar(20),
	effect		Varchar(70),
	charmSize	Varchar(10),
	lvl		Number,
	PRIMARY KEY (type, name, charmSize)
);

DROP TABLE UpgradeTable;
CREATE TABLE UpgradeTable
(
	old	Varchar(26),
	new	Varchar(26)
);