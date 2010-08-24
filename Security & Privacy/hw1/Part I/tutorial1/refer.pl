#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

print header;
print start_html("Referring Page");
print "Welcome, I see you've just come from 
$ENV{HTTP_REFERER}!<p>\n";

print end_html;
