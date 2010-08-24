#!/usr/local/bin/perl
use DBI;
use LWP::RobotUA;
use strict;

$ENV{ORACLE} = "/home/oracle/oracle";
$ENV{ORACLE_HOME} = "/home/oracle/oracle/OraHome1";
$ENV{ORACLE_BASE} = "/home/oracle/oracle/OraHome1";
$ENV{ORACLE_SID} = "dbase";

my $username = "btsai_03";
my $password = "harpoon";

my $Db = DBI->connect("dbi:Oracle:", $username, $password, { AutoCommit => 0, PrintError => 0}) || die("Can't login to Oracle: $DBI::errstr\n");

my $ROBOT_NAME = 'BTsaiBot/1.0';
my $ROBOT_MAIL = 'btsai@cs.jhu.edu';

my $ua = new LWP::RobotUA $ROBOT_NAME, $ROBOT_MAIL;
$ua->delay(0.016);

### Armor

print "Processing Circlets...\n";
&readPage("http://www.battle.net/diablo2exp/items/circlets.shtml");
&getCirclets;

print "Processing Helms...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/helms.shtml");
&getHelms("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/helms.shtml");
&getHelms("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/helms.shtml");
&getHelms("Elite");

print "Processing Armor...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/armor.shtml");
&getArmor("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/armor.shtml");
&getArmor("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/armor.shtml");
&getArmor("Elite");

print "Processing Shields...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/shields.shtml");
&getShields("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/shields.shtml");
&getShields("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/shields.shtml");
&getShields("Elite");

print "Processing Gloves...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/gloves.shtml");
&getGloves("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/gloves.shtml");
&getGloves("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/gloves.shtml");
&getGloves("Elite");

print "Processing Boots...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/boots.shtml");
&getBoots("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/boots.shtml");
&getBoots("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/boots.shtml");
&getBoots("Elite");

print "Processing Belts...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/belts.shtml");
&getBelts("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/belts.shtml");
&getBelts("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/belts.shtml");
&getBelts("Elite");

### Class Armor

print "Processing Barbarian Helms...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/barbhelms.shtml");
&getClassHelms("Normal", "Barbarian", "Barbarian Helm");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/barbhelms.shtml");
&getClassHelms("Exceptional", "Barbarian", "Barbarian Helm");
&readPage("http://www.battle.net/diablo2exp/items/elite/barbhelms.shtml");
&getClassHelms("Elite", "Barbarian", "Barbarian Helm");

print "Processing Druid Pelts...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/druidpelts.shtml");
&getClassHelms("Normal", "Druid", "Pelt");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/druidpelts.shtml");
&getClassHelms("Exceptional", "Druid", "Pelt");
&readPage("http://www.battle.net/diablo2exp/items/elite/druidpelts.shtml");
&getClassHelms("Elite", "Druid", "Pelt");

print "Processing Paladin Shields...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/paladinshields.shtml");
&getClassShields("Normal", "Paladin", "Paladin Shield");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/paladinshields.shtml");
&getClassShields("Exceptional", "Paladin", "Paladin Shield");
&readPage("http://www.battle.net/diablo2exp/items/elite/paladinshields.shtml");
&getClassShields("Elite", "Paladin", "Paladin Shield");

print "Processing Shrunken Heads...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/shrunkenheads.shtml");
&getClassShields("Normal", "Necromancer", "Shrunken Head");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/shrunkenheads.shtml");
&getClassShields("Exceptional", "Necromancer", "Shrunken Head");
&readPage("http://www.battle.net/diablo2exp/items/elite/shrunkenheads.shtml");
&getClassShields("Elite", "Necromancer", "Shrunken Head");

### Weapons

print "Processing Axes...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/axes.shtml");
&getDualWeapons("Normal", "Ax");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/axes.shtml");
&getDualWeapons("Exceptional", "Ax");
&readPage("http://www.battle.net/diablo2exp/items/elite/axes.shtml");
&getDualWeapons("Elite", "Ax");

