######################################################
# convertBack.pl
#
# Written by John Blatz for JHU CS 325/425
# Professor Jason Eisner
# 9 February 2005
######################################################
# This script reads a file corresponding to the 
# solution of a SAT problem and renames the variables
# to the names used before applying convertToDIMACS.pl
# 
# To use this file, type
#     perl convertBack.pl solution_file key_file
#
# solution_file is assumed to contain nothing but
# a list of integers from 1 to n, preceded by - if 
# the variable they correspond to is false.
# 
# key_file is the file, ending in ".key", produced by
# convertToDIMACS.pl.  
#
# The output of this program is the same as
# solution_file, with the original variable names 
# restored.
#
# Note that the original file used "~" to indicate 
# "not", whereas the output of this script uses "-".
######################################################

#!/usr/bin/perl

$file = shift;
$keyfile = shift;

open FILE, $keyfile;
my %hash;

while(<FILE>) {
	($val, $key) = split /\s+/;
	next unless $key;
	$hash{$key} = $val;
}

open FILE, $file;
while (<FILE>) {
	for (split /\s+/) {
		print "-" if s/-//;
		print $hash{$_};
		print " ";
	}
	print "\n";
}