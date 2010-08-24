#!/usr/local/bin/perl -wT
use CGI qw(:standard Vars);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

print header;
print start_html("Test Form");

my %form = Vars();
foreach my $p (sort(keys(%form))) {
    $form{$p} = param($p);
    print "$p = $form{$p}<br>\n";
}
print end_html;
