#!/usr/local/bin/perl -wT
use CGI;
$cgi = CGI->new;
print $cgi->header;
print $cgi->start_html(-title=>"Hello World", -bgcolor=>"#cccccc", -text=>"#999999");
print $cgi->h2("Hello, world!");
print $cgi->end_html;