print "Processing Bows...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/bows.shtml");
&getBows("Normal", "Bow");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/bows.shtml");
&getBows("Exceptional", "Bow");
&readPage("http://www.battle.net/diablo2exp/items/elite/bows.shtml");
&getBows("Elite", "Bow");

print "Processing Crossbows...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/crossbows.shtml");
&getBows("Normal", "Crossbow");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/crossbows.shtml");
&getBows("Exceptional", "Crossbow");
&readPage("http://www.battle.net/diablo2exp/items/elite/crossbows.shtml");
&getBows("Elite", "Crossbow");

print "Processing Daggers...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/daggers.shtml");
&getMeleeWeapons("Normal", "Dagger", 1);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/daggers.shtml");
&getMeleeWeapons("Exceptional", "Dagger", 1);
&readPage("http://www.battle.net/diablo2exp/items/elite/daggers.shtml");
&getMeleeWeapons("Elite", "Dagger", 1);

print "Processing Javelins...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/javelins.shtml");
&getThrowingWeapons("Normal", "Javelin");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/javelins.shtml");
&getThrowingWeapons("Exceptional", "Javelin");
&readPage("http://www.battle.net/diablo2exp/items/elite/javelins.shtml");
&getThrowingWeapons("Elite", "Javelin");

print "Processing Maces...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/maces.shtml");
&getDualWeapons("Normal", "Mace");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/maces.shtml");
&getDualWeapons("Exceptional", "Mace");
&readPage("http://www.battle.net/diablo2exp/items/elite/maces.shtml");
&getDualWeapons("Elite", "Mace");

print "Processing Polearms...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/polearms.shtml");
&getMeleeWeapons("Normal", "Polearm", 2);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/polearms.shtml");
&getMeleeWeapons("Exceptional", "Polearm", 2);
&readPage("http://www.battle.net/diablo2exp/items/elite/polearms.shtml");
&getMeleeWeapons("Elite", "Polearm", 2);

print "Processing Scepters...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/scepters.shtml");
&getMeleeWeapons("Normal", "Scepter", 1);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/scepters.shtml");
&getMeleeWeapons("Exceptional", "Scepter", 1);
&readPage("http://www.battle.net/diablo2exp/items/elite/scepters.shtml");
&getMeleeWeapons("Elite", "Scepter", 1);

print "Processing Spears...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/spears.shtml");
&getMeleeWeapons("Normal", "Spear", 2);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/spears.shtml");
&getMeleeWeapons("Exceptional", "Spear", 2);
&readPage("http://www.battle.net/diablo2exp/items/elite/spears.shtml");
&getMeleeWeapons("Elite", "Spear", 2);

print "Processing Staves...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/staves.shtml");
&getMagicWeapons("Normal", "Staff", 2);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/staves.shtml");
&getMagicWeapons("Exceptional", "Staff", 2);
&readPage("http://www.battle.net/diablo2exp/items/elite/staves.shtml");
&getMagicWeapons("Elite", "Staff", 2);

print "Processing Swords...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/swords.shtml");
&getSwords("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/swords.shtml");
&getSwords("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/swords.shtml");
&getSwords("Elite");

print "Processing Throwing Weapons...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/throw.shtml");
&getThrowingWeapons("Normal", "Throwing");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/throw.shtml");
&getThrowingWeapons("Exceptional", "Throwing");
&readPage("http://www.battle.net/diablo2exp/items/elite/throw.shtml");
&getThrowingWeapons("Elite", "Throwing");

print "Processing Wands...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/wands.shtml");
&getMagicWeapons("Normal", "Wand", 1);
&readPage("http://www.battle.net/diablo2exp/items/exceptional/wands.shtml");
&getMagicWeapons("Exceptional", "Wand", 1);
&readPage("http://www.battle.net/diablo2exp/items/elite/wands.shtml");
&getMeleeWeapons("Elite", "Wand", 1);

### Class Weapons

print "Processing Amazon Weapons...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/amazonweapons.shtml");
&getAmazonWeapons("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/amazonweapons.shtml");
&getAmazonWeapons("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/amazonweapons.shtml");
&getAmazonWeapons("Elite");

