#!/usr/local/bin/perl -wT
use strict;
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);

my %colors = (  "red", "\#ff0000",
                "green", "\#00ff00",
                "blue", "\#0000ff",
                "gold", "\#cccc00");

print header;
print start_html(-bgcolor=>$colors{param('color')});

my $color = param('color');
print "You picked $color.<br>\n";

print end_html;
