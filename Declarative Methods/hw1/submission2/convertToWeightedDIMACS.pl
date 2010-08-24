######################################################
# convertToWeightedDIMACS.pl
#
# Written by John Blatz for JHU CS 325/425
# Professor Jason Eisner
# 10 February 2005
######################################################
# This script will convert CNF files in a more 
# "user-friendly" format into the weighted extension 
# to the DIMACS standard that is used by UBC-SAT.
# 
# The format that this script accepts is as follows:
#    w:weight literal v literal v ... v literal & 
#    w:weight literal v literal v ... v literal &
#	...
#    w:weight literal v literal v ... v literal
#
# where each instance of "literal" is an alphanumeric 
# string representing the name of the variable,
# optionally preceded by "~", and each instance of
# "weight" is replaced by a real number for the 
# weight of the clause in question.
#
# As you expect, "~" means "not", "v" means "or", 
# and "&" means "and".  Parentheses are not used,
# and as a result in this format "or" has higher 
# precedence than "and", even though it's the other
# way around in C++.
#
# For obvious reasons, you are not allowed to have a 
# variable named "v".  Variables are additionally not 
# allowed to be named "0", or contain the characters
# "~" or "&".
#
# Note: This script also accepts files already in
# DIMACS CNF format, not affecting any change to them
# except for possibly permuting the variable indices.
# This means that if you want to have your "encode"
# and "decode" programs work directly with DIMACS CNF
# format rather than this one, you can.
#
# 
# The DIMACS CNF format file will be output to STDOUT,
# and a file ending in ".key" will be created that
# records the mapping between your variable names
# and the indices in the DIMACS file.  The ".key" file
# will be used later by the script convertBack.pl.
######################################################

#!/usr/bin/perl



$file = shift;
open FILE, $file or die;

@fileparts = split /\./, $file;
$file = $fileparts[0];


my %hash;
$hash{"&"} = 0;
$hash{"0"} = 0;

$clauses = 0;
$bonus = 0;
while(<FILE>) {
	next if /^c\s+/ or /^p\s+/;
	++$clauses if /&/ or /\s+0\s+/;
	$bonus = 1 if /&/;
	s/\s*v\s*/ /g;
	s/~//g;
	s/-//g;
	foreach (split /\s+/) {
		next if /^w:/;
		$hash{$_} = scalar(keys(%hash))-1 unless exists $hash{$_};	
	}	
}
$clauses += $bonus;

open OUT, "> $file.key" or die "can't open keyfile for output";
foreach $key (keys(%hash)) {
	print OUT $key;
	print OUT " ";
	print OUT $hash{$key};
	print OUT "\n";
}

print "c $file.cnf\n";
print "c Auto-generated DIMACS CNF file created by convertToWeightedDIMACS.pl\n";
print "p wcnf ";
print scalar(keys(%hash)) - 2;
print " $clauses\n";

seek FILE, 0, 0;
while(<FILE>) {
	next if /^c\s+/ or /^p\s+/;
	s/\s*v\s*/ /g;
	foreach (split /\s+/) {
		if (/w:([\.\d]*)/) {
			print $1; 
			print " ";
			next;
		}
		$neg = s/~//g || s/^-//g;
		print "-" if $neg;
		print "$hash{$_} ";
	}	
	print "\n";
}
print "0\n" if $bonus;


