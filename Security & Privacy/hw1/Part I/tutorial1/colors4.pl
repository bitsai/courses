#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);

print header;
print start_html;

my $color = param('color');
print "You picked $color.<br>\n";

print end_html;
