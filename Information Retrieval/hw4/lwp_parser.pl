#!/usr/local/bin/perl -w

#
# This program extracts all the image links from the web page given on 
# the command line and prints them to STDOUT
#
# Example:
#
#    lwp_client.pl http://www.cs.jhu.edu/~jkloss/index.shtml
#
# Note: you must use a command line argument of http://some.web.address
#       or else the program will fail with error code 404 (document not
#       found).

use strict;

use HTML::Element;
use HTML::Parser;
use HTML::TreeBuilder;
use HTTP::Request;
use HTTP::Response;
use LWP::UserAgent;
use URI::URL;

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
# create an HTML::TreeBuilder object and build a parse tree from
# the retrieved HTML document
#

my $html_tree = new HTML::TreeBuilder;
$html_tree->parse( $response->content );

#
# extract all image links from the returned HTML pages and print them
# to STDOUT.
#
#   $html_tree->extract_links( "img" )
#
#      to extract links from a TreeBuilder object you pass to the
#      &extract_links method an array containg the start tags of
#      the links you are looking for.
#
#      Example:
#
#          $html_tree->extract_links( "img" )
#
#      Returns all links of the form
#
#          <img src="../images/some_picture.gif">
#
#   (new URI::URL $link)->abs( $response->base )
#
#      used to convert a relative link (such as "~jkloss/index.html") into
#      a fully qualified link (such as "http://www.cs.jhu.edu/~jkloss/")
#      which may be used by an HTTP::Request object. The method
#
#        $reponse->base 
#
#      returns the base URL of the $response object (an HTTP::Response).
#
#        (new URI::URL $link)->abs( $response->base )
#    
#      prepends this base to the current $link.
#

foreach my $item (@{ $html_tree->extract_links( "img" )}) {

    my $link = shift @$item;
    my $furl = (new URI::URL $link)->abs( $response->base );

    print $furl, "\n";
}

#
# delete the parse tree once we are done with it (otherwise perl's
# garbage collector will not free memory due to circular references
# created in the parse tree).
#

$html_tree->delete( );
