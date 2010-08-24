#!/usr/bin/perl
use strict;

open(INFILE, "<$ARGV[0]")
  or die "Couldn't open $ARGV[0] for reading\n";

while(<INFILE>) {

  if(/^.+?: Packet size: (\d+)/) {
    print "$1\n";
  }
}

