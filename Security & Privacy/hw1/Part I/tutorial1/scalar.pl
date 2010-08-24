#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;

my $email = 'fnord@cgi101.com';
my $url = "http://www.cgi101.com";

print header;
print start_html("Scalars");
print <<EndHTML;
<h2>Hello</h2>
<p>
My e-mail address is $email, and my web url is
<a href="$url">$url</a>.
</p>
EndHTML

print end_html;