print "Processing Assassin Katars...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/katars.shtml");
&getAssassinKatars("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/katars.shtml");
&getAssassinKatars("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/katars.shtml");
&getAssassinKatars("Elite");

print "Processing Sorceress Orbs...\n";
&readPage("http://www.battle.net/diablo2exp/items/normal/orbs.shtml");
&getSorceressOrbs("Normal");
&readPage("http://www.battle.net/diablo2exp/items/exceptional/orbs.shtml");
&getSorceressOrbs("Exceptional");
&readPage("http://www.battle.net/diablo2exp/items/elite/orbs.shtml");
&getSorceressOrbs("Elite");

### End of program stuff

$Db->disconnect();
$Db = undef;
exit (0);

### Armor Subroutines

sub getCirclets
{
	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
	
		if ($first =~ /<TD>/)
		{
			my ($line, $discard);
			my ($name, $minDefense, $maxDefense, $durability, $charLvl, $maxSockets, $magicLvl);

			$discard = <TEMP>;
			$line = <TEMP>;
			$line =~ /<b>(.+)<\/b>/;
			$name = $1;
			$discard = <TEMP>;
			$discard = <TEMP>;
			$line = <TEMP>;
			$line =~ /<SPAN>(.+)-(.+)<\/font>/;
			$minDefense = $1;
			$maxDefense = $2;
			$line = <TEMP>;
			$line =~ /<SPAN>(.+)<\/font>/;
			$durability = $1;
			$line = <TEMP>;
			$line =~ /<SPAN>(.+)<\/font>/;
			$charLvl = $1;
			$line = <TEMP>;
			$line =~ /<SPAN>(.+)<\/font>/;
			$maxSockets = $1;
			$line = <TEMP>;
			$line =~ /<SPAN>(.+)<\/font>/;
			$magicLvl = $1;

			&putMasterItem($name, 'Normal', 'None', 'All');
			&putArmor("Circlet", $name, $minDefense, $maxDefense, $charLvl, 0, 0, 0, 0, 0, 0, $maxSockets, "None", 0, 0, $magicLvl);
		}
	}

	close TEMP;
}

sub getHelms
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/)
		{
			my $second = <TEMP>;
	
			if ($second =~ /<TD.*>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $maxSockets);

				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Helm", $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, 0, 0, 0, $maxSockets, "None", 0, 0, 0);
			}
		}
	}

	close TEMP;
}

sub getArmor
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $maxSockets, $type);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+?)(<\/SPAN>)*<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$type = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Armor", $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, 0, 0, 0, $maxSockets, $type, 0, 0, 0);
			}
		}
	}

	close TEMP;
}

sub getShields
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $block, $durability, $maxSockets, $minSmiteDamage, $maxSmiteDamage, $type);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$block = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)\sto\s(.+)<\/font>/;
				$minSmiteDamage = $1;
				$maxSmiteDamage = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/SPAN>/;
				$type = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Shield", $name, $minDefense, $maxDefense, $charLvl, $minStr, $block, $durability, 0, 0, 0, $maxSockets, $type, $minSmiteDamage, $maxSmiteDamage, 0);
			}
		}
	}

	close TEMP;
}

sub getGloves
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Gloves", $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, 0, 0, 0, 0, 'None', 0, 0, 0);
			}
		}
	}

	close TEMP;
}

sub getBoots
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $minKickDamage, $maxKickDamage);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minKickDamage = $1;
				$maxKickDamage = $2;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Boots", $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, $minKickDamage, $maxKickDamage, 0, 0, 'None', 0, 0, 0);
			}
		}
	}

	close TEMP;
}

sub getBelts
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $boxes);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;

				if ($line =~ /<SPAN>(\d+)-(\d+)<\/font>/)
				{
					$minDefense = $1;
					$maxDefense = $2;
				}
				elsif ($line =~ /<SPAN>(.+)<\/font>/)
				{
					$minDefense = $1;
					$maxDefense = $1;
				}

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$boxes = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putArmor("Belt", $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, 0, 0, $boxes, 0, 'None', 0, 0, 0);
			}
		}
	}

	close TEMP;
}

