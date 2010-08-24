#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

# declare the colors hash:
my %colors = (	red => "#ff0000", green=> "#00ff00",
    blue => "#0000ff",	black => "#000000",
    white => "#ffffff" );

# print the html headers
print header;
print start_html(-title=>"Colors", -bgcolor=>"#cccccc");

foreach my $color (sort(keys %colors)) {
    print qq(<font color="$colors{$color}">$color</font>\n);
}

$colors{purple} = "#ff00ff";

print "<br>\n";

foreach my $color (sort(keys %colors)) {
    print qq(<font color="$colors{$color}">$color</font>\n);
}

print "<br>\n";

if (exists $colors{purple}) {
    print "Sorry, the color purple is already in the hash.<br>\n";
} else {
    $colors{purple} = "#ff00ff";
}

delete $colors{purple};

foreach my $color (sort(keys %colors)) {
    print qq(<font color="$colors{$color}">$color</font>\n);
}

%colors = ();

print "<br>\n";

print "Colors:<br>\n";

foreach my $color (sort(keys %colors)) {
    print qq(<font color="$colors{$color}">$color</font>\n);
}

my %colors = (red => "#ff0000", green=> "#00ff00",
              blue => "#0000ff",	black => "#000000",
              white => "#ffffff" );

my @keyslice = keys %colors;
my @valueslice = values %colors;

print "@keyslice<br>\n";
print "@valueslice<br>\n";

my $empty = scalar(%colors);
my $size = scalar keys(%colors);
print "$empty<br>\n";
print "$size<br>\n";

print end_html;
