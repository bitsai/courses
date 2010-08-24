#!/usr/local/bin/perl
use strict;
use DBI;

$ENV{ORACLE} = "/home/oracle/oracle";
$ENV{ORACLE_HOME} = "/home/oracle/oracle/OraHome1";
$ENV{ORACLE_BASE} = "/home/oracle/oracle/OraHome1";
$ENV{ORACLE_SID} = "dbase";

my $username = "btsai_03";
my $password = "harpoon";

my $userpassword;
my $SSN;
my $grade;

my $Db = DBI->connect("dbi:Oracle:", $username, $password, { AutoCommit => 0, PrintError => 0}) || die("Can't login to Oracle: $DBI::errstr\n");

print "\nPlease Enter the 600.315/415 Password: ";
chomp($userpassword = <>);

print "Verifying...\n";

my $qry = $Db->prepare(qq(SELECT curpasswords FROM Passwords WHERE curpasswords = '$userpassword'));
my @arr = &execute($qry);

if (! defined ($arr[0])) 
{
	die("Invalid Password!\n");
}

print "\nPlease Enter the 4-digit SSN of the Student: ";
chomp($SSN = <>);

print "Verifying...\n";

$qry = $Db->prepare(qq(SELECT lname, fname FROM RawScores WHERE ssn = '$SSN'));
@arr = &execute($qry);

if (! defined ($arr[0])) 
{
	die("Invalid SSN!\n");
}

print "\nEnter the Final Exam Score for ".$arr[0]." ".$arr[1].": ";
chomp($grade = <>);

$qry = $Db->prepare(qq(UPDATE RawScores SET fexam = '$grade' WHERE ssn = '$SSN'));
&execute($qry);

print "\nYour change has been processed.\n";

$Db->disconnect();
$Db = undef;
exit (0);

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