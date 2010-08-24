#!/usr/local/bin/perl -w

#
# This program extracts all the image links from the web page given on
# the command line and prints them to STDOUT. This program is the same
# as the lwp_parser.pl program except that it use the package HTML::LinkExtor
# to extract links. This class gives you more control in parsing links
#
# Example:
#
#    lwp_client.pl http://www.cs.jhu.edu/~jkloss/index.shtml
#
# Note: you must use a command line argument of http://some.web.address
#       or else the program will fail with error code 404 (document not
#       found).

use strict;
use HTML::LinkExtor;
use HTTP::Request;
use HTTP::Response;
use LWP::UserAgent;
use URI::URL;

my $ROBOT_NAME = 'BTsaiBot/1.0';
my $ROBOT_MAIL = 'btsai@cs.jhu.edu';

my $ua = new LWP::UserAgent;	# create an new LWP::UserAgent
$ua->agent( $ROBOT_NAME );	# identify who we are
$ua->from ( $ROBOT_MAIL );	# and give an email address in case anyone would
					# like to complain

my %images = ( );	# array to store found image links
my %links = ( );	# array to store found links

# call back routine used by the HTML::LinkExtor object to parse passed input lines

sub call_back
{
	my ($tag, %attribs,  ) = @_;	# the <"TAG" ... > value parsed out
						# the attribute which follow $tag

	# currently we only retrieve and parse "img" tags
	#
	# a more fine grained approach to parsing could be implemented
	# here.

	$images{ $attribs{ 'src' } } = 1 if $tag eq "img";
	$links{ $attribs{ 'href' } } = 1 if $tag eq "a";
}

my $extract = new HTML::LinkExtor( \&call_back );

# create a request object associating the 'GET' method with the URL we
# want to connect to. Then have the UserAgent contact the web server and
# return the server's response.
#
# Note: during the parsing of $response the sub reference will call
#       $extract->parse( ) for each buffered line read. $extract will
#       then call $call_back (from &parse) and extact image links.
#

my $request  = new HTTP::Request 'GET' => "$ARGV[0]";
my $response = $ua->request( $request, sub{ $extract->parse( $_[0] ) });
my $base     = $response->base;

$base =~ /http:\/\/www\.([^\/]*).*/;
my $domain = $1;
print "Domain: ".$domain."\n";

# translate each link to a fully qualified URL

%images = map { $_ = url($_, $base)->abs; } %images;
%links = map { $_ = url($_, $base)->abs; } %links;

# discard self-referencing links
while ((my $key, my $val) = each %links)
{
	if ($key =~ /$base#.*/)
	{
#		print "*SR ".$key."\n";
		delete $links{$key};
	}
}

# discard non-local links
while ((my $key, my $val) = each %links)
{
	if ($key !~ /\.$domain/)
	{
#		print "*NL ".$key."\n";
		delete $links{$key};
	}
}

print "\n";

while ((my $key, my $val) = each %images)
{ print $key."\n"; }

print "\n";

while ((my $key, my $val) = each %links)
{ print $key."\n"; }
