#!/usr/local/bin/perl -wT
use strict;
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);

my %colors = (  red     => "#ff0000",
                green   => "#00ff00",
                blue    => "#0000ff",
                gold    => "#cccc00");

print header;
my $color = param('color');

# do some validation - be sure they picked a valid color
if (exists $colors{$color}) {
   print start_html(-title=>"Results", -bgcolor=>$color);
   print "You picked $color.<br>\n";
} else {
   print start_html(-title=>"Results");
   print "You didn't pick a color! (You picked '$color')";
}
print end_html;
