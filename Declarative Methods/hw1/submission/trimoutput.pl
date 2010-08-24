######################################################
# trimoutput.pl
#
# Written by John Blatz for JHU CS 325/425
# Professor Jason Eisner
# 9 February 2005
######################################################
# This script reads the output from UBC-SAT or zChaff,
# extracts the solution, and prints it to STDOUT.
#
# Output format is the sequence of the numbers from 
# 1 to n, positive if the solution has set that 
# variable true, and negative if it has set that 
# variable false.
#
# If no satisfying assignment is found, this script
# will print nothing.
######################################################
#!/usr/bin/perl

my $live = shift;
open FILE, $live or die;

$maxsat = 0;

while(<FILE>) {
	exit if /No Solution found/;
	last if /Instance Satisfiable/;		# zChaff output
	last if /Solution found/;		# UBC-SAT output
	if (/vararray/) {				#  (max-sat)
		$maxsat = 1;
		last;
	}
}

if ($maxsat) {
	$line = <FILE>;
	@elts = split /\s+/, $line;
	@vars = split //, $elts[3];
	foreach $i (0.. $#vars) {
		if ($vars[$i] == 0) {
			print "-";
		}
		print $i+1, "\n";
	}
	print "\n";
	exit;
}

while(<FILE>) {
	exit if /Variables/;
	if (/Random Seed Used/) {			# zchaff stuff
		@elts = split /\s+/;
		foreach (@elts) {
			last if/Random/;
			print; print "\n";
		}
		exit;
	}
	print;
}