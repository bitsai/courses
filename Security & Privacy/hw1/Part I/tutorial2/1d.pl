#!/usr/local/bin/perl -wT
use strict;
use CGI;

my $query = new CGI;
my $time = localtime;

print $query->header( "text/html" );
print <<END_HERE;
<html><head><title>My First CGI Script</title></head>
<body bgcolor="#ffffcc">

<h1>This is a pretty lame Web page</h1>
<p>
$time
</body></html>
END_HERE
