#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
# This is a comment
# So is this
#
# Comments are useful for telling the reader
# what's happening. This is important if you
# write code that someone else will have to
# maintain later.
print header;		# here's a comment. print the header
print start_html("Hello World");
print "<h2>Hello, world!</h2>\n";
print end_html;	# print the footer
# the end.