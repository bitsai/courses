#!/usr/local/bin/perl -w

#
# This program walks through HTML pages, extracting all the links to other
# text/html pages and then walking those links. Basically the robot performs
# a breadth first search through an HTML directory structure.
#
# All other functionality must be implemented
#
# Example:
#
#    robot_base.pl mylogfile.log content.txt http://www.cs.jhu.edu/
#
# Note: you must use a command line argument of http://some.web.address
#       or else the program will fail with error code 404 (document not
#       found).

use strict;
use Carp;

use HTML::LinkExtor;
use HTTP::Request;
use HTTP::Response;
use HTTP::Status;
use LWP::RobotUA;
use URI::URL;

URI::URL::strict( 1 );	# insure that we only traverse well formed URL's
$| = 1;	# force flush on output channel

my $log_file = shift (@ARGV);
my $content_file = shift (@ARGV);

if ((!defined ($log_file)) || (!defined ($content_file))) 
{
	print STDERR "You must specify a log file, a content file and a base_url\n";
	print STDERR "when running the web robot:\n";
	print STDERR "robot_base.pl mylogfile.log content.txt base_url\n";
	exit (1);
}

open LOG, ">$log_file";
open CONTENT, ">$content_file";

############################################################
##               PLEASE CHANGE THESE DEFAULTS             ##
############################################################

# I don't want to be flamed by web site administrators for
# the lousy behavior of your robots.

my $ROBOT_NAME = 'BTsaiBot/1.0';
my $ROBOT_MAIL = 'btsai@cs.jhu.edu';

# create an instance of LWP::RobotUA.
#
# Note: you _must_ include a name and email address during construction
#       (web site administrators often times want to know who to bitch at
#       for intrusive bugs).
#
# Note: the LWP::RobotUA delays a set amount of time before contacting a
#       server again. The robot will first contact the base server (www.
#       servername.tag) to retrieve the robots.txt file which tells the
#       robot where it can and can't go. It will then delay. The default
#       delay is 1 minute (which is what I am using). You can change this
#       with a call of
#
#         $robot->delay( $ROBOT_DELAY_IN_MINUTES );
#
#       At any rate, if your program seems to be doing nothing, wait for
#       at least 60 seconds (default delay) before concluding that some-
#       thing is wrong.

my $robot = new LWP::RobotUA $ROBOT_NAME, $ROBOT_MAIL;
$robot->delay(0.02);

my $base_url = shift(@ARGV);   # the root URL we will start from
$base_url =~ /http:\/\/(www\.){0,1}([^\/]*).*/;
my $domain = $2;
print "\nDOMAIN: $domain\n";

my @search_urls = ();    # current URL's waiting to be trapsed
my @wanted_urls = ();    # URL's which contain info that we are looking for
my %relevance   = ();    # how relevant is a particular URL to our search
my %pushed      = ();    # URL's which have either been visited or are already
                         # on the @search_urls array

push @search_urls, $base_url;
$pushed{$base_url} = 1;

