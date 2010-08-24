#!/usr/bin/perl
use strict;

open(INFILE, "<$ARGV[0]")
  or die "Couldn't open $ARGV[0] for reading\n";

my $numnodes = $ARGV[1];
if ($numnodes == 0) {
  print "usage error: call parseeval.pl <output of simnet eval script> <number of nodes>\n";
  exit;
}

my %num_packets;
my %packet_size;
my %times;
my %intervals;
my %requests;

my $interval = 0;

while(<INFILE>) {
  if (/^.+?: interval: (\d+) numreq: (\d+)/) {
    $intervals{$1}++;
    $requests{$1}+=$2;
    if($intervals{$1} >= ($numnodes / 2) &&
       $1 > $interval) {
      print "Transitioning from $interval to $1 ($intervals{$interval} $intervals{$1})\n";
      $interval = $1;
    }

  } elsif(/^.+?: Packet size: (\d+)/) {
    $packet_size{$interval} += $1;
    $num_packets{$interval}++;

  } elsif(/^.+?: Req process took: (\d+)/) {
    $times{$interval} += $1;
    #print "TIME:$1\n";
  }

}

for(my $i = 0; $i <= $interval; $i++) {
  print "Interval $i\n";
  print "Num Packets: $num_packets{$i}\n";
  print "Total packet size: $packet_size{$i}\n";
  print "Total time: $times{$i}\n";
  print "Num Requests: $requests{$i}\n";
  my $avg_time = $times{$i} / $requests{$i};
  my $avg_time = $times{$i} / $num_packets{$i};
  my $avg_size = $packet_size{$i} / $requests{$i};
  my $avg_req = $packet_size{$i} / $num_packets{$i}; 
  print "Avg time: $avg_time\n";
  print "Avg time per packet: $avg_time\n";
  print "Avg request size: $avg_size\n";
  print "Avg packet size: $avg_req\n";
}

