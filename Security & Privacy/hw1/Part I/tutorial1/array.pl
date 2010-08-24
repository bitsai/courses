#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

# print the html headers
print header;
print start_html("Colors");

my @colors = ("red","green","blue");

foreach my $i (@colors) {
    print "$i<br>\n";
}

my @people = ();
push(@people, "Howard");
push(@people, ("Sara", "Ken", "Josh"));
my $who = shift(@people);
print "$who<br>\n";

my $who = pop(@people);
print "$who<br>\n";

my $linelen = @people;
print "There are $linelen people in line.<br>\n";
print "The last person in line is $people[$#people].<br>\n";

my @colors = ("cyan", "magenta", "yellow", "black");
foreach my $i (0..$#colors) {
   print "color $i is $colors[$i]<br>\n";
}

my @slice = @colors[1..2];
print "@slice<br>\n";

my @colors = ("cyan", "magenta", "yellow", "black");
print "@colors<br>\n";
my @colors2 = sort(@colors);
print "@colors2<br>\n";

my @colors = ("cyan", "magenta", "yellow", "black");
print "@colors<br>\n";
@colors = reverse(@colors);
print "@colors<br>\n";

my @colors = ("cyan", "magenta", "yellow", "black");
print "@colors<br>\n";
@colors = reverse(sort(@colors));
print "@colors<br>\n";

my @numberlist = (8, 4, 3, 12, 7, 15, 5);
print "@numberlist<br>\n";
my @sortednumberlist = sort( {$a <=> $b;} @numberlist);
print "@sortednumberlist<br>\n";

my @colors = ("cyan", "magenta", "yellow", "black");
print "@colors<br>\n";
my $colorstring = join(", ",@colors);
print "$colorstring<br>\n";

my ($first, $second, $third) = sort("red", "green", "blue");
print "$first, $second, $third<br>\n";

print end_html;