while (@search_urls) 
{
	# insure that the URL is well-formed, otherwise skip it
	# if not or something other than HTTP

	my $url = shift @search_urls;
	my $parsed_url = eval { new URI::URL $url; };

	print "\nVisiting: ", $url, "\n";

	next if $@;
	next if $parsed_url->scheme !~/http/i;

	# get header information on URL to see it's status (exis-
	# tant, accessible, etc.) and content type. If the status
	# is not okay or the content type is not what we are
	# looking for skip the URL and move on

	print LOG "[HEAD ] $url\n";

	my $request  = new HTTP::Request HEAD => $url;
	my $response = $robot->request( $request );

	next if $response->code != RC_OK;
	next if !(&wanted_content( $url, $response->content_type ));

	print LOG "[GET  ] $url\n";

	$request->method( 'GET' );
	$response = $robot->request( $request );

	next if $response->code != RC_OK;
	next if $response->content_type !~ m@text/html@;

	print LOG "[LINKS] $url\n";

	&extract_content ($response->content, $url);
	my @related_urls  = &grab_urls( $response->content );

	foreach my $link (@related_urls) 
	{
		my $full_url = eval { (new URI::URL $link, $response->base)->abs; };
		delete $relevance{ $link } and next if $@;

		if (defined $full_url)
		{
			if ($full_url =~ /$url#.*/)	# Self-referential links
			{
#				print "*SR ".$full_url."\n";
				next;
			}

			if ($full_url !~ /\.$domain/)	# Non-local links
			{
#				print "*NL ".$full_url."\n"
				next;
			}
		}

		$relevance{ $full_url } = $relevance{ $link };
		delete $relevance{ $link } if $full_url ne $link;

		push @search_urls, $full_url and $pushed{ $full_url } = 1
	    	if ! exists $pushed{ $full_url };
	}

	# reorder the urls base upon relevance so that we search
	# areas which seem most relevant to us first.

	@search_urls = sort { $relevance{ $a } <=> $relevance{ $b }; } @search_urls;
}

close LOG;
close CONTENT;

exit (0);

# wanted_content
#
#  this function should check to see if the current URL content
#  is something which is either
#
#    a) something we are looking for (e.g. postscript, pdf,
#       plain text, or html). In this case we should save the URL in the
#       @wanted_urls array.
#
#    b) something we can traverse and search for links
#       (this can be just text/html).

sub wanted_content 
{
	my $url = shift;
	my $content = shift;

#	print "Content Type: ".$content."\n";
	
	if ($content =~ m@application/postscript@)
	{
		print "PS Link: ".$url."\n";
		print LOG "[PS   ] $url\n";
		push @wanted_urls, $url; 
	}
	
	if ($content =~ m@application/pdf@)
	{
		print "PDF Link: ".$url."\n";
		print LOG "[PDF  ] $url\n";
		push @wanted_urls, $url;
	}

	return $content =~ m@text/html@;
}

# extract_content
#
#  this function should read through the context of all the text/html
#  documents retrieved by the web robot and extract three types of
#  contact information described in the assignment

sub extract_content 
{
	my $content = shift;
	my $url = shift;
	my $copy_content1 = $content;
	my $copy_content2 = $content;
	my $copy_content3 = $content;

	my $phone;
	my $email;
	my $address;

	# parse out information you want
	# print it in the tuple format to the CONTENT and LOG files, for example:

	while ($copy_content1 =~ s/(\w+@\w+\.\w+(\.\w+){0,1})//)
	{
		$email = $1;
		print CONTENT "($url; EMAIL; $email)\n";
		print LOG "($url; EMAIL; $email)\n";
		print "EMAIL: ", $email, "\n";
	}

	while ($copy_content2 =~ s/\D((\d{3}){0,1}(\(\d{3}\)){0,1}\D\d{3}\D\d{4})\D//)
	{
		$phone = $1;
		print CONTENT "($url; PHONE; $phone)\n";
		print LOG "($url; PHONE; $phone)\n";
		print "PHONE: ", $phone, "\n";
	}

	while ($copy_content3 =~ s/([A-Za-z]+,{0,1}\s[A-Za-z]+,{0,1}\s\d{5}(.\d{4}){0,1})//)
	{
		$address = $1;
		print CONTENT "($url; ADDRESS; $address)\n";
		print LOG "($url; ADDRESS; $address)\n";
		print "ADDRESS: ", $address, "\n";
	}

	return;
}

# grab_urls
#
#   this function parses through the content of a passed HTML page and
#   picks out all links and any immediately related text.
#
#   Example:
#
#     given
#
#       <a href="somepage.html">This is some web page</a>
#
#     the link "somepage.html" and related text "This is some web page"
#     will be parsed out. However, given
#
#       <a href="anotherpage.html"><img src="image.jpg">
#
#       Further text which does not relate to the link . . .
#
#     the link "anotherpage.html" will be parse out but the text "Further
#     text which . . . " will be ignored.
#
#   Relevancy based on both the link itself and the related text should
#   be calculated and stored in the %relevance hash
#
#   Example:
#
#      $relevance{ $link } = &your_relevance_method( $link, $text );

sub grab_urls 
{
	my $content = shift;
	my %urls    = ();		# NOTE: this is an associative array so that we only
					# push the same "href" value once.

	skip:

	while ($content =~ s/<\s*[aA] ([^>]*)>\s*(?:<[^>]*>)*(?:([^<]*)(?:<[^aA>]*>)*<\/\s*[aA]\s*>)?//) 
	{
		my $tag_text = $1;
		my $reg_text = $2;

		if (defined $reg_text) 
		{
			$reg_text =~ s/[\n\r]/ /;
			$reg_text =~ s/\s{2,}/ /;
		}

		my $link = "";
		$reg_text = "" if (!defined $reg_text);

		if ($tag_text =~ /href\s*=\s*(?:["']([^"']*)["']|([^\s])*)/i) 
		{
			$link = $1 || $2;
			$link = "" if (!defined $link);

			# okay, the same link may occur more than once in a
			# document, but currently I only consider the last
			# instance of a particular link

			$relevance{ $link } = &compute_relevance( $link, $reg_text );
			$urls{ $link }      = 1;
		}

		# print $reg_text, "\n" if defined $reg_text;
		# print $link, "\n";
	}

	return keys %urls;	# the keys of the associative array hold all the
					# links we've found (no repeats).
}

# compute_relevance

sub compute_relevance
{
	my $link = shift;
	my $text = shift;

#	print "URL: ".$link."\n";
#	print "TEXT: ".$text."\n";

	if ($link =~ /~\w+$/)
	{ return 1; }
	if ($link =~ /homepage/ and $text =~ /homepage/)
	{ return 2; }
	if ($link =~ /homepage/ or $text =~ /homepage/)
	{ return 3; }
	if ($link =~ /people/ and $text =~ /people/)
	{ return 4; }
	if ($link =~ /people/ or $text =~ /people/)
	{ return 5; }

	return 6;
}