### Class Armor Subroutines

sub getClassHelms
{
	my $quality = shift;
	my $class = shift;
	my $itemType = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/)
		{
			my $second = <TEMP>;
	
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $maxSockets);

				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$charLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;

				&putMasterItem($name, $quality, 'None', $class);
				&putArmor($itemType, $name, $minDefense, $maxDefense, $charLvl, $minStr, 0, $durability, 0, 0, 0, $maxSockets, 'None', 0, 0, 0);
			}
		}
	}

	close TEMP;
}

sub getClassShields
{
	my $quality = shift;
	my $class = shift;
	my $itemType = shift;
	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDefense, $maxDefense, $charLvl, $minStr, $durability, $block, $maxSockets, $minSmiteDamage, $maxSmiteDamage, $type);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)-(.+)<\/font>/;
				$minDefense = $1;
				$maxDefense = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$charLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$block = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				
				if ($class =~ /Paladin/)
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+) to (.+)<\/font>/;
					$minSmiteDamage = $1;
					$maxSmiteDamage = $2;
				}
				else
				{
					$minSmiteDamage = 0;
					$maxSmiteDamage = 0;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/SPAN>/;
				$type = $1;

				&putMasterItem($name, $quality, 'None', $class);
				&putArmor($itemType, $name, $minDefense, $maxDefense, $charLvl, $minStr, $block, $durability, 0, 0, 0, $maxSockets, $type, $minSmiteDamage, $maxSmiteDamage, 0);
			}
		}
	}

	close TEMP;
}

### Weapon Subroutines

sub getDualWeapons
{
	my $quality = shift;
	my $itemType = shift;
	my $hands = 0;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /Table/) { $hands++; }
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);		
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon($itemType, $name, $hands, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, 0, 'No');
			}
		}
	}

	close TEMP;
}

sub getBows
{
	my $quality = shift;
	my $itemType = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);		
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $maxSockets, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(<SPAN>)*(.+)<\/b>/;
				$name = $2;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon($itemType, $name, 2, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, 0, 0, $maxSockets, $speed, 0, 0, 'No');
			}
		}
	}

	close TEMP;
}

sub getMeleeWeapons
{
	my $quality = shift;
	my $itemType = shift;
	my $hands = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);		
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+?)(<\/SPAN>)*<\/font>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+?)(<\/SPAN>)*<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon($itemType, $name, $hands, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, 0, 'No');
			}
		}
	}

	close TEMP;
}

sub getThrowingWeapons
{
	my $quality = shift;
	my $itemType = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);		
				my ($name, $minThrowDamage, $maxThrowDamage, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $stackSize, $weaponClass, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(<SPAN>)*(.+)<\/b>/;
				$name = $2;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minThrowDamage = $1;
				$maxThrowDamage = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+?)(<\/SPAN>)*<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+?)(<\/SPAN>)*<\/font>/;
				$stackSize = $1;
				$line = <TEMP>;
				if ($line =~ /BR/)
				{
					$weaponClass = "All";
					$line =~ /<SPAN>\[(.+)\]<BR>/;
					$speed = $1;
				}
				else
				{
					$line =~ /<SPAN>(.+)<\/font>/;
					$weaponClass = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>\[(.+)\]<BR>/;
					$speed = $1;
				}

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon($itemType, $name, 1, $minThrowDamage, $maxThrowDamage, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, 0, 0, $speed, $stackSize, 0, 'No');
			}
		}
	}

	close TEMP;
}

sub getSwords
{
	my $quality = shift;
	my $hands = 1;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /TWO HANDED/) { $hands++; }
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.*>/)
			{
				my ($line, $discard);		
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed);
			
				$discard = <TEMP>;
				$line = <TEMP>;
				if ($line =~ /<b>(.+)<\/b>/) { $name = $1; }
				else
				{ 
					$line = <TEMP>;
					$line =~ /<b>(.+)<\/b>/;
					$name = $1;
				}
				$discard = <TEMP>;
				if ($hands > 1) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}

				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon('Sword', $name, $hands, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, 0, 'No');
			}
		}
	}

	close TEMP;
}

