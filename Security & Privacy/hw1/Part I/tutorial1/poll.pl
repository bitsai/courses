#!/usr/local/bin/perl -wT
use CGI qw(:standard);
use CGI::Carp qw(warningsToBrowser fatalsToBrowser);
use strict;
use Fcntl qw(:flock :seek);

my $outfile = "poll.out";

# only record the vote if they actually picked something
if (param('pick')) {
   open(OUT, ">>$outfile") or &dienice("Couldn't open $outfile: $!");
   flock(OUT, LOCK_EX);      # set an exclusive lock 
   seek(OUT, 0, SEEK_END);   # then seek the end of file
   print OUT param('pick'),"\n";
   close(OUT);
} else {
# this is optional, but if they didn't vote, you might 
# want to tell them about it...
   &dienice("You didn't pick anything!");
}

# redirect to the results.cgi. 
# (Change to your own URL...)
print redirect("http://hops.cs.jhu.edu/cgi-bin/cgiwrap/~btsai/results.pl");

sub dienice {
    my($msg) = @_;
    print header;
    print start_html("Error");
    print h2("Error");
    print $msg;
    print end_html;
    exit;
}
