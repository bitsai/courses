#!/usr/local/bin/perl -wT

# Do not use this script!  It has no security holes,
# but it's bad programming practice.
# It should have the -w switch, use strict,
# and probably use the -T switch (not necessary
# in this example, but a good habit to get into)

print "Content-type: text/html\n\n";
print "<html><head><title>My First CGI Script</title></head>";
print "<body bgcolor=\"#ffffcc\">";
print "<h1>This is a pretty lame Web page</h1>";
print "<p>";
print "Who is this Ovid guy, anyway?";
print "</body></html>";