sub getMagicWeapons
{	
	my $quality = shift;
	my $itemType = shift;
	my $hands = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $magicLvl, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) to (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;

				if ($quality =~ /Normal/) { $charLvl = 0; }
				else
				{
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/SPAN>/;
					$charLvl = $1;
				}
				
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$magicLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'All');
				&putWeapon($itemType, $name, $hands, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, $magicLvl, 'No');
			}
		}
	}

	close TEMP;
}

### Class Weapon Subroutines

sub getAmazonWeapons
{
	my $quality = shift;
	my $mode = 0;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<CENTER>/) { $mode++; }
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				
				if ($mode == 1)
				{
					my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $maxSockets, $weaponClass, $speed);
			
					$line = <TEMP>;
					if ($line =~ /img/) { $discard = <TEMP>; }
					$line = <TEMP>;
					$line =~ /<b>(.+)<\/b>/;
					$name = $1;
					$discard = <TEMP>;
					$line = <TEMP>;
					$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
					$minDamage = $1;
					$maxDamage = $2;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$charLvl = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minStr = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minDex = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$maxSockets = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$weaponClass = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>\[(.+)\]<BR>/;
					$speed = $1;

					&putMasterItem($name, $quality, 'None', 'Amazon');
					&putWeapon('Amazon Weapon', $name, 2, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, 0, 0, $maxSockets, $speed, 0, 0, 'Yes');
				}
				elsif ($mode == 2)
				{
					my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $weaponClass, $speed);
			
					$line = <TEMP>;
					if ($line =~ /img/) { $discard = <TEMP>; }
					$line = <TEMP>;
					$line =~ /<b>(.+)<\/b>/;
					$name = $1;
					$discard = <TEMP>;
					$line = <TEMP>;
					$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
					$minDamage = $1;
					$maxDamage = $2;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$charLvl = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minStr = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minDex = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$range = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$durability = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$maxSockets = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$weaponClass = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>\[(.+)\]<BR>/;
					$speed = $1;

					&putMasterItem($name, $quality, 'None', 'Amazon');
					&putWeapon('Amazon Weapon', $name, 2, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, 0, 'Yes');
				}
				elsif ($mode == 3)
				{
					my ($name, $minThrowDamage, $maxThrowDamage, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $stackSize, $weaponClass, $speed);
			
					$line = <TEMP>;
					if ($line =~ /img/) { $discard = <TEMP>; }
					$line = <TEMP>;
					$line =~ /<b>(.+)<\/b>/;
					$name = $1;
					$discard = <TEMP>;
					$line = <TEMP>;
					$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
					$minThrowDamage = $1;
					$maxThrowDamage = $2;
					$line = <TEMP>;
					$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
					$minDamage = $1;
					$maxDamage = $2;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$charLvl = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minStr = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$minDex = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$range = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$stackSize = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>(.+)<\/font>/;
					$weaponClass = $1;
					$line = <TEMP>;
					$line =~ /<SPAN>\[(.+)\]<BR>/;
					$speed = $1;

					&putMasterItem($name, $quality, 'None', 'Amazon');
					&putWeapon('Amazon Weapon', $name, 1, $minThrowDamage, $maxThrowDamage, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, 0, 0, $speed, $stackSize, 0, 'Yes');
				}
			}
		}
	}

	close TEMP;
}

sub getAssassinKatars
{
	my $quality = shift;
	
	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $plusSkills, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$charLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$plusSkills = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'Assassin');
				&putWeapon('Katar', $name, 1, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, 0, $plusSkills);
			}
		}
	}

	close TEMP;
}

