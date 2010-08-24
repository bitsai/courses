#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);

print header;
print start_html;

my @colors = ("red", "green", "blue", "gold");
foreach my $color (@colors) {
   if (param($color)) {
      print "You picked $color.<br>\n";
   }
}

print end_html;
