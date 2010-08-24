#!/usr/local/bin/perl -w

#
# This program does the same thing as tcp_client.pl but instead uses
# the classes defined in the LWP package.
#
# Example:
#
#    lwp_client.pl http://www.cs.jhu.edu
#
# Note: you must use a command line argument of http://some.web.address
#       or else the program will fail with error code 404 (document not
#       found).

use strict;

use HTTP::Request;
use HTTP::Response;
use LWP::UserAgent;

my $ROBOT_NAME = 'BTsaiBot/1.0';
my $ROBOT_MAIL = 'btsai@cs.jhu.edu';


my $ua = new LWP::UserAgent;  # create an new LWP::UserAgent
$ua->agent( $ROBOT_NAME );    # identify who we are
$ua->from ( $ROBOT_MAIL );    # and give an email address in case anyone would
                              # like to complain

#
# create a request object associating the 'GET' method with the URL we
# want to connect to. Then have the UserAgent contact the web server and
# return the server's response.
#

my $request  = new HTTP::Request 'GET' => "$ARGV[0]";
my $response = $ua->request( $request );

# 
# print out the header information and the unmodified content
# of the requested page.
#

print $response->headers_as_string, "\n";
print $response->content;
