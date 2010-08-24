#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);

print header;
print start_html;

my @colors = param('color');
foreach my $color (@colors) {
   print "You picked $color.<br>\n";
}

print end_html;