sub getSorceressOrbs
{
	my $quality = shift;

	open TEMP, "TEMP";

	while (<TEMP>)
	{
		my $first = $_;
		
		if ($first =~ /<TR>/) 
		{ 
			my $second = <TEMP>; 
			
			if ($second =~ /<TD.+>/)
			{
				my ($line, $discard);
				my ($name, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $magicLvl, $speed);
			
				$line = <TEMP>;
				if ($line =~ /img/) { $discard = <TEMP>; }
				$line = <TEMP>;
				$line =~ /<b>(.+)<\/b>/;
				$name = $1;
				$discard = <TEMP>;
				$line = <TEMP>;
				$line =~ /<SPAN>(\d+) To (\d+).*<\/font>/;
				$minDamage = $1;
				$maxDamage = $2;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$charLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minStr = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$minDex = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$range = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$durability = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$maxSockets = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>(.+)<\/font>/;
				$magicLvl = $1;
				$line = <TEMP>;
				$line =~ /<SPAN>\[(.+)\]<BR>/;
				$speed = $1;

				&putMasterItem($name, $quality, 'None', 'Sorceress');
				&putWeapon('Orb', $name, 1, 0, 0, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, 0, $magicLvl, 'Yes');
			}
		}
	}

	close TEMP;
}

### Input/Output Subroutines

sub readPage
{
	my $URL = shift;
	open TEMP, ">TEMP";

	my $request = new HTTP::Request GET => $URL;
	my $response = $ua->request($request);
	my $content = $response->content;

	print TEMP $content;
	close TEMP;
}

sub putMasterItem
{
	my $name = shift;
	my $itemQuality = shift;
	my $magicQuality = shift;
	my $usableClass = shift;

	$name =~ s/'//;
	
	my $qry = $Db->prepare(qq(INSERT INTO MasterItemTable VALUES('$name', '$itemQuality', '$magicQuality', '$usableClass')));
	&execute($qry);
}

sub putArmor
{
	my $itemType = shift;
	my $name = shift;
	my $minDefense = shift;
	my $maxDefense = shift;
	my $charLvl = shift;
	my $minStr = shift;
	my $block = shift;
	my $durability = shift;
	my $minKickDamage = shift;
	my $maxKickDamage = shift;
	my $boxes = shift;
	my $maxSockets = shift;
	my $type = shift;
	my $minSmiteDamage = shift;
	my $maxSmiteDamage = shift;
	my $magicLvl = shift;
	
	$name =~ s/'//;
	$minStr =~ s/-/0/;
	
	my $qry = $Db->prepare(qq(INSERT INTO ArmorTable VALUES('$itemType', '$name', $minDefense, $maxDefense, $charLvl, $minStr, $block, $durability, $minKickDamage, $maxKickDamage, $boxes, $maxSockets, '$type', $minSmiteDamage, $maxSmiteDamage, $magicLvl)));
	&execute($qry);
}

sub putWeapon
{
	my $itemType = shift;
	my $name = shift;
	my $hands = shift;
	my $minThrowDamage = shift;
	my $maxThrowDamage = shift;
	my $minDamage = shift;
	my $maxDamage = shift;
	my $charLvl = shift;
	my $minStr = shift;
	my $minDex = shift;
	my $range = shift;
	my $durability = shift;
	my $maxSockets = shift;
	my $speed = shift;
	my $stackSize = shift;
	my $magicLvl = shift;
	my $plusSkills = shift;
	
	$name =~ s/'//;
	$minStr =~ s/-/0/;
	$minDex =~ s/-/0/;
	$minDex =~ s/&nbsp;/0/;
	$durability =~ s/Indestructible/0/;
	
	my $qry = $Db->prepare(qq(INSERT INTO WeaponTable VALUES('$itemType', '$name', $hands, $minThrowDamage, $maxThrowDamage, $minDamage, $maxDamage, $charLvl, $minStr, $minDex, $range, $durability, $maxSockets, $speed, $stackSize, $magicLvl, '$plusSkills')));
	&execute($qry);
}

sub execute
{
	my $qry = shift;

	if (! $qry->execute()) 
	{
		my $err = $DBI::errstr;
		$qry->finish();
		$Db->disconnect();
		$Db = undef;
		die("$err\n");
	}

	my @arr = $qry->fetchrow_array();
	$qry->finish();
	return @arr;
}